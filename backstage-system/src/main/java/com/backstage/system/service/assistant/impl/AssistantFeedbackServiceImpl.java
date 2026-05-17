package com.backstage.system.service.assistant.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.assistant.AssistantFeedbackCategory;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackTagVO;
import com.backstage.system.domain.assistant.AssistantTicketStatus;
import com.backstage.system.domain.assistant.AssistantFeedback;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackCreateDTO;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackPageDTO;
import com.backstage.system.domain.assistant.dto.AssistantTicketQueryDTO;
import com.backstage.system.domain.assistant.dto.AssistantTicketStatusUpdateDTO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackDetailVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackListVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackProcessRecordVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackVO;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.assistant.AssistantFeedbackMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.service.assistant.IAssistantFeedbackCategoryService;
import com.backstage.system.service.assistant.IAssistantFeedbackFavoriteService;
import com.backstage.system.service.assistant.IAssistantFeedbackLikeService;
import com.backstage.system.service.assistant.IAssistantFeedbackProcessRecordService;
import com.backstage.system.service.assistant.IAssistantFeedbackService;
import com.backstage.system.service.assistant.IAssistantFeedbackTagService;
import com.backstage.system.util.FeedbackHotScoreCalculator;
import com.backstage.system.utils.UserContextUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AI 助手反馈服务实现
 *
 * @author backstage
 */
