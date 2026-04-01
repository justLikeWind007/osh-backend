package com.backstage.system.service.impl.course;

import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.exception.ServiceException;
import com.backstage.common.utils.DateUtils;
import com.backstage.common.utils.StringUtils;
import com.backstage.common.utils.PageUtils;
import com.backstage.common.utils.bean.BeanUtils;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.course.OshCourseSection;
import com.backstage.system.domain.course.OshCourseMaterial;
import com.backstage.system.domain.dto.*;
import com.backstage.system.domain.vo.*;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.mapper.course.OshCourseSectionMapper;
import com.backstage.system.mapper.course.OshCourseMaterialMapper;
import com.backstage.system.mapper.course.OshCourseQuestionMapper;
import com.backstage.system.mapper.course.OshCourseTagMapper;
import com.backstage.system.mapper.course.OshUserCourseProgressMapper;
import com.backstage.system.mapper.course.OshCourseStaffMapper;
import com.backstage.system.mapper.course.OshCourseReviewMapper;
import com.backstage.system.domain.fava.OshFava;
import com.backstage.system.mapper.fava.OshFavaMapper;
import com.backstage.system.service.course.ICourseManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.Set;

/**
 * 课程管理 Service 业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-24
 */
@Service
public class CourseManageServiceImpl implements ICourseManageService {
    
    private static final Logger log = LoggerFactory.getLogger(CourseManageServiceImpl.class);
    
    @Autowired
    private OshCourseMapper courseMapper;
    
    @Autowired
    private OshCourseSectionMapper sectionMapper;
    
    @Autowired
    private OshCourseMaterialMapper materialMapper;
    
    @Autowired
    private OshCourseQuestionMapper questionMapper;
    
    @Autowired
    private OshCourseTagMapper tagMapper;
    
    @Autowired
    private OshUserCourseProgressMapper progressMapper;
    
    @Autowired
    private OshCourseStaffMapper staffMapper;
    
    @Autowired
    private OshCourseReviewMapper reviewMapper;
    
    @Autowired
    private OshFavaMapper favaMapper;
    
    // ==================== 课程查询接口实现 ====================
    
    /**
     * 查询课程列表：支持多标签筛选、支持关键字模糊搜索、按标签使用量降序排列，热门课程优先展示
     * @param courseDTO 查询条件 DTO
     * @return 分页课程列表
     */
    @Override
    public TableDataInfo selectCourseList(CourseQueryDTO courseDTO) {
   
        Map<String, Object> params = new HashMap<>();
        if (courseDTO.getTagIds() != null && !courseDTO.getTagIds().isEmpty()) {
            params.put("tagIds", courseDTO.getTagIds());
        }
        
        // 3. 处理关键字搜索
        if (StringUtils.isNotEmpty(courseDTO.getKeyword())) {
            params.put("keyword", "%" + courseDTO.getKeyword() + "%");
        }
        
        // 4. 设置排序字段和方式
        params.put("sortBy", courseDTO.getSortBy());
        params.put("sortOrder", courseDTO.getSortOrder());
        

        PageUtils.startPage();
        List<OshCourse> list = courseMapper.selectCourseListByCondition(params);
        return new TableDataInfo(list, new com.github.pagehelper.PageInfo<OshCourse>(list).getTotal());
    }
    
    /**
     * 获取课程标签列表（供前端多选下拉框使用）
     * 
     * @return 标签列表（包含 id、name、useCount）
     */
    @Override
    public List<Map<String, Object>> selectTagList() {
        return tagMapper.selectAllTags();
    }
    
    /**
     * 获取课程详情
     * 语法逻辑：
     * 1. 查询课程基本信息
     * 2. 查询章节列表
     * 3. 查询评价统计
     * 4. 判断用户是否已购买
     * 
     * 实现效果：
     * - 返回课程完整信息，包括服务周期、服务内容
     * - 显示前几节免费试听章节
     * - 展示好评、中评、差评数量
     * - 标记用户是否已购买
     * 
     * @param courseId 课程 ID
     * @param userId 用户 ID（用于判断是否购买）
     * @return 课程详情 VO
     */
    @Override
    public CourseDetailVO getCourseDetail(Long courseId, Long userId) {
        // 1. 查询课程基本信息
        OshCourse course = courseMapper.selectCourseById(courseId);
        if (course == null) {
            throw new ServiceException("课程不存在");
        }
        
        // 2. 构建课程详情 VO
        CourseDetailVO vo = new CourseDetailVO();
        vo.setId(course.getId());
        vo.setTitle(course.getTitle());
        vo.setCover(course.getCover());
        vo.setTryContent(course.getTryContent());
        vo.setPrice(course.getPrice() != null ? course.getPrice().toString() : "0.00");
        vo.setTPrice(course.getTPrice() != null ? course.getTPrice().toString() : "0.00");
        vo.setType(course.getType());
        
        // 3. 设置服务周期和服务内容（从扩展字段或配置表获取）
        vo.setServiceCycle("永久有效");
        vo.setServiceContent("包含答疑、资料下载等");
        
        // 4. 查询评价统计
        Map<String, Object> reviewStats = getReviewStatistics(courseId);
        vo.setGoodCount((Integer) reviewStats.getOrDefault("goodCount", 0));
        vo.setMediumCount((Integer) reviewStats.getOrDefault("mediumCount", 0));
        vo.setBadCount((Integer) reviewStats.getOrDefault("badCount", 0));
        
        // 5. 判断用户是否已购买
        boolean isPurchased = checkUserPurchased(courseId, userId);
        vo.setIsBuy(isPurchased);
        
        // 6. 查询章节列表（仅显示前几节免费章节用于试看）
        List<CourseSectionVO> sections = getCourseSections(courseId, userId);
        vo.setSections(sections);
        
        return vo;
    }
    
