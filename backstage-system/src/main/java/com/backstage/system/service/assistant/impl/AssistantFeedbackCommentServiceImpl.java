package com.backstage.system.service.assistant.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.assistant.AssistantFeedback;
import com.backstage.system.domain.assistant.AssistantFeedbackComment;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackCommentDTO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackCommentVO;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.assistant.AssistantFeedbackCommentMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.service.assistant.IAssistantFeedbackCategoryService;
import com.backstage.system.service.assistant.IAssistantFeedbackCommentService;
import com.backstage.system.service.assistant.IAssistantFeedbackService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AI 助手反馈评论 Service 实现
 *
 * @author backstage
 */
@Service
public class AssistantFeedbackCommentServiceImpl
        extends ServiceImpl<AssistantFeedbackCommentMapper, AssistantFeedbackComment>
        implements IAssistantFeedbackCommentService {

    public AssistantFeedbackCommentServiceImpl(IAssistantFeedbackService feedbackService, IAssistantFeedbackCategoryService categoryService, OshUserMapper oshUserMapper) {
        this.feedbackService = feedbackService;
        this.categoryService = categoryService;
        this.oshUserMapper = oshUserMapper;
    }

    private final IAssistantFeedbackService feedbackService;
    private final IAssistantFeedbackCategoryService categoryService;
    private final OshUserMapper oshUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComment(AssistantFeedbackCommentDTO dto, Long userId) {
        AssistantFeedback feedback = getActiveFeedback(dto.getFeedbackId());
        if (feedback == null) {
            throw new ServiceException("反馈不存在");
        }
        if (!categoryService.isCommentAllowed(feedback.getCategoryId())) {
            throw new ServiceException("当前反馈不允许评论");
        }

        // 构建评论实体
        AssistantFeedbackComment comment = new AssistantFeedbackComment();
        comment.setFeedbackId(dto.getFeedbackId());
        comment.setUserId(userId);
        comment.setContent(dto.getContent().trim());
        comment.setIsAdminReply(Boolean.TRUE.equals(dto.getIsAdminReply()) ? 1 : 0);

        // 判断是一级评论还是二级评�?
        Long parentId = ObjectUtil.defaultIfNull(dto.getParentId(), 0L);
        comment.setParentId(parentId);

        if (parentId == 0) {
            // 一级评�?
            comment.setRootId(0L);
            comment.setCommentLevel(1);
        } else {
            // 二级评论
            AssistantFeedbackComment parentComment = getById(parentId);
            if (parentComment == null) {
                throw new ServiceException("父评论不存在");
            }
            if (!dto.getFeedbackId().equals(parentComment.getFeedbackId())) {
                throw new ServiceException("父评论与当前反馈不匹配");
            }

            // 确定根评�?ID
            Long rootId = parentComment.getCommentLevel() == 1 ? parentId : parentComment.getRootId();
            comment.setRootId(rootId);
            comment.setCommentLevel(2);
            comment.setReplyToUserId(parentComment.getUserId());
            comment.setReplyToUserName(resolveReplyToUserName(parentComment));
        }

        // 保存评论
        save(comment);

        // 更新反馈的评论数�?
        feedbackService.incrementCommentCount(dto.getFeedbackId());

        return comment.getId();
    }

    @Override
    public List<AssistantFeedbackCommentVO> listCommentsByFeedbackId(Long feedbackId, Integer pageNum, Integer pageSize) {
        if (getActiveFeedback(feedbackId) == null) {
            throw new ServiceException("反馈不存在");
        }
        // 查询一级评论（分页）
        Page<AssistantFeedbackComment> page = lambdaQuery()
                .eq(AssistantFeedbackComment::getFeedbackId, feedbackId)
                .eq(AssistantFeedbackComment::getCommentLevel, 1)
                .orderByAsc(AssistantFeedbackComment::getCreateTime)
                .page(new Page<>(pageNum, pageSize));

        List<AssistantFeedbackComment> rootComments = page.getRecords();
        if (rootComments.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询所有一级评论的二级评论
        List<Long> rootIds = rootComments.stream()
                .map(AssistantFeedbackComment::getId)
                .collect(Collectors.toList());

        List<AssistantFeedbackComment> replies = lambdaQuery()
                .in(AssistantFeedbackComment::getRootId, rootIds)
                .eq(AssistantFeedbackComment::getCommentLevel, 2)
                .orderByAsc(AssistantFeedbackComment::getCreateTime)
                .list();

        Map<Long, OshUser> userMap = buildUserMap(rootComments, replies);

        List<AssistantFeedbackCommentVO> result = page.getRecords().stream()
                .map(comment -> toCommentVO(comment, userMap))
                .collect(Collectors.toList());

        // 按根评论 ID 分组
        Map<Long, List<AssistantFeedbackCommentVO>> repliesMap = replies.stream()
                .map(reply -> toCommentVO(reply, userMap))
                .collect(Collectors.groupingBy(AssistantFeedbackCommentVO::getRootId));

        // 将二级评论填充到一级评论中
        result.forEach(comment -> comment.setReplies(repliesMap.get(comment.getId())));

        return result;
    }

    private AssistantFeedbackCommentVO toCommentVO(AssistantFeedbackComment comment, Map<Long, OshUser> userMap) {
        AssistantFeedbackCommentVO commentVO = new AssistantFeedbackCommentVO();
        BeanUtil.copyProperties(comment, commentVO);

        OshUser user = userMap.get(comment.getUserId());
        if (user != null) {
            commentVO.setUserName(user.getUsername() != null && !user.getUsername().isEmpty() ? user.getUsername() : user.getUsername());
            commentVO.setUserAvatar(user.getAvatar());
        }
        return commentVO;
    }

    private Map<Long, OshUser> buildUserMap(List<AssistantFeedbackComment> rootComments, List<AssistantFeedbackComment> replies) {
        Set<Long> userIds = rootComments.stream()
                .map(AssistantFeedbackComment::getUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        userIds.addAll(replies.stream()
                .map(AssistantFeedbackComment::getUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet()));
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return oshUserMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(OshUser::getId, Function.identity()));
    }

    private AssistantFeedback getActiveFeedback(Long feedbackId) {
        return feedbackService.lambdaQuery()
                .eq(AssistantFeedback::getId, feedbackId)
                .eq(AssistantFeedback::getDeleteFlag, (byte) 0)
                .one();
    }

    private String resolveReplyToUserName(AssistantFeedbackComment parentComment) {
        OshUser replyToUser = parentComment.getUserId() == null ? null : oshUserMapper.selectById(parentComment.getUserId());
        if (replyToUser != null) {
            return StrUtil.isNotBlank(replyToUser.getNickname()) ? replyToUser.getNickname() : replyToUser.getUsername();
        }
        return StrUtil.blankToDefault(parentComment.getReplyToUserName(), "用户");
    }

    @Override
    public Long countByFeedbackId(Long feedbackId) {
        if (getActiveFeedback(feedbackId) == null) {
            return 0L;
        }
        return lambdaQuery()
                .eq(AssistantFeedbackComment::getFeedbackId, feedbackId)
                .count();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId, Long userId) {
        AssistantFeedbackComment comment = getById(commentId);
        if (comment == null) {
            throw new ServiceException("评论不存在");
        }

        // 只能删除自己的评�?
        if (!comment.getUserId().equals(userId)) {
            throw new ServiceException("无权删除他人评论");
        }

        // 逻辑删除
        boolean success = removeById(commentId);

        if (success) {
            // 更新反馈的评论数�?
            feedbackService.decrementCommentCount(comment.getFeedbackId());
        }

        return success;
    }
}