@Service
public class AssistantFeedbackServiceImpl extends ServiceImpl<AssistantFeedbackMapper, AssistantFeedback>
        implements IAssistantFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(AssistantFeedbackServiceImpl.class);

    public AssistantFeedbackServiceImpl(IAssistantFeedbackCategoryService categoryService, IAssistantFeedbackLikeService likeService, IAssistantFeedbackFavoriteService favoriteService, IAssistantFeedbackProcessRecordService processRecordService, IAssistantFeedbackTagService feedbackTagService, OshUserMapper oshUserMapper) {
        this.categoryService = categoryService;
        this.likeService = likeService;
        this.favoriteService = favoriteService;
        this.processRecordService = processRecordService;
        this.feedbackTagService = feedbackTagService;
        this.oshUserMapper = oshUserMapper;
    }

    private final IAssistantFeedbackCategoryService categoryService;
    private final IAssistantFeedbackLikeService likeService;
    private final IAssistantFeedbackFavoriteService favoriteService;
    private final IAssistantFeedbackProcessRecordService processRecordService;
    private final IAssistantFeedbackTagService feedbackTagService;
    private final OshUserMapper oshUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssistantFeedbackVO createFeedback(Long userId, AssistantFeedbackCreateDTO dto) {
        return createFeedback(userId, dto, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssistantFeedbackVO createFeedback(Long userId, AssistantFeedbackCreateDTO dto, boolean allowAdminOnly) {
        // 验证分类是否存在且可用
        AssistantFeedbackCategory category = categoryService.getById(dto.getCategoryId());
        if (category == null || category.getIsEnabled() == 0) {
            throw new ServiceException("分类不存在或已禁用");
        }

        // 检查是否为管理员专用分类
        if (!allowAdminOnly && category.getIsAdminOnly() == 1) {
            throw new ServiceException("该分类仅管理员可用");
        }

        AssistantFeedback feedback = new AssistantFeedback();
        feedback.setUserId(userId);
        feedback.setCategoryId(dto.getCategoryId());
        feedback.setTicketNo(generateTicketNo());
        feedback.setTitle(dto.getTitle().trim());
        feedback.setContent(dto.getContent().trim());
        feedback.setPagePath(StrUtil.isNotBlank(dto.getPagePath()) ? dto.getPagePath().trim() : null);
        feedback.setStatus(AssistantTicketStatus.PENDING.getCode());
        feedback.setResult("");
        feedback.setHandlerId(null);
        feedback.setHandlerName(null);
        feedback.setHandledTime(null);
        feedback.setCloseReason(null);
        feedback.setIsPinned(0);
        feedback.setPinOrder(0);
        feedback.setCommentCount(0);
        feedback.setViewCount(0);
        feedback.setLikeCount(0);
        feedback.setFavoriteCount(0);
        feedback.setHotScore(0);
        feedback.setDeleted(false);
        feedback.setCreateBy(userId);
        feedback.setUpdateBy(userId);

        save(feedback);
        feedbackTagService.bindFeedbackTags(feedback.getId(), dto.getTagIds(), userId);
        safeCreateProcessRecord(feedback.getId(), null, AssistantTicketStatus.PENDING.getCode(),
                userId, getUserDisplayName(getUserById(userId), "匿名用户"), "用户提交反馈");
        return toVO(feedback);
    }

    @Override
    public TableDataInfo getMyFeedback(Long userId) {
        List<AssistantFeedback> list = lambdaQuery()
                .eq(AssistantFeedback::getUserId, userId)
                .eq(AssistantFeedback::getDeleteFlag, (byte) 0)
                .orderByDesc(AssistantFeedback::getCreateTime)
                .list();

        List<AssistantFeedbackVO> rows = list.stream().map(this::toVO).collect(Collectors.toList());
        return new TableDataInfo(rows, new PageInfo<>(list).getTotal());
    }

    @Override
    public TableDataInfo listTickets(AssistantTicketQueryDTO queryDTO) {
        List<AssistantFeedback> list = lambdaQuery()
                .eq(AssistantFeedback::getDeleteFlag, (byte) 0)
                .eq(StrUtil.isNotBlank(queryDTO.getStatus()), AssistantFeedback::getStatus, AssistantTicketStatus.normalize(queryDTO.getStatus()))
                .eq(queryDTO.getCategoryId() != null, AssistantFeedback::getCategoryId, queryDTO.getCategoryId())
                .and(StrUtil.isNotBlank(queryDTO.getKeyword()), wrapper -> wrapper
                        .like(AssistantFeedback::getTicketNo, queryDTO.getKeyword())
                        .or()
                        .like(AssistantFeedback::getTitle, queryDTO.getKeyword())
                        .or()
                        .like(AssistantFeedback::getContent, queryDTO.getKeyword()))
                .orderByDesc(AssistantFeedback::getCreateTime)
                .list();

        List<AssistantFeedbackVO> rows = list.stream().map(this::toVO).collect(Collectors.toList());
        return new TableDataInfo(rows, new PageInfo<>(list).getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssistantFeedbackVO updateTicketStatus(Long ticketId, Long handlerId, AssistantTicketStatusUpdateDTO dto) {
        String targetStatus = AssistantTicketStatus.normalize(dto.getToStatus());
        if (!AssistantTicketStatus.isValid(targetStatus)) {
            throw new ServiceException("不支持的工单状态");
        }

        AssistantFeedback feedback = this.getById(ticketId);
        if (feedback == null || feedback.getDeleteFlag() == 1) {
            throw new ServiceException("工单不存在");
        }

        String currentStatus = AssistantTicketStatus.normalize(feedback.getStatus());
        if (!AssistantTicketStatus.canTransfer(currentStatus, targetStatus)) {
            throw new ServiceException("当前状态不允许流转到目标状态");
        }

        OshUser handler = getUserById(handlerId);
        String handlerName = getUserDisplayName(handler, "管理员");
        String remark = StrUtil.trim(dto.getRemark());
        LocalDateTime handledTime = LocalDateTime.now();

        feedback.setStatus(targetStatus);
        feedback.setResult(StrUtil.blankToDefault(remark, ""));
        feedback.setHandlerId(handlerId);
        feedback.setHandlerName(handlerName);
        feedback.setUpdateBy(handlerId);
        this.updateById(feedback);
        safeCreateProcessRecord(ticketId, currentStatus, targetStatus, handlerId, handlerName, remark);
        feedback.setHandledTime(handledTime);
        feedback.setCloseReason(AssistantTicketStatus.CLOSED.getCode().equals(targetStatus) ? StrUtil.blankToDefault(remark, "") : null);
        return toVO(feedback);
    }

    /**
     * 实体转 VO
     */
    private AssistantFeedbackVO toVO(AssistantFeedback feedback) {
        return toVO(feedback, null);
    }

    private AssistantFeedbackVO toVO(AssistantFeedback feedback, Map<Long, List<AssistantFeedbackTagVO>> feedbackTagMap) {
        AssistantFeedbackVO vo = new AssistantFeedbackVO();
        BeanUtils.copyProperties(feedback, vo);
        fillUserInfo(vo, feedback.getUserId());
        fillHandlerInfo(vo, feedback.getHandlerId(), feedback.getHandlerName());
        vo.setStatus(AssistantTicketStatus.normalize(feedback.getStatus()));
        vo.setResult(StrUtil.blankToDefault(feedback.getResult(), ""));
        fillProcessSummary(vo, listProcessRecordsSafely(feedback.getId()));

        // 补充分类信息
        AssistantFeedbackCategory category = categoryService.getById(feedback.getCategoryId());
        if (category != null) {
            vo.setCategoryCode(category.getCode());
            vo.setCategoryName(category.getName());
            vo.setCategoryIcon(category.getIcon());
        }
        vo.setTags(resolveFeedbackTags(feedback.getId(), feedbackTagMap));

        return vo;
    }

    /**
     * 生成工单编号
     * 格式：TK + 日期(yyyyMMdd) + 时间戳后6位
     */
    private String generateTicketNo() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = timestamp.substring(Math.max(0, timestamp.length() - 6));
        return "TK" + date + suffix;
    }

    @Override
    public TableDataInfo pageFeedback(AssistantFeedbackPageDTO dto) {
        Page<AssistantFeedback> page = queryFeedbackPage(dto);
        Map<Long, List<AssistantFeedbackTagVO>> feedbackTagMap = buildFeedbackTagMap(page.getRecords());

        List<AssistantFeedbackVO> rows = page.getRecords().stream()
                .map(feedback -> toVO(feedback, feedbackTagMap))
                .collect(Collectors.toList());

        return new TableDataInfo(rows, page.getTotal());
    }

    @Override
    public TableDataInfo pageFeedbackList(AssistantFeedbackPageDTO dto) {
        Page<AssistantFeedback> page = queryFeedbackPage(dto);
        Map<Long, OshUser> userMap = buildUserMap(page.getRecords());
        Map<Long, AssistantFeedbackCategory> categoryMap = buildCategoryMap(page.getRecords());
        Map<Long, List<AssistantFeedbackTagVO>> feedbackTagMap = buildFeedbackTagMap(page.getRecords());

        List<AssistantFeedbackListVO> rows = page.getRecords().stream()
                .map(feedback -> toListVO(feedback, userMap, categoryMap, feedbackTagMap))
                .collect(Collectors.toList());

        return new TableDataInfo(rows, page.getTotal());
    }

    private Page<AssistantFeedback> queryFeedbackPage(AssistantFeedbackPageDTO dto) {
        Set<Long> tagFilteredFeedbackIds = filterFeedbackIdsByTags(dto.getTagIds());
        Set<Long> scopedFeedbackIds = filterFeedbackIdsByQueryMode(dto);
        Set<Long> filteredFeedbackIds = intersectFeedbackIds(tagFilteredFeedbackIds, scopedFeedbackIds);
        if (shouldReturnEmptyPage(tagFilteredFeedbackIds, scopedFeedbackIds, filteredFeedbackIds)) {
            return new Page<>(dto.getPageNum(), dto.getPageSize(), 0);
        }
        // 处理 isAnnouncement 参数：转换为分类查询
        Long categoryIdToQuery = dto.getCategoryId();
        if (dto.getIsAnnouncement() != null) {
            if (dto.getIsAnnouncement() == 1) {
                // 查询公告：使用 announcement 分类
                AssistantFeedbackCategory announcementCategory = categoryService.lambdaQuery()
                        .eq(AssistantFeedbackCategory::getCode, "announcement")
                        .one();
                if (announcementCategory != null) {
                    categoryIdToQuery = announcementCategory.getId();
                }
            } else if (dto.getIsAnnouncement() == 0) {
                // 查询非公告：排除 announcement 分类
                AssistantFeedbackCategory announcementCategory = categoryService.lambdaQuery()
                        .eq(AssistantFeedbackCategory::getCode, "announcement")
                        .one();
                if (announcementCategory != null) {
                    Long announcementCategoryId = announcementCategory.getId();
                    Page<AssistantFeedback> page = lambdaQuery()
                            .ne(AssistantFeedback::getCategoryId, announcementCategoryId)
                            .in(filteredFeedbackIds != null && !filteredFeedbackIds.isEmpty(), AssistantFeedback::getId, filteredFeedbackIds)
                            .eq(dto.getCategoryId() != null, AssistantFeedback::getCategoryId, dto.getCategoryId())
                            .eq(StrUtil.isNotBlank(dto.getStatus()), AssistantFeedback::getStatus, AssistantTicketStatus.normalize(dto.getStatus()))
                            .eq(dto.getIsPinned() != null, AssistantFeedback::getIsPinned, dto.getIsPinned())
                            .eq(dto.getUserId() != null, AssistantFeedback::getUserId, dto.getUserId())
                            .and(StrUtil.isNotBlank(dto.getKeyword()), wrapper -> wrapper
                                    .like(AssistantFeedback::getTitle, dto.getKeyword())
                                    .or()
                                    .like(AssistantFeedback::getContent, dto.getKeyword()))
                            .last(buildOrderBySql(dto.getSortType()))
                            .page(new Page<>(dto.getPageNum(), dto.getPageSize()));
                    return page;
                }
            }
        }

        return lambdaQuery()
                .in(filteredFeedbackIds != null && !filteredFeedbackIds.isEmpty(), AssistantFeedback::getId, filteredFeedbackIds)
                .eq(categoryIdToQuery != null, AssistantFeedback::getCategoryId, categoryIdToQuery)
                .eq(StrUtil.isNotBlank(dto.getStatus()), AssistantFeedback::getStatus, AssistantTicketStatus.normalize(dto.getStatus()))
                .eq(dto.getIsPinned() != null, AssistantFeedback::getIsPinned, dto.getIsPinned())
                .eq(dto.getUserId() != null, AssistantFeedback::getUserId, dto.getUserId())
                .and(StrUtil.isNotBlank(dto.getKeyword()), wrapper -> wrapper
                        .like(AssistantFeedback::getTitle, dto.getKeyword())
                        .or()
                        .like(AssistantFeedback::getContent, dto.getKeyword()))
                .last(buildOrderBySql(dto.getSortType()))
                .page(new Page<>(dto.getPageNum(), dto.getPageSize()));
    }

    /**
     * 构建排序 SQL
     */
    private String buildOrderBySql(String sortType) {
        if (StrUtil.isBlank(sortType)) {
            sortType = "hot"; // 默认按最热排序
        }

        // 置顶排序始终在最前面
        String pinOrderSql = "ORDER BY is_pinned DESC, pin_order ASC, ";

        if ("hot".equals(sortType)) {
            // 热度排序：使用预计算的热度分字段
            // 热度分计算规则见 FeedbackHotScoreCalculator
            return pinOrderSql + "hot_score DESC, create_time DESC";
        } else if ("comment".equals(sortType)) {
            // 最多评论
            return pinOrderSql + "comment_count DESC, create_time DESC";
        } else if ("latest".equals(sortType)) {
            // 最新
            return pinOrderSql + "create_time DESC";
        } else {
            // 默认最热
            return pinOrderSql + "hot_score DESC, create_time DESC";
        }
    }

    @Override
    public AssistantFeedbackDetailVO getFeedbackDetail(Long feedbackId) {
        AssistantFeedback feedback = getById(feedbackId);
        if (feedback == null || feedback.getDeleteFlag() == 1) {
            throw new ServiceException("反馈不存在");
        }

        // 增加浏览次数
        incrementViewCount(feedbackId);

        // 转换为详情 VO
        AssistantFeedbackDetailVO detailVO = BeanUtil.copyProperties(feedback, AssistantFeedbackDetailVO.class);
        fillUserInfo(detailVO, feedback.getUserId());
        fillHandlerInfo(detailVO, feedback.getHandlerId(), feedback.getHandlerName());
        detailVO.setStatus(AssistantTicketStatus.normalize(feedback.getStatus()));
        detailVO.setStatusText(AssistantTicketStatus.getDescriptionByCode(detailVO.getStatus()));
        List<AssistantFeedbackProcessRecordVO> processRecords = listProcessRecordsSafely(feedbackId);
        detailVO.setProcessRecords(processRecords);
        fillProcessSummary(detailVO, processRecords);

        // 填充分类信息
        AssistantFeedbackCategory category = categoryService.getById(feedback.getCategoryId());
        if (category != null) {
            detailVO.setCategoryCode(category.getCode());
            detailVO.setCategoryName(category.getName());
            detailVO.setCategoryIcon(category.getIcon());
            detailVO.setAllowComment(category.getAllowComment());
        }
        detailVO.setTags(buildFeedbackTagMap(Collections.singletonList(feedback)).getOrDefault(feedbackId, Collections.emptyList()));
        detailVO.setViewCount((feedback.getViewCount() == null ? 0 : feedback.getViewCount()) + 1);

        // 填充当前用户的互动状态（点赞、收藏）
        try {
            Long userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                detailVO.setIsLiked(false);
                detailVO.setIsFavorited(false);
                return detailVO;
            }
            log.info("[getFeedbackDetail] 当前用户ID: {}, 反馈ID: {}", userId, feedbackId);
            boolean isLiked = likeService.isLiked(feedbackId, userId);
            boolean isFavorited = favoriteService.isFavorited(feedbackId, userId);
            log.info("[getFeedbackDetail] 点赞状态: {}, 收藏状态: {}", isLiked, isFavorited);
            detailVO.setIsLiked(isLiked);
            detailVO.setIsFavorited(isFavorited);
        } catch (Exception e) {
            // 未登录或获取用户信息失败，默认为未点赞、未收藏
            log.warn("[getFeedbackDetail] 获取用户互动状态失败: {}", e.getMessage());
            detailVO.setIsLiked(false);
            detailVO.setIsFavorited(false);
        }

        return detailVO;
    }

    @Override
    public List<AssistantFeedbackProcessRecordVO> listFeedbackProcessRecords(Long feedbackId) {
        AssistantFeedback feedback = getById(feedbackId);
        if (feedback == null || feedback.getDeleteFlag() == 1) {
            throw new ServiceException("反馈不存在");
        }
        return listProcessRecordsSafely(feedbackId);
    }

    private void fillUserInfo(AssistantFeedbackVO feedbackVO, Long userId) {
        OshUser user = getUserById(userId);
        if (user == null) {
            return;
        }
        feedbackVO.setUserName(StrUtil.isNotBlank(user.getUsername()) ? user.getUsername() : "匿名用户");
        feedbackVO.setUserAvatar(user.getAvatar());
    }

    private AssistantFeedbackListVO toListVO(AssistantFeedback feedback,
                                             Map<Long, OshUser> userMap,
                                             Map<Long, AssistantFeedbackCategory> categoryMap,
                                             Map<Long, List<AssistantFeedbackTagVO>> feedbackTagMap) {
        AssistantFeedbackListVO vo = new AssistantFeedbackListVO();
        BeanUtils.copyProperties(feedback, vo);
        vo.setStatus(AssistantTicketStatus.normalize(feedback.getStatus()));
        vo.setStatusText(AssistantTicketStatus.getDescriptionByCode(vo.getStatus()));
        fillUserInfo(vo, userMap.get(feedback.getUserId()));

        AssistantFeedbackCategory category = categoryMap.get(feedback.getCategoryId());
        if (category != null) {
            vo.setCategoryCode(category.getCode());
            vo.setCategoryName(category.getName());
            vo.setCategoryIcon(category.getIcon());
        }

        vo.setContentPreview(buildContentPreview(feedback.getContent()));
        vo.setTags(feedbackTagMap.getOrDefault(feedback.getId(), Collections.emptyList()));
        return vo;
    }

    private void fillUserInfo(AssistantFeedbackListVO feedbackVO, OshUser user) {
        if (user == null) {
            return;
        }
        feedbackVO.setUserName(StrUtil.isNotBlank(user.getUsername()) ? user.getUsername() : "匿名用户");
        feedbackVO.setUserAvatar(user.getAvatar());
    }

    private void fillUserInfo(AssistantFeedbackDetailVO detailVO, Long userId) {
        OshUser user = getUserById(userId);
        if (user == null) {
            return;
        }
        detailVO.setUserName(getUserDisplayName(user, detailVO.getUserName()));
        detailVO.setUserAvatar(user.getAvatar());
    }

    private void fillHandlerInfo(AssistantFeedbackDetailVO detailVO, Long handlerId, String fallbackHandlerName) {
        OshUser handler = getUserById(handlerId);
        detailVO.setHandlerName(getUserDisplayName(handler, fallbackHandlerName));
    }

    private void fillHandlerInfo(AssistantFeedbackVO feedbackVO, Long handlerId, String fallbackHandlerName) {
        OshUser handler = getUserById(handlerId);
        feedbackVO.setHandlerName(getUserDisplayName(handler, fallbackHandlerName));
    }

    private String buildContentPreview(String content) {
        if (StrUtil.isBlank(content)) {
            return "";
        }
        String normalizedContent = StrUtil.replace(content.trim(), "\n", " ");
        return normalizedContent.length() <= 100 ? normalizedContent : normalizedContent.substring(0, 100);
    }

    private Map<Long, OshUser> buildUserMap(List<AssistantFeedback> feedbackList) {
        Set<Long> userIds = feedbackList.stream()
                .map(AssistantFeedback::getUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return oshUserMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(OshUser::getId, Function.identity()));
    }

    private Map<Long, AssistantFeedbackCategory> buildCategoryMap(List<AssistantFeedback> feedbackList) {
        Set<Long> categoryIds = feedbackList.stream()
                .map(AssistantFeedback::getCategoryId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return categoryService.lambdaQuery()
                .in(AssistantFeedbackCategory::getId, categoryIds)
                .list()
                .stream()
                .collect(Collectors.toMap(AssistantFeedbackCategory::getId, Function.identity()));
    }

    private Map<Long, List<AssistantFeedbackTagVO>> buildFeedbackTagMap(List<AssistantFeedback> feedbackList) {
        Set<Long> feedbackIds = feedbackList.stream()
                .map(AssistantFeedback::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return feedbackTagService.mapFeedbackTags(feedbackIds);
    }

    private Set<Long> filterFeedbackIdsByTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return null;
        }
        return feedbackTagService.listFeedbackIdsByTagIds(tagIds);
    }

    private Set<Long> filterFeedbackIdsByQueryMode(AssistantFeedbackPageDTO dto) {
        String queryMode = normalizeQueryMode(dto.getQueryMode());
        if ("all".equals(queryMode)) {
            return null;
        }
        Long userId = dto.getUserId();
        if (userId == null) {
            return Collections.emptySet();
        }
        if ("mine".equals(queryMode)) {
            return lambdaQuery()
                    .select(AssistantFeedback::getId)
                    .eq(AssistantFeedback::getUserId, userId)
                    .eq(AssistantFeedback::getDeleteFlag, (byte) 0)
                    .list()
                    .stream()
                    .map(AssistantFeedback::getId)
                    .collect(Collectors.toSet());
        }
        if ("favorite".equals(queryMode)) {
            return favoriteService.listFavoriteFeedbackIds(userId);
        }
        return null;
    }

    private String normalizeQueryMode(String queryMode) {
        if (StrUtil.isBlank(queryMode)) {
            return "all";
        }
        return queryMode.trim().toLowerCase();
    }

    private Set<Long> intersectFeedbackIds(Set<Long> leftIds, Set<Long> rightIds) {
        if (leftIds == null) {
            return rightIds;
        }
        if (rightIds == null) {
            return leftIds;
        }
        return leftIds.stream().filter(rightIds::contains).collect(Collectors.toSet());
    }

    private boolean shouldReturnEmptyPage(Set<Long> tagFilteredFeedbackIds, Set<Long> scopedFeedbackIds, Set<Long> filteredFeedbackIds) {
        boolean tagFilterEmpty = tagFilteredFeedbackIds != null && tagFilteredFeedbackIds.isEmpty();
        boolean scopeFilterEmpty = scopedFeedbackIds != null && scopedFeedbackIds.isEmpty();
        boolean intersectionEmpty = filteredFeedbackIds != null && filteredFeedbackIds.isEmpty();
        return tagFilterEmpty || scopeFilterEmpty || intersectionEmpty;
    }

    private List<AssistantFeedbackTagVO> resolveFeedbackTags(Long feedbackId, Map<Long, List<AssistantFeedbackTagVO>> feedbackTagMap) {
        if (feedbackTagMap != null) {
            return feedbackTagMap.getOrDefault(feedbackId, Collections.emptyList());
        }
        return feedbackTagService.mapFeedbackTags(Collections.singleton(feedbackId))
                .getOrDefault(feedbackId, Collections.emptyList());
    }

    private OshUser getUserById(Long userId) {
        return userId == null ? null : oshUserMapper.selectById(userId);
    }

    private String getUserDisplayName(OshUser user, String fallbackName) {
        if (user == null) {
            return fallbackName;
        }
        return StrUtil.isNotBlank(user.getUsername()) ? user.getUsername() : "匿名用户";
    }

    private void safeCreateProcessRecord(Long feedbackId, String fromStatus, String toStatus,
                                         Long operatorId, String operatorName, String remark) {
        try {
            processRecordService.createRecord(feedbackId, fromStatus, toStatus, operatorId, operatorName, remark);
        } catch (Exception exception) {
            log.warn("写入反馈处理记录失败，feedbackId={}, message={}", feedbackId, exception.getMessage());
        }
    }

    private List<AssistantFeedbackProcessRecordVO> listProcessRecordsSafely(Long feedbackId) {
        try {
            return processRecordService.listByFeedbackId(feedbackId);
        } catch (Exception exception) {
            log.warn("查询反馈处理记录失败，feedbackId={}, message={}", feedbackId, exception.getMessage());
            return Collections.emptyList();
        }
    }

    private void fillProcessSummary(AssistantFeedbackVO feedbackVO, List<AssistantFeedbackProcessRecordVO> processRecords) {
        if (processRecords == null || processRecords.isEmpty()) {
            return;
        }
        AssistantFeedbackProcessRecordVO latestRecord = processRecords.stream()
                .filter(record -> record.getCreateTime() != null)
                .max(Comparator.comparing(AssistantFeedbackProcessRecordVO::getCreateTime))
                .orElse(processRecords.get(processRecords.size() - 1));
        feedbackVO.setHandledTime(latestRecord.getCreateTime());
        if (AssistantTicketStatus.CLOSED.getCode().equals(latestRecord.getToStatus())) {
            feedbackVO.setCloseReason(latestRecord.getRemark());
        }
        if (StrUtil.isBlank(feedbackVO.getHandlerName())) {
            feedbackVO.setHandlerName(latestRecord.getOperatorName());
        }
    }

    private void fillProcessSummary(AssistantFeedbackDetailVO detailVO, List<AssistantFeedbackProcessRecordVO> processRecords) {
        if (processRecords == null || processRecords.isEmpty()) {
            return;
        }
        AssistantFeedbackProcessRecordVO latestRecord = processRecords.stream()
                .filter(record -> record.getCreateTime() != null)
                .max(Comparator.comparing(AssistantFeedbackProcessRecordVO::getCreateTime))
                .orElse(processRecords.get(processRecords.size() - 1));
        detailVO.setHandledTime(latestRecord.getCreateTime());
        if (AssistantTicketStatus.CLOSED.getCode().equals(latestRecord.getToStatus())) {
            detailVO.setCloseReason(latestRecord.getRemark());
        }
        if (StrUtil.isBlank(detailVO.getHandlerName())) {
            detailVO.setHandlerName(latestRecord.getOperatorName());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pinFeedback(Long feedbackId, Integer pinOrder) {
        if (pinOrder < 1 || pinOrder > 3) {
            throw new ServiceException("置顶排序必须在 1-3 之间");
        }

        // 检查该排序位置是否已被占用
        Long count = lambdaQuery()
                .eq(AssistantFeedback::getIsPinned, 1)
                .eq(AssistantFeedback::getPinOrder, pinOrder)
                .ne(AssistantFeedback::getId, feedbackId)
                .count();

        if (count > 0) {
            throw new ServiceException("该置顶位置已被占用，请先取消其他反馈的置顶");
        }

        AssistantFeedback feedback = new AssistantFeedback();
        feedback.setId(feedbackId);
        feedback.setIsPinned(1);
        feedback.setPinOrder(pinOrder);
        return updateById(feedback);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unpinFeedback(Long feedbackId) {
        AssistantFeedback feedback = new AssistantFeedback();
        feedback.setId(feedbackId);
        feedback.setIsPinned(0);
        feedback.setPinOrder(0);
        return updateById(feedback);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementCommentCount(Long feedbackId) {
        // 先查询当前数据
        AssistantFeedback feedback = getById(feedbackId);
        if (feedback == null) {
            return;
        }

        // 计算新的评论数和热度分
        int newCommentCount = (feedback.getCommentCount() == null ? 0 : feedback.getCommentCount()) + 1;
        int newHotScore = calculateHotScore(feedback, null, null, newCommentCount, null);

        // 使用 entity 方式更新，确保自动填充生效
        AssistantFeedback updateEntity = new AssistantFeedback();
        updateEntity.setId(feedbackId);
        updateEntity.setCommentCount(newCommentCount);
        updateEntity.setHotScore(newHotScore);
        updateById(updateEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decrementCommentCount(Long feedbackId) {
        // 先查询当前数据
        AssistantFeedback feedback = getById(feedbackId);
        if (feedback == null || feedback.getCommentCount() == null || feedback.getCommentCount() <= 0) {
            return;
        }

        // 计算新的评论数和热度分
        int newCommentCount = feedback.getCommentCount() - 1;
        int newHotScore = calculateHotScore(feedback, null, null, newCommentCount, null);

        // 使用 entity 方式更新，确保自动填充生效
        AssistantFeedback updateEntity = new AssistantFeedback();
        updateEntity.setId(feedbackId);
        updateEntity.setCommentCount(newCommentCount);
        updateEntity.setHotScore(newHotScore);
        updateById(updateEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementViewCount(Long feedbackId) {
        // 先查询当前数据
        AssistantFeedback feedback = getById(feedbackId);
        if (feedback == null) {
            return;
        }

        // 计算新的浏览数和热度分
        int newViewCount = (feedback.getViewCount() == null ? 0 : feedback.getViewCount()) + 1;
        int newHotScore = calculateHotScore(feedback, null, null, null, newViewCount);

        // 使用 entity 方式更新，确保自动填充生效
        AssistantFeedback updateEntity = new AssistantFeedback();
        updateEntity.setId(feedbackId);
        updateEntity.setViewCount(newViewCount);
        updateEntity.setHotScore(newHotScore);
        updateById(updateEntity);
    }

    /**
     * 计算热度分（支持部分字段更新）
     *
     * @param feedback      原反馈数据
     * @param newLikeCount      新点赞数（null表示不变）
     * @param newFavoriteCount  新收藏数（null表示不变）
     * @param newCommentCount   新评论数（null表示不变）
     * @param newViewCount      新浏览数（null表示不变）
     * @return 新的热度分
     */
    private int calculateHotScore(AssistantFeedback feedback, Integer newLikeCount, Integer newFavoriteCount,
                                  Integer newCommentCount, Integer newViewCount) {
        int likeCount = newLikeCount != null ? newLikeCount : (feedback.getLikeCount() == null ? 0 : feedback.getLikeCount());
        int favoriteCount = newFavoriteCount != null ? newFavoriteCount : (feedback.getFavoriteCount() == null ? 0 : feedback.getFavoriteCount());
        int commentCount = newCommentCount != null ? newCommentCount : (feedback.getCommentCount() == null ? 0 : feedback.getCommentCount());
        int viewCount = newViewCount != null ? newViewCount : (feedback.getViewCount() == null ? 0 : feedback.getViewCount());

        return FeedbackHotScoreCalculator.calculate(likeCount, favoriteCount, commentCount, viewCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFeedback(Long feedbackId) {
        AssistantFeedback feedback = new AssistantFeedback();
        feedback.setId(feedbackId);
        return removeById(feedback);
    }
}