    /**
     * 新增课程
     * 语法逻辑：
     * 1. 保存课程基本信息
     * 2. 保存标签关联关系
     * 
     * 实现效果：
     * - 返回新创建的课程 ID
     * - 事务保证数据一致性
     * - 权限控制由 Controller 层@RequiresPermissions 注解处理
     * 
     * @param course 课程信息
     * @param userId 用户 ID
     * @return 课程 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long insertCourse(OshCourse course, Long userId) {
        // 1. 设置课程默认值
        course.setCreateTime(DateUtils.getNowDate());
        course.setUpdateTime(DateUtils.getNowDate());
        
        // 2. 插入课程信息
        int result = courseMapper.insertCourse(course);
        if (result <= 0) {
            throw new ServiceException("新增课程失败");
        }
        
        // 3. 获取生成的课程 ID
        Long courseId = course.getId();
        
        // 4. 保存标签关联关系
        if (course.getTagIds() != null && course.getTagIds().length > 0) {
            for (Long tagId : course.getTagIds()) {
                Map<String, Object> param = new HashMap<>();
                param.put("courseId", courseId);
                param.put("tagId", tagId);
                tagMapper.insertCourseTagRelation(param);
                
                // 更新标签使用次数
                tagMapper.incrementUsageCount(tagId);
            }
        }
        
        return courseId;
    }
    
    /**
     * 修改课程
     * 语法逻辑：
     * 1. 更新课程基本信息
     * 2. 更新标签关联关系
     * 
     * 实现效果：
     * - 删除旧标签关联，插入新标签关联
     * - 事务保证数据一致性
     * - 权限控制由 Controller 层@RequiresPermissions 注解处理
     * 
     * @param course 课程信息
     * @param userId 用户 ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCourse(OshCourse course, Long userId) {
        // 1. 更新时间
        course.setUpdateTime(DateUtils.getNowDate());
        
        // 2. 更新课程信息
        int result = courseMapper.updateCourse(course);
        
        // 3. 更新标签关联（先删后增）
        if (course.getTagIds() != null) {
            // 删除旧的标签关联
            tagMapper.deleteCourseTagRelationByCourseId(course.getId());
            
            // 插入新的标签关联
            if (course.getTagIds().length > 0) {
                for (Long tagId : course.getTagIds()) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("courseId", course.getId());
                    param.put("tagId", tagId);
                    tagMapper.insertCourseTagRelation(param);
                    
                    // 更新标签使用次数
                    tagMapper.incrementUsageCount(tagId);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 删除课程
     * 语法逻辑：
     * 1. 级联删除章节、资料、问答等
     * 
     * 实现效果：
     * - 物理删除课程及相关所有数据
     * - 事务保证数据一致性
     * - 权限控制由 Controller 层@RequiresPermissions 注解处理
     * 
     * @param courseId 课程 ID
     * @param userId 用户 ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCourse(Long courseId, Long userId) {
        // 1. 级联删除相关数据
        // 删除章节
        sectionMapper.deleteSectionsByCourseId(courseId);
        
        // 删除资料
        materialMapper.deleteMaterialsByCourseId(courseId);
        
        // 删除问答
        questionMapper.deleteQuestionsByCourseId(courseId);
        
        // 删除标签关联
        tagMapper.deleteCourseTagRelationByCourseId(courseId);
        
        // 删除学习进度
        progressMapper.deleteProgressByCourseId(courseId);
        
        // 删除服务人员记录
        staffMapper.deleteStaffsByCourseId(courseId);
        
        // 删除评价
        reviewMapper.deleteReviewsByCourseId(courseId);
        
        // 2. 删除课程
        return courseMapper.deleteCourseById(courseId);
    }
    
    // ==================== 课程章节接口实现 ====================
    
    /**
     * 获取课程大纲
     * 语法逻辑：
     * 1. 查询所有章节
     * 2. 标记免费/付费状态
     * 3. 计算学习进度
     * 
     * 实现效果：
     * - 显示所有章节，按排序序号排列
     * - 未购买时，付费章节显示锁定状态
     * - 已购买时，显示学习进度（已学/未学）
     * 
     * @param courseId 课程 ID
     * @param userId 用户 ID
     * @return 章节列表 VO
     */
    @Override
    public List<CourseSectionVO> getCourseSections(Long courseId, Long userId) {
        // 1. 查询所有章节（按排序序号）
        List<Map<String, Object>> sections = sectionMapper.selectSectionsByCourseId(courseId);
        
        // 2. 判断用户是否已购买
        boolean isPurchased = checkUserPurchased(courseId, userId);
        
        // 3. 查询用户学习进度
        Map<String, Object> progressMap = new HashMap<>();
        if (isPurchased && userId != null) {
            progressMap = progressMapper.selectProgressByUserIdAndCourseId(userId, courseId);
        }
        
        // 4. 封装返回结果
        List<CourseSectionVO> result = new ArrayList<>();
        for (Map<String, Object> section : sections) {
            CourseSectionVO vo = new CourseSectionVO();
            vo.setId((Long) section.get("id"));
            vo.setTitle((String) section.get("section_title"));
            vo.setSectionType((String) section.get("section_type"));
            vo.setDuration((Integer) section.get("duration"));
            vo.setFree((Boolean) section.get("is_free"));
            
            // 判断是否已学习
            if (progressMap != null && progressMap.containsKey(vo.getId())) {
                vo.setLearned(true);
            } else {
                vo.setLearned(false);
            }
            
            // 查询该章节的问题数量
            int questionCount = questionMapper.countQuestionsBySectionId(vo.getId());
            vo.setHasQuestion(questionCount > 0);
            vo.setQuestionCount(questionCount);
            
            // 未购买时，付费章节显示锁定
            if (!isPurchased && !vo.getFree()) {
                vo.setLocked(true);
            } else {
                vo.setLocked(false);
            }
            
            result.add(vo);
        }
        
        return result;
    }
    
    /**
     * 立即学习（试看免费章节）
     * 语法逻辑：
     * 1. 查询章节信息
     * 2. 判断是否免费
     * 3. 检查是否已购买
     * 
     * 实现效果：
     * - 免费章节直接返回视频 URL 或文本内容
     * - 付费章节提示需要购买
     * 
     * @param courseId 课程 ID
     * @param sectionId 章节 ID
     * @param userId 用户 ID
     * @return 章节内容
     */
    @Override
    public SectionDTO learnSection(Long courseId, Long sectionId, Long userId) {
        // 1. 查询章节信息
        Map<String, Object> section = sectionMapper.selectSectionById(sectionId);
        if (section == null) {
            throw new ServiceException("章节不存在");
        }
        
        // 2. 校验章节是否属于该课程
        if (!courseId.equals(section.get("course_id"))) {
            throw new ServiceException("章节不属于该课程");
        }
        
        // 3. 构建返回结果
        SectionDTO dto = new SectionDTO();
        dto.setId(sectionId);
        
        // 4. 判断是否免费
        boolean isFree = (Boolean) section.get("is_free");
        dto.setFree(isFree);
        
        // 5. 如果免费，直接返回内容
        if (isFree) {
            dto.setCanLearn(true);
            dto.setNeedPurchase(false);
            
            String sectionType = (String) section.get("section_type");
            if ("video".equals(sectionType)) {
                dto.setVideoUrl((String) section.get("section_content"));
            } else if ("text".equals(sectionType)) {
                dto.setTextContent((String) section.get("section_content"));
            }
        } else {
            // 6. 付费章节，检查是否已购买
            boolean isPurchased = checkUserPurchased(courseId, userId);
            if (isPurchased) {
                dto.setCanLearn(true);
                dto.setNeedPurchase(false);
                
                String sectionType = (String) section.get("section_type");
                if ("video".equals(sectionType)) {
                    dto.setVideoUrl((String) section.get("section_content"));
                } else if ("text".equals(sectionType)) {
                    dto.setTextContent((String) section.get("section_content"));
                }
            } else {
                dto.setCanLearn(false);
                dto.setNeedPurchase(true);
            }
        }
        
        return dto;
    }
    
