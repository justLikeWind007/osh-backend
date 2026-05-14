package com.backstage.system.service.assistant.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.util.StrUtil;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.assistant.AssistantFeedbackCategory;
import com.backstage.system.domain.assistant.AssistantTicketStatus;
import com.backstage.system.domain.assistant.AssistantFeedback;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackCreateDTO;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackPageDTO;
import com.backstage.system.domain.assistant.dto.AssistantTicketQueryDTO;
import com.backstage.system.domain.assistant.dto.AssistantTicketStatusUpdateDTO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackDetailVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackProcessRecordVO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackTagVO;
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
import com.backstage.system.service.assistant.support.AssistantFeedbackQuerySupport;
import com.backstage.system.service.assistant.support.AssistantFeedbackViewAssembler;
import com.backstage.system.util.FeedbackHotScoreCalculator;
import com.backstage.system.utils.UserContextUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public AssistantFeedbackServiceImpl(IAssistantFeedbackCategoryService categoryService,
                                        IAssistantFeedbackLikeService likeService,
                                        IAssistantFeedbackFavoriteService favoriteService,
                                        IAssistantFeedbackProcessRecordService processRecordService,
                                        IAssistantFeedbackTagService feedbackTagService,
                                        OshUserMapper oshUserMapper,
                                        AssistantFeedbackQuerySupport feedbackQuerySupport,
                                        AssistantFeedbackViewAssembler feedbackViewAssembler) {
        this.categoryService = categoryService;
        this.likeService = likeService;
        this.favoriteService = favoriteService;
        this.processRecordService = processRecordService;
        this.feedbackTagService = feedbackTagService;
        this.oshUserMapper = oshUserMapper;
        this.feedbackQuerySupport = feedbackQuerySupport;
        this.feedbackViewAssembler = feedbackViewAssembler;
    }

    private final IAssistantFeedbackCategoryService categoryService;
    private final IAssistantFeedbackLikeService likeService;
    private final IAssistantFeedbackFavoriteService favoriteService;
    private final IAssistantFeedbackProcessRecordService processRecordService;
    private final IAssistantFeedbackTagService feedbackTagService;
    private final OshUserMapper oshUserMapper;
    private final AssistantFeedbackQuerySupport feedbackQuerySupport;
    private final AssistantFeedbackViewAssembler feedbackViewAssembler;

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
        return feedbackViewAssembler.toFeedbackVO(feedback);
    }

    @Override
    public TableDataInfo getMyFeedback(Long userId) {
        List<AssistantFeedback> list = lambdaQuery()
                .eq(AssistantFeedback::getUserId, userId)
                .eq(AssistantFeedback::getDeleteFlag, (byte) 0)
                .orderByDesc(AssistantFeedback::getCreateTime)
                .list();

        List<AssistantFeedbackVO> rows = list.stream()
                .map(feedbackViewAssembler::toFeedbackVO)
                .collect(Collectors.toList());
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

        List<AssistantFeedbackVO> rows = list.stream()
                .map(feedbackViewAssembler::toFeedbackVO)
                .collect(Collectors.toList());
        return new TableDataInfo(rows, new PageInfo<>(list).getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssistantFeedbackVO updateTicketStatus(Long ticketId, Long handlerId, AssistantTicketStatusUpdateDTO dto) {
        String targetStatus = AssistantTicketStatus.normalize(dto.getToStatus());
        if (!AssistantTicketStatus.isValid(targetStatus)) {
            throw new ServiceException("不支持的工单状态");
        }

        AssistantFeedback feedback = getActiveFeedback(ticketId);
        if (feedback == null) {
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
        return feedbackViewAssembler.toFeedbackVO(feedback);
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
        Page<AssistantFeedback> page = feedbackQuerySupport.pageFeedback(dto);
        Map<Long, List<AssistantFeedbackTagVO>> feedbackTagMap =
                feedbackViewAssembler.buildFeedbackTagMap(page.getRecords());

        List<AssistantFeedbackVO> rows = page.getRecords().stream()
                .map(feedback -> feedbackViewAssembler.toFeedbackVO(feedback, feedbackTagMap))
                .collect(Collectors.toList());

        return new TableDataInfo(rows, page.getTotal());
    }

    @Override
    public TableDataInfo pageFeedbackList(AssistantFeedbackPageDTO dto) {
        Page<AssistantFeedback> page = feedbackQuerySupport.pageFeedback(dto);
        Map<Long, OshUser> userMap = feedbackViewAssembler.buildUserMap(page.getRecords());
        Map<Long, AssistantFeedbackCategory> categoryMap = feedbackViewAssembler.buildCategoryMap(page.getRecords());
        Map<Long, List<AssistantFeedbackTagVO>> feedbackTagMap =
                feedbackViewAssembler.buildFeedbackTagMap(page.getRecords());

        List<?> rows = page.getRecords().stream()
                .map(feedback -> feedbackViewAssembler.toFeedbackListVO(feedback, userMap, categoryMap, feedbackTagMap))
                .collect(Collectors.toList());

        return new TableDataInfo(rows, page.getTotal());
    }

    @Override
    public AssistantFeedbackDetailVO getFeedbackDetail(Long feedbackId) {
        AssistantFeedback feedback = getActiveFeedback(feedbackId);
        if (feedback == null) {
            throw new ServiceException("反馈不存在");
        }

        // 增加浏览次数
        incrementViewCount(feedbackId);

        List<AssistantFeedbackProcessRecordVO> processRecords = feedbackViewAssembler.listProcessRecordsSafely(feedbackId);
        AssistantFeedbackDetailVO detailVO = feedbackViewAssembler.toFeedbackDetailVO(
                feedback,
                processRecords,
                (feedback.getViewCount() == null ? 0 : feedback.getViewCount()) + 1
        );

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
        AssistantFeedback feedback = getActiveFeedback(feedbackId);
        if (feedback == null) {
            throw new ServiceException("反馈不存在");
        }
        return feedbackViewAssembler.listProcessRecordsSafely(feedbackId);
    }

    private OshUser getUserById(Long userId) {
        return userId == null ? null : oshUserMapper.selectById(userId);
    }

    private AssistantFeedback getActiveFeedback(Long feedbackId) {
        return lambdaQuery()
                .eq(AssistantFeedback::getId, feedbackId)
                .eq(AssistantFeedback::getDeleteFlag, (byte) 0)
                .one();
    }

    private String getUserDisplayName(OshUser user, String fallbackName) {
        if (user == null) {
            return fallbackName;
        }
        return StrUtil.isNotBlank(user.getNickname()) ? user.getNickname() : user.getUsername();
    }

    private void safeCreateProcessRecord(Long feedbackId, String fromStatus, String toStatus,
                                         Long operatorId, String operatorName, String remark) {
        try {
            processRecordService.createRecord(feedbackId, fromStatus, toStatus, operatorId, operatorName, remark);
        } catch (Exception exception) {
            log.warn("写入反馈处理记录失败，feedbackId={}, message={}", feedbackId, exception.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pinFeedback(Long feedbackId, Integer pinOrder) {
        if (pinOrder < 1 || pinOrder > 3) {
            throw new ServiceException("置顶排序必须在 1-3 之间");
        }
        if (getActiveFeedback(feedbackId) == null) {
            throw new ServiceException("反馈不存在");
        }

        // 检查该排序位置是否已被占用
        Long count = lambdaQuery()
                .eq(AssistantFeedback::getDeleteFlag, (byte) 0)
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
        if (getActiveFeedback(feedbackId) == null) {
            throw new ServiceException("反馈不存在");
        }
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
        AssistantFeedback feedback = getActiveFeedback(feedbackId);
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
        AssistantFeedback feedback = getActiveFeedback(feedbackId);
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
        AssistantFeedback feedback = getActiveFeedback(feedbackId);
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
        AssistantFeedback feedback = getActiveFeedback(feedbackId);
        if (feedback == null) {
            throw new ServiceException("反馈不存在");
        }
        feedback.setDeleted(true);
        return updateById(feedback);
    }
}
