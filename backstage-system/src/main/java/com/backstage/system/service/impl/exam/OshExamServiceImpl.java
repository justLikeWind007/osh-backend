package com.backstage.system.service.impl.exam;

import com.alibaba.fastjson2.JSON;
import com.backstage.common.core.domain.R;
import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.dto.exam.ExamQuestionSaveDto;
import com.backstage.system.domain.dto.exam.ExamSaveDto;
import com.backstage.system.domain.dto.exam.ExamSearchDto;
import com.backstage.system.domain.exam.OshExamQuestion;
import com.backstage.system.domain.exam.*;
import com.backstage.system.domain.vo.exam.ExamDetailVo;
import com.backstage.system.domain.vo.exam.ExamVo;
import com.backstage.system.domain.vo.exam.QuestionVo;
import com.backstage.system.mapper.exam.OshExamCollectionMapper;
import com.backstage.system.mapper.exam.OshExamMapper;
import com.backstage.system.mapper.exam.OshExamTagMapper;
import com.backstage.system.mapper.exam.OshExamQuestionMapper;
import com.backstage.system.mapper.exam.OshUserExamRecordMapper;
import com.backstage.system.service.exam.IOshExamService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OshExamServiceImpl implements IOshExamService {

    @Autowired
    private OshExamMapper examMapper;

    @Autowired
    private OshExamTagMapper examTagMapper;

    @Autowired
    private OshExamCollectionMapper examCollectionMapper;

    @Autowired
    private OshUserExamRecordMapper userExamRecordMapper;

    @Autowired
    private OshExamQuestionMapper examQuestionMapper;

    // ─────────────────────────────────────────────
    // 考试列表（分页 + 搜索 + 回填 is_test/is_collected）
    // ─────────────────────────────────────────────
    @Override
    public PageResponse<ExamVo> searchExams(ExamSearchDto dto, Long currentUserId) {
        // 把 userId 注入 dto，供 collectFlag=1 时 SQL 使用
        dto.setUserId(currentUserId);
        PageHelper.startPage(dto.getPageNum(), dto.getPageSize());
        List<ExamVo> list = examMapper.selectExamList(dto);
        PageInfo<ExamVo> pageInfo = new PageInfo<>(list);

        if (!list.isEmpty()) {
            List<Long> examIds = list.stream().map(ExamVo::getId).collect(Collectors.toList());

            // 回填标签
            for (ExamVo vo : list) {
                List<String> tags = examTagMapper.selectTagNamesByExamId(vo.getId());
                vo.setTags(tags);
                vo.setIs_test(false);
                vo.setIs_collected(false);
            }

            // 回填 is_test / is_collected（需要登录）
            if (currentUserId != null) {
                List<Long> finishedIds = examMapper.selectFinishedExamIdsByUserId(currentUserId);
                Set<Long> finishedSet = new HashSet<>(finishedIds);

                List<Long> collectedIds = examCollectionMapper.selectCollectedExamIdsByUserId(currentUserId);
                Set<Long> collectedSet = new HashSet<>(collectedIds);

                for (ExamVo vo : list) {
                    vo.setIs_test(finishedSet.contains(vo.getId()));
                    vo.setIs_collected(collectedSet.contains(vo.getId()));
                }
            }
        }

        return PageResponse.of(list, pageInfo.getTotal(), dto.getPageNum(), dto.getPageSize());
    }

    // ─────────────────────────────────────────────
    // 考试详情（含题目，自动创建/复用考试记录）
    // ─────────────────────────────────────────────
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamDetailVo getExamDetail(Long examId, Long currentUserId) {
        ExamDetailVo detail = examMapper.selectExamById(examId);
        if (detail == null) return null;

        // 处理题目选项格式（JSON字符串 → 数组）
        if (detail.getTestpaper_questions() != null) {
            for (QuestionVo q : detail.getTestpaper_questions()) {
                if (q.getOptions() instanceof String) {
                    String optStr = (String) q.getOptions();
                    q.setOptions(JSON.parseArray(optStr, String.class));
                }
                // 初始化用户答案默认值
                if ("checkbox".equals(q.getType()) || "answer".equals(q.getType()) || "completion".equals(q.getType())) {
                    q.setUser_value(new String[]{""});
                } else {
                    q.setUser_value(-1);
                }
            }
        }

        // 创建或复用考试记录，返回真实的 user_test_id
        if (currentUserId != null) {
            OshUserExamRecord record = userExamRecordMapper.selectByUserIdAndExamId(currentUserId, examId);
            if (record == null) {
                record = new OshUserExamRecord();
                record.setUser_id(currentUserId);
                record.setExam_id(examId);
                record.setAnswer_status(0);
                record.setRead_status(0);
                record.setScore(0);
                record.setDelete_flag(0);
                userExamRecordMapper.insertOshUserExam(record);
            }
            detail.setUser_test_id(record.getId());
        } else {
            detail.setUser_test_id(null);
        }

        return detail;
    }

    // ─────────────────────────────────────────────
    // 新增/修改考试
    // ─────────────────────────────────────────────
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Long> saveExam(ExamSaveDto dto, String operator) {
        if (StringUtils.isBlank(dto.getTitle())) {
            return R.fail("考试标题不能为空");
        }

        OshExamination exam;
        boolean isCreate = (dto.getId() == null);

        if (isCreate) {
            // 新增
            if (dto.getTotalScore() == null) return R.fail("总分不能为空");
            if (dto.getPassScore() == null)  return R.fail("及格分不能为空");
            if (dto.getExpire() == null)     return R.fail("考试时长不能为空");

            exam = new OshExamination();
            exam.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
            exam.setCollectCount(0);
            exam.setQuestionCount(0);
            exam.setDeleteFlag(0);
            exam.setCreateBy(operator);
            exam.setCreateTime(new Date());
        } else {
            // 修改
            exam = examMapper.selectById(dto.getId());
            if (exam == null || exam.getDeleteFlag() == 1) {
                return R.fail("考试不存在");
            }
        }

        exam.setTitle(dto.getTitle().trim());
        if (dto.getTotalScore() != null) exam.setTotalScore(dto.getTotalScore());
        if (dto.getPassScore() != null)  exam.setPassScore(dto.getPassScore());
        if (dto.getExpire() != null)     exam.setExpire(dto.getExpire());
        if (dto.getStartTime() != null)  exam.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null)    exam.setEndTime(dto.getEndTime());
        if (dto.getStatus() != null)     exam.setStatus(dto.getStatus());
        if (dto.getResourceType() != null) exam.setResourceType(StringUtils.trimToNull(dto.getResourceType()));
        if (dto.getResourceId() != null)   exam.setResourceId(dto.getResourceId());
        if (dto.getCover() != null)        exam.setCover(StringUtils.trimToNull(dto.getCover()));
        if (dto.getDescription() != null)  exam.setDescription(dto.getDescription().trim());

        if (isCreate) {
            examMapper.insert(exam);
        } else {
            examMapper.updateById(exam);
        }

        // 处理标签（先删后建）
        if (dto.getTags() != null) {
            examTagMapper.deleteTagRelByExamId(exam.getId());
            for (String tagName : dto.getTags()) {
                if (StringUtils.isBlank(tagName)) continue;
                Long tagId = resolveTagId(tagName.trim(), operator);
                if (tagId != null) {
                    OshExamTagRel rel = new OshExamTagRel();
                    rel.setExamId(exam.getId());
                    rel.setTagId(tagId);
                    rel.setDeleteFlag(0);
                    rel.setCreateBy(operator);
                    rel.setUpdateBy(operator);
                    examTagMapper.insertTagRel(rel);
                    // 增加标签使用次数
                    OshExamTag tag = examTagMapper.selectById(tagId);
                    if (tag != null) {
                        tag.setUseCount(tag.getUseCount() + 1);
                        examTagMapper.updateById(tag);
                    }
                }
            }
        }

        return R.ok(exam.getId());
    }

    // ─────────────────────────────────────────────
    // 删除考试（软删除）
    // ─────────────────────────────────────────────
    @Override
    public R<String> deleteExam(Long examId, String operator) {
        OshExamination exam = examMapper.selectById(examId);
        if (exam == null || exam.getDeleteFlag() == 1) {
            return R.fail("考试不存在");
        }
        examMapper.deleteExamById(examId, operator);
        return R.ok("删除成功");
    }

    // ─────────────────────────────────────────────
    // 收藏/取消收藏
    // ─────────────────────────────────────────────
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> toggleCollect(Long examId, Long userId, String operator) {
        OshExamCollection existing = examCollectionMapper.selectByUserIdAndExamId(userId, examId);
        OshExamination exam = examMapper.selectById(examId);
        if (exam == null || exam.getDeleteFlag() == 1) {
            return R.fail("考试不存在");
        }

        if (existing == null) {
            // 新增收藏
            OshExamCollection col = new OshExamCollection();
            col.setUserId(userId);
            col.setExamId(examId);
            col.setDeleteFlag(0);
            col.setCreateBy(operator);
            col.setCreateTime(new Date());
            col.setUpdateBy(operator);
            col.setUpdateTime(new Date());
            examCollectionMapper.insert(col);
            // 收藏数 +1
            exam.setCollectCount(exam.getCollectCount() == null ? 1 : exam.getCollectCount() + 1);
            examMapper.updateById(exam);
            return R.ok("收藏成功");
        } else if (existing.getDeleteFlag() == 1) {
            // 恢复收藏
            examCollectionMapper.updateDeleteFlag(existing.getId(), 0, operator);
            exam.setCollectCount(exam.getCollectCount() == null ? 1 : exam.getCollectCount() + 1);
            examMapper.updateById(exam);
            return R.ok("收藏成功");
        } else {
            // 取消收藏
            examCollectionMapper.updateDeleteFlag(existing.getId(), 1, operator);
            exam.setCollectCount(Math.max(0, exam.getCollectCount() == null ? 0 : exam.getCollectCount() - 1));
            examMapper.updateById(exam);
            return R.ok("取消收藏成功");
        }
    }

    // ─────────────────────────────────────────────
    // 获取标签列表
    // ─────────────────────────────────────────────
    @Override
    public List<OshExamTag> getTagList() {
        return examTagMapper.selectAllActiveTags();
    }

    // ─────────────────────────────────────────────
    // 题目增删改
    // ─────────────────────────────────────────────
    private static final Set<String> QUESTION_TYPES = new HashSet<>(Arrays.asList(
            "radio", "checkbox", "trueOrfalse", "completion", "answer"));

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Long> saveExamQuestion(ExamQuestionSaveDto dto, String operator) {
        if (dto.getExamId() == null) {
            return R.fail("examId 不能为空");
        }
        if (StringUtils.isBlank(dto.getTitle())) {
            return R.fail("题目标题不能为空");
        }
        if (dto.getScore() == null || dto.getScore() < 1) {
            return R.fail("题目分值无效");
        }
        if (StringUtils.isBlank(dto.getType()) || !QUESTION_TYPES.contains(dto.getType())) {
            return R.fail("题目类型无效");
        }
        OshExamination exam = examMapper.selectById(dto.getExamId());
        if (exam == null || exam.getDeleteFlag() == 1) {
            return R.fail("考试不存在");
        }

        String opt = StringUtils.trimToNull(dto.getOptions());
        if ("radio".equals(dto.getType()) || "checkbox".equals(dto.getType())) {
            if (StringUtils.isBlank(opt)) {
                return R.fail("选择题请填写 options（JSON 数组字符串）");
            }
            try {
                JSON.parseArray(opt, String.class);
            } catch (Exception e) {
                return R.fail("options 需为合法 JSON 数组");
            }
        }

        boolean isCreate = dto.getId() == null;
        OshExamQuestion row = new OshExamQuestion();
        row.setExamId(dto.getExamId());
        row.setTitle(dto.getTitle().trim());
        row.setScore(dto.getScore());
        row.setType(dto.getType());
        row.setRemark(StringUtils.trimToNull(dto.getRemark()));
        row.setOptions(opt);
        row.setCorrectAnswer(StringUtils.trimToNull(dto.getCorrectAnswer()));
        row.setUpdateBy(operator != null ? operator : "system");

        if (isCreate) {
            row.setCreateBy(operator != null ? operator : "system");
            examQuestionMapper.insertQuestion(row);
        } else {
            OshExamQuestion existing = examQuestionMapper.selectByIdAndExamId(dto.getId(), dto.getExamId());
            if (existing == null) {
                return R.fail("题目不存在");
            }
            row.setId(dto.getId());
            int n = examQuestionMapper.updateQuestion(row);
            if (n == 0) {
                return R.fail("题目更新失败");
            }
        }

        examMapper.updateExamQuestionStats(dto.getExamId());
        return R.ok(row.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<String> deleteExamQuestion(Long questionId, Long examId, String operator) {
        if (questionId == null || examId == null) {
            return R.fail("参数不完整");
        }
        OshExamQuestion existing = examQuestionMapper.selectByIdAndExamId(questionId, examId);
        if (existing == null) {
            return R.fail("题目不存在");
        }
        examQuestionMapper.softDeleteQuestion(questionId, examId, operator != null ? operator : "system");
        examMapper.updateExamQuestionStats(examId);
        return R.ok("删除成功");
    }

    // ─────────────────────────────────────────────
    // 私有：根据标签名 resolve ID（存在复用，不存在新建）
    // ─────────────────────────────────────────────
    private Long resolveTagId(String tagName, String operator) {
        OshExamTag existing = examTagMapper.selectTagByName(tagName);
        if (existing != null) return existing.getId();

        OshExamTag tag = new OshExamTag();
        tag.setName(tagName);
        tag.setSort(0);
        tag.setUseCount(0);
        tag.setStatus(1);
        tag.setDeleteFlag(0);
        tag.setCreateBy(operator);
        tag.setCreateTime(new Date());
        tag.setUpdateBy(operator);
        tag.setUpdateTime(new Date());
        try {
            examTagMapper.insert(tag);
            return tag.getId();
        } catch (DuplicateKeyException ex) {
            OshExamTag retry = examTagMapper.selectTagByName(tagName);
            return retry != null ? retry.getId() : null;
        }
    }
}