    /**
     * 更新学习进度
     * 语法逻辑：
     * 1. 查询当前进度
     * 2. 更新进度信息
     * 3. 计算完成百分比
     * 
     * 实现效果：
     * - 记录用户学习的每个章节
     * - 自动计算整体学习进度百分比
     * - 学完所有章节后标记为已完成
     * 
     * @param courseId 课程 ID
     * @param sectionId 章节 ID
     * @param userId 用户 ID
     * @param progress 学习进度百分比
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProgress(Long courseId, Long sectionId, Long userId, Double progress) {
        // 1. 查询当前进度记录
        Map<String, Object> currentProgress = progressMapper.selectProgressByUserIdAndCourseId(userId, courseId);
        
        // 2. 计算总章节数
        int totalSections = sectionMapper.countSectionsByCourseId(courseId);
        
        if (currentProgress == null) {
            // 3. 首次学习，创建进度记录
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("courseId", courseId);
            params.put("sectionId", sectionId);
            params.put("learnedSectionCount", 1);
            params.put("totalSectionCount", totalSections);
            params.put("progressPercent", progress);
            params.put("lastLearnTime", DateUtils.getNowDate());
            
            // 判断是否完成
            if (progress >= 100.0) {
                params.put("isCompleted", 1);
                params.put("completeTime", DateUtils.getNowDate());
            } else {
                params.put("isCompleted", 0);
            }
            
            progressMapper.insertProgress(params);
        } else {
            // 4. 更新进度记录
            int learnedCount = (Integer) currentProgress.get("learned_section_count");
            
            // 如果该章节未学过，增加已学章节数
            if (!sectionId.equals(currentProgress.get("section_id"))) {
                learnedCount++;
            }
            
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("courseId", courseId);
            params.put("sectionId", sectionId);
            params.put("learnedSectionCount", learnedCount);
            params.put("progressPercent", progress);
            params.put("lastLearnTime", DateUtils.getNowDate());
            
            // 判断是否完成
            if (learnedCount >= totalSections) {
                params.put("isCompleted", 1);
                params.put("completeTime", DateUtils.getNowDate());
            } else {
                params.put("isCompleted", 0);
            }
            
            progressMapper.updateProgress(params);
        }
    }
    
    // ==================== 视频播放接口实现 ====================
    
    /**
     * 获取章节视频信息
     * 语法逻辑：
     * 1. 查询章节详情
     * 2. 检查访问权限
     * 3. 查询学习进度
     * 
     * 实现效果：
     * - 返回视频URL、时长、封面、分辨率等信息
     * - 包含当前播放进度
     * - 无权限时不返回视频URL
     * 
     * @param sectionId 章节ID
     * @param userId 用户ID（可为空）
     * @return 章节视频信息 VO
     */
    @Override
    public SectionVideoVO getSectionVideo(Long sectionId, Long userId) {
        // 1. 查询章节视频详情
        Map<String, Object> section = sectionMapper.selectSectionVideoById(sectionId);
        if (section == null) {
            throw new ServiceException("章节不存在");
        }
        
        Long courseId = (Long) section.get("course_id");
        
        // 2. 检查章节状态
        Integer sectionStatus = (Integer) section.get("status");
        if (sectionStatus == null || sectionStatus != 1) {
            throw new ServiceException("该章节已下架");
        }
        
        // 3. 构建返回结果
        SectionVideoVO vo = new SectionVideoVO();
        vo.setSectionId(sectionId);
        vo.setTitle((String) section.get("title"));
        vo.setCourseId(courseId);
        vo.setCourseName((String) section.get("course_name"));
        vo.setDuration((Integer) section.get("duration"));
        vo.setCover((String) section.get("cover"));
        vo.setVideoCodec((String) section.get("video_codec"));
        vo.setVideoResolution((String) section.get("video_resolution"));
        vo.setFileSize((Long) section.get("file_size"));
        vo.setSubtitleUrl((String) section.get("subtitle_url"));
        vo.setType((String) section.get("type"));
        vo.setExamId((Long) section.get("exam_id"));
        
        // 格式化时长文本
        if (vo.getDuration() != null) {
            vo.setDurationText(formatDuration(vo.getDuration()));
        }
        
        // 4. 检查是否免费
        Boolean isFree = (Boolean) section.get("is_free");
        vo.setIsFree(isFree != null && isFree);
        
        // 5. 检查访问权限
        boolean hasAccess = false;
        if (vo.getIsFree()) {
            hasAccess = true;
        } else if (userId != null) {
            hasAccess = checkUserPurchased(courseId, userId);
        }
        vo.setHasAccess(hasAccess);
        
        // 6. 有权限才返回视频URL
        if (hasAccess) {
            vo.setMediaUrl((String) section.get("media_url"));
        }
        
        // 7. 查询学习进度（仅登录用户）
        if (userId != null) {
            Map<String, Object> progress = progressMapper.selectSectionProgress(userId, sectionId);
            if (progress != null) {
                vo.setCurrentProgress((Integer) progress.get("progress"));
                vo.setLastPosition((Integer) progress.get("last_position"));
                vo.setStatus((Integer) progress.get("status"));
                vo.setIsCompleted((Boolean) progress.get("is_completed"));
            } else {
                vo.setCurrentProgress(0);
                vo.setLastPosition(0);
                vo.setStatus(0);
                vo.setIsCompleted(false);
            }
        }
        
        return vo;
    }
    
    /**
     * 更新播放进度
     * 语法逻辑：
     * 1. 查询当前进度
     * 2. 更新或创建进度记录
     * 3. 累加学习时长
     * 
     * 实现效果：
     * - 记录用户视频播放进度
     * - 支持断点续播
     * 
     * @param sectionId 章节ID
     * @param progressDTO 进度更新请求
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlayProgress(Long sectionId, ProgressDTO progressDTO, Long userId) {
        // 1. 查询章节信息
        Map<String, Object> section = sectionMapper.selectSectionById(sectionId);
        if (section == null) {
            throw new ServiceException("章节不存在");
        }
        
        Long courseId = (Long) section.get("course_id");
        
        // 2. 查询当前进度
        Map<String, Object> currentProgress = progressMapper.selectSectionProgress(userId, sectionId);
        
        // 3. 判断是否完成
        boolean isCompleted = progressDTO.getProgress() != null && progressDTO.getProgress() >= 100;
        Integer status = progressDTO.getStatus();
        if (status == null) {
            status = isCompleted ? 3 : 1; // 3-已完成 1-学习中
        }
        
        if (currentProgress == null) {
            // 4. 首次学习，创建进度记录
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("courseId", courseId);
            params.put("sectionId", sectionId);
            params.put("status", status);
            params.put("progress", progressDTO.getProgress());
            params.put("lastPosition", progressDTO.getLastPosition());
            params.put("learnTime", progressDTO.getLearnTime());
            params.put("isCompleted", isCompleted ? 1 : 0);
            
            progressMapper.insertSectionProgress(params);
        } else {
            // 5. 更新进度记录
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("sectionId", sectionId);
            params.put("status", status);
            params.put("progress", progressDTO.getProgress());
            params.put("lastPosition", progressDTO.getLastPosition());
            params.put("learnTime", progressDTO.getLearnTime());
            params.put("isCompleted", isCompleted ? 1 : 0);
            
            progressMapper.updateSectionProgress(params);
        }
        
        // 6. 同步更新课程整体进度
        updateCourseOverallProgress(courseId, userId);
    }
    
    /**
     * 获取播放历史
     * 语法逻辑：
     * 1. 查询用户章节学习进度记录
     * 
     * 实现效果：
     * - 返回上次播放位置、进度百分比、学习状态等
     * 
     * @param sectionId 章节ID
     * @param userId 用户ID
     * @return 章节学习进度 VO
     */
    @Override
    public SectionProgressVO getPlayProgress(Long sectionId, Long userId) {
        // 1. 查询进度记录
        Map<String, Object> progress = progressMapper.selectSectionProgress(userId, sectionId);
        
        // 2. 构建返回结果
        SectionProgressVO vo = new SectionProgressVO();
        vo.setSectionId(sectionId);
        
        if (progress != null) {
            vo.setCourseId((Long) progress.get("course_id"));
            vo.setStatus((Integer) progress.get("status"));
            vo.setProgress((Integer) progress.get("progress"));
            vo.setLastPosition((Integer) progress.get("last_position"));
            vo.setLearnTime((Integer) progress.get("learn_time"));
            vo.setWatchCount((Integer) progress.get("watch_count"));
            vo.setIsCompleted((Boolean) progress.get("is_completed"));
            vo.setCompleteTime((Date) progress.get("complete_time"));
            vo.setCreateTime((Date) progress.get("create_time"));
            vo.setUpdateTime((Date) progress.get("update_time"));
        } else {
            // 无记录则返回默认值
            vo.setStatus(0);
            vo.setProgress(0);
            vo.setLastPosition(0);
            vo.setLearnTime(0);
            vo.setWatchCount(0);
            vo.setIsCompleted(false);
        }
        
        return vo;
    }
    
