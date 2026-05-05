package com.backstage.system.service.assistant.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.assistant.AssistantFeedbackComment;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackCommentDTO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackCommentVO;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.assistant.AssistantFeedbackCommentMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.service.assistant.IAssistantFeedbackCommentService;
import com.backstage.system.service.assistant.IAssistantFeedbackService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AssistantFeedbackCommentServiceImpl
        extends ServiceImpl<AssistantFeedbackCommentMapper, AssistantFeedbackComment>
        implements IAssistantFeedbackCommentService {

    private final IAssistantFeedbackService feedbackService;
    private final OshUserMapper oshUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComment(AssistantFeedbackCommentDTO dto, Long userId) {
        // 构建评论实体
        AssistantFeedbackComment comment = new AssistantFeedbackComment();
        comment.setFeedbackId(dto.getFeedbackId());
        comment.setUserId(userId);
        comment.setContent(dto.getContent());

        // 判断是一级评论还是二级评论
        Long parentId = ObjectUtil.defaultIfNull(dto.getParentId(), 0L);
        comment.setParentId(parentId);

        if (parentId == 0) {
            // 一级评论
            comment.setRootId(0L);
            comment.setCommentLevel(1);
        } else {
            // 二级评论
            AssistantFeedbackComment parentComment = getById(parentId);
            if (parentComment == null) {
                throw new ServiceException("父评论不存在");
            }

            // 确定根评论 ID
            Long rootId = parentComment.getCommentLevel() == 1 ? parentId : parentComment.getRootId();
            comment.setRootId(rootId);
            comment.setCommentLevel(2);
            comment.setReplyToUserId(dto.getReplyToUserId());
            comment.setReplyToUserName(dto.getReplyToUserName());
        }

        // 保存评论
        save(comment);

        // 更新反馈的评论数量
        feedbackService.incrementCommentCount(dto.getFeedbackId());

        return comment.getId();
    }

    @Override
    public List<AssistantFeedbackCommentVO> listCommentsByFeedbackId(Long feedbackId, Integer pageNum, Integer pageSize) {
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
            commentVO.setUserName(user.getNickname() != null && !user.getNickname().isEmpty() ? user.getNickname() : user.getUsername());
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

    @Override
    public Long countByFeedbackId(Long feedbackId) {
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

        // 只能删除自己的评论
        if (!comment.getUserId().equals(userId)) {
            throw new ServiceException("无权删除他人评论");
        }

        // 逻辑删除
        boolean success = removeById(commentId);

        if (success) {
            // 更新反馈的评论数量
            feedbackService.decrementCommentCount(comment.getFeedbackId());
        }

        return success;
    }
}