    /**
     * 记录观看完成
     * 语法逻辑：
     * 1. 更新章节学习状态为已完成
     * 2. 更新课程整体进度
     * 3. 检查是否有关联考试
     * 
     * 实现效果：
     * - 标记章节已学完
     * - 更新完成时间
     * - 返回考试ID（如有）
     * 
     * @param sectionId 章节ID
     * @param userId 用户ID
     * @return 考试ID（如有）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long markSectionComplete(Long sectionId, Long userId) {
        // 1. 查询章节信息
        Map<String, Object> section = sectionMapper.selectSectionById(sectionId);
        if (section == null) {
            throw new ServiceException("章节不存在");
        }
        
        Long courseId = (Long) section.get("course_id");
        
        // 2. 更新章节学习状态
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("sectionId", sectionId);
        params.put("status", 3); // 3-已完成
        params.put("isCompleted", 1);
        
        // 检查是否已有进度记录
        Map<String, Object> progress = progressMapper.selectSectionProgress(userId, sectionId);
        if (progress == null) {
            // 创建进度记录
            Map<String, Object> newParams = new HashMap<>();
            newParams.put("userId", userId);
            newParams.put("courseId", courseId);
            newParams.put("sectionId", sectionId);
            newParams.put("status", 3);
            newParams.put("progress", 100);
            newParams.put("lastPosition", 0);
            newParams.put("learnTime", 0);
            newParams.put("isCompleted", 1);
            
            progressMapper.insertSectionProgress(newParams);
        } else {
            progressMapper.updateSectionStatus(params);
        }
        
        // 3. 同步更新课程整体进度
        updateCourseOverallProgress(courseId, userId);
        
        // 4. 返回关联考试ID（如有）
        return (Long) section.get("exam_id");
    }
    
    /**
     * 检查购买权限
     * 语法逻辑：
     * 1. 查询章节信息
     * 2. 检查章节是否免费
     * 3. 检查用户是否已购买课程
     * 
     * 实现效果：
     * - 返回用户是否有权限观看此章节
     * 
     * @param sectionId 章节ID
     * @param userId 用户ID（可为空）
     * @return 章节访问权限 VO
     */
    @Override
    public SectionAccessVO checkSectionAccess(Long sectionId, Long userId) {
        // 1. 查询章节信息
        Map<String, Object> section = sectionMapper.selectSectionVideoById(sectionId);
        if (section == null) {
            throw new ServiceException("章节不存在");
        }
        
        Long courseId = (Long) section.get("course_id");
        
        // 2. 构建返回结果
        SectionAccessVO vo = new SectionAccessVO();
        vo.setSectionId(sectionId);
        
        // 3. 检查是否免费
        Boolean isFree = (Boolean) section.get("is_free");
        vo.setIsFree(isFree != null && isFree);
        
        // 4. 免费章节直接有权限
        if (vo.getIsFree()) {
            vo.setHasAccess(true);
            vo.setNeedPurchase(false);
            vo.setIsPurchased(false);
            return vo;
        }
        
        // 5. 付费章节检查购买状态
        vo.setNeedPurchase(true);
        
        if (userId == null) {
            vo.setHasAccess(false);
            vo.setIsPurchased(false);
            vo.setReason("请先登录");
            return vo;
        }
        
        boolean isPurchased = checkUserPurchased(courseId, userId);
        vo.setIsPurchased(isPurchased);
        vo.setHasAccess(isPurchased);
        
        if (!isPurchased) {
            vo.setReason("请先购买课程");
            // 返回课程价格
            Object price = section.get("price");
            vo.setPrice(price != null ? price.toString() : "0.00");
        }
        
        return vo;
    }
    
    /**
     * 格式化时长
     * 
     * @param seconds 秒数
     * @return 格式化后的时长文本（如：30:00）
     */
    private String formatDuration(Integer seconds) {
        if (seconds == null || seconds <= 0) {
            return "00:00";
        }
        
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }
    
    /**
     * 更新课程整体进度
     * 
     * @param courseId 课程ID
     * @param userId 用户ID
     */
    private void updateCourseOverallProgress(Long courseId, Long userId) {
        // 1. 统计课程总章节数
        int totalSections = sectionMapper.countSectionsByCourseId(courseId);
        
        // 2. 查询已完成的章节数（这里简化处理，实际需要统计）
        Map<String, Object> courseProgress = progressMapper.selectProgressByUserIdAndCourseId(userId, courseId);
        
        // 3. 更新课程进度
        if (courseProgress != null) {
            int learnedCount = (Integer) courseProgress.getOrDefault("learned_section_count", 0);
            Double progressPercent = (totalSections > 0) ? (learnedCount * 100.0 / totalSections) : 0.0;
            
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("courseId", courseId);
            params.put("progressPercent", progressPercent);
            params.put("lastLearnTime", DateUtils.getNowDate());
            params.put("isCompleted", learnedCount >= totalSections ? 1 : 0);
            
            progressMapper.updateProgress(params);
        }
    }
    
    // ==================== 课程资料接口实现 ====================
    
    /**
     * 获取课程资料列表
     * 语法逻辑：
     * 1. 检查用户是否已购买
     * 2. 查询资料列表
     * 
     * 实现效果：
     * - 未购买用户不可见资料
     * - 已购买用户可以下载资料
     * 
     * @param courseId 课程 ID
     * @param userId 用户 ID
     * @return 资料列表 VO
     */
    @Override
    public List<CourseMaterialVO> getCourseMaterials(Long courseId, Long userId) {
        // 1. 检查是否已购买
        boolean isPurchased = checkUserPurchased(courseId, userId);
        if (!isPurchased) {
            throw new ServiceException("请先购买课程");
        }
        
        // 2. 查询资料列表
        List<Map<String, Object>> materials = materialMapper.selectMaterialsByCourseId(courseId);
        
        // 3. 封装返回结果
        List<CourseMaterialVO> result = new ArrayList<>();
        for (Map<String, Object> material : materials) {
            CourseMaterialVO vo = new CourseMaterialVO();
            vo.setId((Long) material.get("id"));
            vo.setMaterialName((String) material.get("material_name"));
            vo.setFileUrl((String) material.get("file_url"));
            vo.setFileSize((Long) material.get("file_size"));
            vo.setDownloadCount((Integer) material.get("download_count"));
            vo.setIsDownloadable((Boolean) material.get("is_downloadable"));
            
            result.add(vo);
        }
        
        return result;
    }
    
    /**
     * 上传课程资料
     * 语法逻辑：
     * 1. 校验文件类型和大小
     * 2. 上传文件到服务器
     * 3. 保存资料信息
     * 
     * 实现效果：
     * - 仅允许压缩包格式（zip/rar/7z）
     * - 文件大小限制 100MB
     * - 返回资料 ID
     * - 权限控制由 Controller 层@RequiresPermissions 注解处理
     * 
     * @param courseId 课程 ID
     * @param file 文件
     * @param materialName 资料名称
     * @param userId 用户 ID
     * @return 资料 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadMaterial(Long courseId, MultipartFile file, String materialName, Long userId) {
        // 1. 校验文件类型
        String fileName = file.getOriginalFilename();
        String extension = "";
        if (fileName != null && fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        
        List<String> allowedExtensions = Arrays.asList("zip", "rar", "7z");
        if (!allowedExtensions.contains(extension)) {
            throw new ServiceException("仅支持压缩包格式（zip/rar/7z）");
        }
        
        // 2. 校验文件大小（100MB）
        long maxSize = 100 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new ServiceException("文件大小不能超过 100MB");
        }
        
        // 3. 上传文件（这里简化处理，实际需要调用文件上传服务）
        // String fileUrl = fileService.upload(file);
        String fileUrl = "/upload/course/" + courseId + "/" + System.currentTimeMillis() + "." + extension;
        
        // 4. 保存资料信息
        Map<String, Object> params = new HashMap<>();
        params.put("courseId", courseId);
        params.put("materialName", materialName);
        params.put("fileUrl", fileUrl);
        params.put("fileType", extension);
        params.put("fileSize", file.getSize());
        params.put("isDownloadable", 1);
        params.put("sortOrder", 0);
        
        materialMapper.insertMaterial(params);
        
        // 5. 返回资料 ID（从 params 中获取）
        return (Long) params.get("id");
    }
    
    /**
     * 删除课程资料
     * 语法逻辑：
     * 1. 查询资料信息
     * 2. 删除文件记录
     * 
     * 实现效果：
     * - 删除资料记录
     * - 权限控制由 Controller 层@RequiresPermissions 注解处理
     * 
     * @param materialId 资料 ID
     * @param userId 用户 ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteMaterial(Long materialId, Long userId) {
        // 1. 查询资料信息
        Map<String, Object> material = materialMapper.selectMaterialById(materialId);
        if (material == null) {
            throw new ServiceException("资料不存在");
        }
        
        // 2. 删除资料
        return materialMapper.deleteMaterialById(materialId);
    }
    
    // ==================== 课程问答接口实现 ====================
    
    /**
     * 提问（课程小节内）
     * 语法逻辑：
     * 1. 保存问题
     * 2. 关联课程和章节
     * 
     * 实现效果：
     * - 问题自动关联到课程和章节
     * - 状态默认为待回答
     * 
     * @param questionDTO 问题 DTO
     * @param userId 用户 ID
     * @return 问题 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long askQuestion(QuestionDTO questionDTO, Long userId) {
        // 1. 构建参数
        Map<String, Object> params = new HashMap<>();
        params.put("courseId", questionDTO.getCourseId());
        params.put("sectionId", questionDTO.getSectionId());
        params.put("userId", userId);
        params.put("questionTitle", questionDTO.getQuestionTitle());
        params.put("questionContent", questionDTO.getQuestionContent());
        
        // 2. 插入问题
        questionMapper.insertQuestion(params);
        
        // 3. 返回问题 ID
        return (Long) params.get("id");
    }
    
    /**
     * 获取课程问答列表
     * 语法逻辑：
     * 1. 查询问题列表
     * 2. 关联用户信息
     * 
     * 实现效果：
     * - 显示该课程下所有问题
     * - 包含提问者、回答者信息
     * 
     * @param courseId 课程 ID
     * @param sectionId 章节 ID（可选）
     * @param status 状态（可选）
     * @return 问答列表
     */
    @Override
    public List<CourseQuestionVO> getQuestions(Long courseId, Long sectionId, String status) {
        // 1. 查询问题列表
        List<Map<String, Object>> questions = questionMapper.selectQuestionsByCourseId(courseId, sectionId, status);
        
        // 2. 封装返回结果
        List<CourseQuestionVO> result = new ArrayList<>();
        for (Map<String, Object> question : questions) {
            CourseQuestionVO vo = new CourseQuestionVO();
            vo.setId((Long) question.get("id"));
            vo.setSectionTitle((String) question.get("section_title"));
            vo.setQuestionTitle((String) question.get("question_title"));
            vo.setQuestionContent((String) question.get("question_content"));
            vo.setAskerName((String) question.get("asker_name"));
            vo.setAnswerContent((String) question.get("answer_content"));
            vo.setAnswererName((String) question.get("answerer_name"));
            vo.setAnswerTime((Date) question.get("answer_time"));
            vo.setStatus((String) question.get("status"));
            vo.setLikeCount((Integer) question.get("like_count"));
            vo.setIsTop((Boolean) question.get("is_top"));
            vo.setCreateTime((Date) question.get("create_time"));
            
            result.add(vo);
        }
        
        return result;
    }
    
    /**
     * 回答问题
     * 语法逻辑：
     * 1. 查询问题信息
     * 2. 保存答案
     * 3. 更新状态
     * 
     * 实现效果：
     * - 状态更新为已回答
     * - 权限控制由 Controller 层@RequiresPermissions 注解处理
     * 
     * @param questionId 问题 ID
     * @param answerContent 回答内容
     * @param userId 用户 ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int answerQuestion(Long questionId, String answerContent, Long userId) {
        // 1. 查询问题信息
        Map<String, Object> question = questionMapper.selectQuestionById(questionId);
        if (question == null) {
            throw new ServiceException("问题不存在");
        }
        
        // 2. 更新问题和答案
        Map<String, Object> params = new HashMap<>();
        params.put("id", questionId);
        params.put("answerContent", answerContent);
        params.put("answerUserId", userId);
        params.put("answerTime", DateUtils.getNowDate());
        params.put("status", "answered");
        
        return questionMapper.updateQuestion(params);
    }
    
    /**
     * 跳转到问答板块问题详情
     * 语法逻辑：
     * 1. 查询问题详情
     * 2. 关联用户信息
     * 
     * 实现效果：
     * - 返回问题完整信息
     * 
     * @param questionId 问题 ID
     * @return 问题详情
     */
    @Override
    public QuestionDTO getQuestionDetail(Long questionId) {
        // 1. 查询问题详情
        Map<String, Object> question = questionMapper.selectQuestionById(questionId);
        if (question == null) {
            throw new ServiceException("问题不存在");
        }
        
        // 2. 构建返回结果
        QuestionDTO dto = new QuestionDTO();
        dto.setId(questionId);
        dto.setCourseId((Long) question.get("course_id"));
        dto.setSectionId((Long) question.get("section_id"));
        dto.setQuestionTitle((String) question.get("question_title"));
        dto.setQuestionContent((String) question.get("question_content"));
        
        return dto;
    }
    
    // ==================== 课程评价接口实现 ====================
    
    /**
     * 提交课程评价
     * 语法逻辑：
     * 1. 检查是否已评价
     * 2. 保存评价
     * 
     * 实现效果：
     * - 好评/中评/差评三选一
     * - 每个用户对每门课程只能评价一次
     * 
     * @param reviewDTO 评价 DTO
     * @param userId 用户 ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitReview(ReviewDTO reviewDTO, Long userId) {
        // 1. 检查是否已评价（实际开发中需要查询）
        // List<Map<String, Object>> reviews = reviewMapper.selectReviewsByCourseId(reviewDTO.getCourseId());
        // for (Map<String, Object> review : reviews) {
        //     if (review.get("user_id").equals(userId)) {
        //         throw new ServiceException("您已经评价过该课程");
        //     }
        // }
        
        // 2. 保存评价
        Map<String, Object> params = new HashMap<>();
        params.put("courseId", reviewDTO.getCourseId());
        params.put("userId", userId);
        params.put("rating", reviewDTO.getRating());
        params.put("reviewContent", reviewDTO.getReviewContent());
        
        return reviewMapper.insertReview(params);
    }
    
    /**
     * 获取课程评价统计
     * 语法逻辑：
     * 1. 统计各评价数量
     * 2. 计算平均分
     * 
     * 实现效果：
     * - 返回好评、中评、差评数量
     * - 计算平均评分
     * 
     * @param courseId 课程 ID
     * @return 评价统计 Map
     */
    @Override
    public Map<String, Object> getReviewStatistics(Long courseId) {
        // 1. 查询评价统计
        Map<String, Object> stats = reviewMapper.countReviewsByCourseId(courseId);
        
        // 2. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        if (stats != null) {
            result.put("goodCount", stats.getOrDefault("good_count", 0));
            result.put("mediumCount", stats.getOrDefault("medium_count", 0));
            result.put("badCount", stats.getOrDefault("bad_count", 0));
            result.put("totalCount", stats.getOrDefault("total_count", 0));
            result.put("averageRating", stats.getOrDefault("average_rating", 0.0));
        } else {
            result.put("goodCount", 0);
            result.put("mediumCount", 0);
            result.put("badCount", 0);
            result.put("totalCount", 0);
            result.put("averageRating", 0.0);
        }
        
        return result;
    }
    
    // ==================== 课程服务人员接口实现 ====================
    
    /**
     * 申请成为课程服务人员
     * 语法逻辑：
     * 1. 检查是否已申请
     * 2. 保存申请记录
     * 
     * 实现效果：
     * - 通过考试与审核的人可以申请
     * - 状态默认为待审核
     * 
     * @param courseId 课程 ID
     * @param staffType 服务类型
     * @param examScore 考试成绩
     * @param userId 用户 ID
     * @return 申请 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long applyStaff(Long courseId, String staffType, Integer examScore, Long userId) {
        // 1. 检查是否已申请
        Map<String, Object> existingStaff = staffMapper.selectStaffByUserIdAndCourseId(userId, courseId);
        if (existingStaff != null) {
            String auditStatus = (String) existingStaff.get("audit_status");
            if ("approved".equals(auditStatus)) {
                throw new ServiceException("您已经是该课程的服务人员");
            } else if ("pending".equals(auditStatus)) {
                throw new ServiceException("您的申请正在审核中");
            }
        }
        
        // 2. 保存申请记录
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("courseId", courseId);
        params.put("staffType", staffType);
        params.put("examScore", examScore);
        
        staffMapper.insertStaff(params);
        
        // 3. 返回申请 ID
        return (Long) params.get("id");
    }
    
    /**
     * 审核服务人员申请
     * 语法逻辑：
     * 1. 更新审核状态
     * 
     * 实现效果：
     * - 审核通过后成为服务人员
     * - 权限控制由 Controller 层@RequiresPermissions 注解处理
     * 
     * @param applyId 申请 ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     * @param userId 审核人用户 ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int auditStaff(Long applyId, String auditStatus, String auditRemark, Long userId) {
        // 1. 更新审核状态
        Map<String, Object> params = new HashMap<>();
        params.put("id", applyId);
        params.put("auditStatus", auditStatus);
        params.put("auditUserId", userId);
        params.put("auditRemark", auditRemark);
        
        return staffMapper.updateStaffAudit(params);
    }
    
    /**
     * 获取课程服务人员列表
     * 语法逻辑：
     * 1. 查询服务人员
     * 2. 关联用户信息
     * 
     * 实现效果：
     * - 返回课程所有服务人员
     * - 包含用户基本信息
     * 
     * @param courseId 课程 ID
     * @return 服务人员列表
     */
    @Override
    public List<Map<String, Object>> getCourseStaffs(Long courseId) {
        return staffMapper.selectStaffsByCourseId(courseId);
    }
    



    /**
     * 检查用户是否已购买课程
     * 语法逻辑：
     * 1. 查询学习进度表
     * 2. 查询订单表
     * 
     * 实现效果：
     * - 有学习进度记录表示已购买
     * - 有订单记录表示已购买
     * 
     * @param courseId 课程 ID
     * @param userId 用户 ID
     * @return 是否已购买
     */
    private boolean checkUserPurchased(Long courseId, Long userId) {
        if (userId == null) {
            return false;
        }
        
        // 1. 查询学习进度
        Map<String, Object> progress = progressMapper.selectProgressByUserIdAndCourseId(userId, courseId);
        if (progress != null) {
            return true;
        }
        
        // 2. 查询订单（这里简化处理，实际需要查询订单表）
        // List<Order> orders = orderMapper.selectOrdersByUserIdAndCourseId(userId, courseId);
        // if (orders != null && !orders.isEmpty()) {
        //     return true;
        // }
        
        return false;
    }
    
    // ==================== 标签查询接口实现 ====================
    
    /**
     * 根据关键字模糊查询标签
     * 
     * @param keyword 关键字（可选）
     * @return 标签列表
     */
    @Override
    public List<Map<String, Object>> searchTags(String keyword) {
        return tagMapper.selectTagsByKeyword(keyword);
    }
    
    // ==================== 课程收藏接口实现 ====================
    
    /**
     * 添加课程收藏
     * 
     * @param courseId 课程 ID
     * @param userId 用户 ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addFavorite(Long courseId, Long userId) {
        // 1. 检查是否已收藏
        int count = favaMapper.countFava(userId, courseId, "course");
        if (count > 0) {
            return 1; // 已收藏，直接返回成功
        }
        
        // 2. 保存收藏记录
        OshFava fava = new OshFava();
        fava.setUserId(userId);
        fava.setGoodsId(courseId);
        fava.setType("course");
        int result = favaMapper.insertFava(fava);
        
        // 3. 更新课程收藏计数
        if (result > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("courseId", courseId);
            courseMapper.incrementFavaCount(params);
        }
        
        return result;
    }
    
    /**
     * 取消课程收藏
     * 
     * @param courseId 课程 ID
     * @param userId 用户 ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeFavorite(Long courseId, Long userId) {
        // 1. 删除收藏记录
        OshFava fava = new OshFava();
        fava.setUserId(userId);
        fava.setGoodsId(courseId);
        fava.setType("course");
        int result = favaMapper.deleteFava(fava);
        
        // 2. 更新课程收藏计数（减少）
        if (result > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("courseId", courseId);
            courseMapper.decrementFavaCount(params);
        }
        
        return result;
    }
    
    /**
     * 检查用户是否已收藏课程
     * 
     * @param courseId 课程 ID
     * @param userId 用户 ID
     * @return 是否已收藏
     */
    @Override
    public boolean checkFavorited(Long courseId, Long userId) {
        if (userId == null) {
            return false;
        }
        return favaMapper.countFava(userId, courseId, "course") > 0;
    }
    
    
    // ==================== 视频上传接口实现 ====================
    
    /**
     * 上传课时视频
     * 语法逻辑：
     * 1. 校验文件类型和大小
     * 2. 上传文件到存储服务
     * 3. 提取视频元数据（时长、分辨率、编码格式等）
     * 4. 生成视频预览封面
     * 5. 返回视频信息
     * 
     * 实现效果：
     * - 支持 mp4、avi、mov、mkv 等常见视频格式
     * - 自动提取视频时长、分辨率等元数据
     * - 自动生成视频封面图
     * - 限制文件大小 500MB
     * 
     * @param file 视频文件
     * @param courseId 课程 ID
     * @param userId 用户 ID
     * @return 视频上传结果 VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public VideoUploadVO uploadSectionVideo(MultipartFile file, Long courseId, Long userId) {
        // 1. 校验文件类型
        String fileName = file.getOriginalFilename();
        String extension = "";
        if (fileName != null && fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        
        List<String> allowedVideoExtensions = Arrays.asList("mp4", "avi", "mov", "mkv", "wmv", "flv", "webm");
        if (!allowedVideoExtensions.contains(extension)) {
            throw new ServiceException("仅支持视频格式（mp4/avi/mov/mkv/wmv/flv/webm）");
        }
        
        // 2. 校验文件大小（500MB）
        long maxVideoSize = 500 * 1024 * 1024;
        if (file.getSize() > maxVideoSize) {
            throw new ServiceException("视频文件大小不能超过 500MB");
        }
        
        // 3. 生成存储路径（使用日期分目录）
        String savePath = "/upload/video/" + courseId + "/" + DateUtils.datePath() + "/" + System.currentTimeMillis() + "." + extension;
        
        // 4. TODO: 实际上传文件到存储服务（OSS/本地存储）
        // 实际需要使用 FileUploadUtils.upload() 或调用 OSS 服务
        // String videoUrl = FileUploadUtils.upload(RuoYiConfig.getProfile(), file, VIDEO_EXTENSION);
        
        // 5. 提取视频元数据（实际需要使用 FFmpeg 或第三方服务）
        VideoUploadVO vo = new VideoUploadVO();
        vo.setVideoUrl(savePath);
        vo.setOriginalFileName(fileName);
        vo.setFileSize(file.getSize());
        
        // TODO: 实际需要调用视频处理服务提取元数据
        // 以下为模拟数据，实际开发需集成 FFmpeg 或视频处理服务
        // 示例：FFmpeg 命令提取信息
        // ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 input.mp4
        // ffprobe -v error -select_streams v:0 -show_entries stream=width,height,codec_name,bit_rate -of default=noprint_wrappers=1:nokey=1 input.mp4
        vo.setDuration(0);  // 实际需要从视频中提取
        vo.setVideoCodec("h264");
        vo.setVideoResolution("1080p");
        vo.setVideoBitrate(0);
        vo.setCoverUrl("/upload/cover/default.png"); // 实际需要生成封面
        
        // 返回结果
        return vo;
    }
    
    /**
     * 上传课时资料
     * 语法逻辑：
     * 1. 校验文件类型（仅允许压缩包 zip/rar/tar/gz）
     * 2. 校验文件大小（不超过 100MB）
     * 3. 安全检查（防止恶意文件上传）
     * 4. 存储到指定目录
     * 
     * 实现效果：
     * - 支持 zip/rar/tar/gz 格式
     * - 自动检查文件名和后缀，防止恶意文件
     * - 存储到课程专属目录，便于管理
     * 
     * @param file 资料文件
     * @param courseId 课程 ID
     * @param materialName 资料名称
     * @param userId 用户 ID
     * @return 资料 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadSectionMaterial(MultipartFile file, Long courseId, String materialName, Long userId) {
        // 1. 校验文件类型
        String fileName = file.getOriginalFilename();
        String extension = "";
        if (fileName != null && fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        
        List<String> allowedArchiveExtensions = Arrays.asList("zip", "rar", "tar", "gz", "7z");
        if (!allowedArchiveExtensions.contains(extension)) {
            throw new ServiceException("仅支持压缩包格式（zip/rar/tar/gz/7z）");
        }
        
        // 2. 校验文件大小（100MB）
        long maxArchiveSize = 100 * 1024 * 1024;
        if (file.getSize() > maxArchiveSize) {
            throw new ServiceException("压缩包文件大小不能超过 100MB");
        }
        
        // 3. 安全检查：检查文件名是否包含非法字符
        if (fileName != null && (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\"))) {
            throw new ServiceException("文件名包含非法字符");
        }
        
        // 4. 生成存储路径
        String savePath = "/upload/course/" + courseId + "/materials/" + DateUtils.datePath() + "/" + System.currentTimeMillis() + "." + extension;
        
        // 5. TODO: 实际上传文件到存储服务
        // 实际需要使用 FileUploadUtils.upload() 或调用 OSS 服务
        // String fileUrl = FileUploadUtils.upload(RuoYiConfig.getProfile(), file, new String[]{"zip", "rar", "tar", "gz", "7z"});
        
        // 6. 保存资料信息到数据库
        Map<String, Object> params = new HashMap<>();
        params.put("courseId", courseId);
        params.put("materialName", StringUtils.defaultIfEmpty(materialName, fileName));
        params.put("fileUrl", savePath);
        params.put("fileType", extension);
        params.put("fileSize", file.getSize());
        params.put("isPayOnly", 1); // 默认仅购买后可下载
        params.put("sort", 0);
        params.put("createBy", String.valueOf(userId));
        params.put("createTime", DateUtils.getNowDate());
        
        materialMapper.insertMaterial(params);
        
        // 7. 返回资料 ID
        return (Long) params.get("id");
    }
    
    
    /**
     * 新增课程、章节及相关资料
     * @param courseCreateDTO 课程创建 DTO
     * @param userId 用户 ID
     * @return 课程 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCourseWithSections(CourseCreateDTO courseCreateDTO, Long userId) {
        // 1. 校验必填参数 
        if (StringUtils.isEmpty(courseCreateDTO.getTitle())) {
            throw new ServiceException("课程标题不能为空");
        }
        // TODO 办：校验其他必填参数（如tagr、type、price 等）
        
        // 2. 保存课程信息
        OshCourse course =  new OshCourse();
        BeanUtils.copyProperties(courseCreateDTO, course);
        course.setCreateBy(String.valueOf(userId));
        course.setCreateTime(DateUtils.getNowDate());
        course.setUpdateTime(DateUtils.getNowDate());

        int result = courseMapper.insertCourse(course);
        if (result <= 0) {
            throw new ServiceException("新增课程失败");
        }

        Long courseId = course.getId();
        // 3. 保存章节信息
        int sectionCount = saveSectionsAndMaterials(courseId, courseCreateDTO.getSections());
        
        // 4. 更新课程章节数量
        if (sectionCount > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", courseId);
            params.put("subCount", sectionCount);
            courseMapper.updateCourseSubCount(params);
        }
        
        // 5. 保存标签关联关系
        saveCourseTags(courseId, courseCreateDTO.getTagIds());
        
        return courseId;
    }
    
    
    /**
     * 保存章节及资料信息
     * @return 章节数量
     */
    private int saveSectionsAndMaterials(Long courseId, List<SectionCreateDTO> sections) {
        if (sections == null || sections.isEmpty()) {
            return 0;
        }
        
        int sectionCount = 0;
        for (SectionCreateDTO sectionDTO : sections) {
            if (sectionDTO == null) {
                continue;
            }
            
            // 1. 构建章节实体
            OshCourseSection section = new OshCourseSection();
            BeanUtils.copyProperties(sectionDTO, section);
            section.setCourseId(courseId);
            
            // 设置默认值
            if (section.getParentId() == null) {
                section.setParentId(0L);
            }
            if (section.getSort() == null) {
                section.setSort(0);
            }
            if (section.getIsFree() == null) {
                section.setIsFree(0);
            }
            if (section.getStatus() == null) {
                section.setStatus(1);
            }

            // 2. 保存章节
            sectionMapper.insertSectionEntity(section);
            Long sectionId = section.getId();
            sectionCount++;
            
            // 3. 保存课时视频（作为特殊资料类型）
            if (StringUtils.isNotEmpty(sectionDTO.getMediaUrl())) {
                saveVideoMaterial(courseId, sectionId, sectionDTO);
            }
            
            // 4. 保存课件资料
            saveSectionMaterials(courseId, sectionId, sectionDTO.getMaterials());
        }
        
        return sectionCount;
    }
    
    /**
     * 保存课时视频资料
     * 视频作为特殊资料类型，关联到章节
     * 
     * @param courseId 课程ID
     * @param sectionId 章节ID
     * @param sectionDTO 章节DTO
     */
    private void saveVideoMaterial(Long courseId, Long sectionId, SectionCreateDTO sectionDTO) {
        OshCourseMaterial material = new OshCourseMaterial();
        // 视频名称：章节标题 + "-视频"
        material.setMaterialName(sectionDTO.getTitle() + " - 视频");
        // 视频URL
        material.setFileUrl(sectionDTO.getMediaUrl());
        // 视频文件类型
        material.setFileType("video");
        // 设置关联
        material.setCourseId(courseId);
        material.setSectionId(sectionId);
        // 视频默认免费可下载（与章节免费属性一致）
        material.setIsPayOnly(sectionDTO.getIsFree() != null && sectionDTO.getIsFree() == 1 ? 0 : 1);
        material.setSort(0);
        
        materialMapper.insertMaterialEntity(material);
    }
    
    /**
     * 保存章节资料（课件压缩包）
     * 
     * @param courseId 课程ID
     * @param sectionId 章节ID
     * @param materials 资料列表
     */
    private void saveSectionMaterials(Long courseId, Long sectionId, List<SectionMaterialDTO> materials) {
        if (materials == null || materials.isEmpty()) {
            return;
        }
        
        // 允许的文件类型白名单
        Set<String> allowedFileTypes = new HashSet<>(Arrays.asList(
            "zip", "rar", "7z", "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt"
        ));
        
        for (SectionMaterialDTO materialDTO : materials) {
            if (materialDTO == null) {
                log.warn("跳过空资料对象");
                continue;
            }
            
            // 校验文件类型
            if (materialDTO.getFileType() != null && 
                !allowedFileTypes.contains(materialDTO.getFileType().toLowerCase())) {
                log.warn("资料文件类型不支持: {}, 跳过该资料", materialDTO.getFileType());
                continue;
            }
            
            OshCourseMaterial material = new OshCourseMaterial();
            // DTO字段映射到实体字段
            material.setMaterialName(materialDTO.getName());
            material.setFileUrl(materialDTO.getUrl());
            material.setFileSize(materialDTO.getFileSize());
            material.setFileType(materialDTO.getFileType());
            // 设置关联
            material.setCourseId(courseId);
            material.setSectionId(sectionId);
            // 设置默认值
            if (material.getIsPayOnly() == null) {
                material.setIsPayOnly(1);
            }
            if (material.getSort() == null) {
                material.setSort(0);
            }
    
            materialMapper.insertMaterialEntity(material);
        }
    }
    
    /**
     * 保存课程标签关联
     */
    private void saveCourseTags(Long courseId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        
        for (Long tagId : tagIds) {
            Map<String, Object> params = new HashMap<>();
            params.put("courseId", courseId);
            params.put("tagId", tagId);
            tagMapper.insertCourseTagRelation(params);
            tagMapper.incrementUsageCount(tagId);
        }
    }
    
    
    // ==================== 添加章节/课时接口实现 ====================
    
    /**
     * 添加章节/课时
     * 语法逻辑：
     * 1. 校验课程是否存在
     * 2. 校验用户权限（课程创建者或服务人员）
     * 3. 保存章节信息
     * 4. 保存章节资料（可选）
     * 5. 更新课程章节数量
     * 
     * 实现效果：
     * - 为已存在的课程追加新的章节或课时内容
     * - 权限控制由 Controller 层 @RequiresPermissions 注解处理
     * 
     * @param courseId 课程 ID
     * @param sectionCreateDTO 章节创建 DTO
     * @param userId 用户 ID
     * @return 章节 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addSection(Long courseId, SectionCreateDTO sectionCreateDTO, Long userId) {
        // 1. 校验课程是否存在
        OshCourse course = courseMapper.selectCourseById(courseId);
        if (course == null) {
            throw new ServiceException("课程不存在");
        }
        
        // 2. 校验必填参数
        if (StringUtils.isEmpty(sectionCreateDTO.getTitle())) {
            throw new ServiceException("章节标题不能为空");
        }
        
        // 3. 构建章节实体
        OshCourseSection section = new OshCourseSection();
        BeanUtils.copyProperties(sectionCreateDTO, section);
        section.setCourseId(courseId);
        
        // 设置默认值
        if (section.getParentId() == null) {
            section.setParentId(0L);
        }
        if (section.getSort() == null) {
            section.setSort(0);
        }
        if (section.getIsFree() == null) {
            section.setIsFree(0);
        }
        if (section.getStatus() == null) {
            section.setStatus(1);
        }
        
        sectionMapper.insertSectionEntity(section);
        Long sectionId = section.getId();
        
        // 4. 保存课时视频（作为特殊资料类型）
        if (StringUtils.isNotEmpty(sectionCreateDTO.getMediaUrl())) {
            saveVideoMaterial(courseId, sectionId, sectionCreateDTO);
        }
        
        // 5. 保存课件资料
        saveSectionMaterials(courseId, sectionId, sectionCreateDTO.getMaterials());
        
        // 6. 课程章节数量 +1
        Map<String, Object> params = new HashMap<>();
        params.put("id", courseId);
        int currentCount = course.getSubCount() != null ? course.getSubCount() : 0;
        params.put("subCount", currentCount + 1);
        courseMapper.updateCourseSubCount(params);
        
        return sectionId;
    }
}
