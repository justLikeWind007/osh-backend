package com.backstage.system.domain.assistant.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 助手反馈列表响应 VO
 *
 * @author backstage
 */
@Data
public class AssistantFeedbackListVO {

    /**
     * 反馈 ID
     */
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 分类 ID
     */
    private Long categoryId;

    /**
     * 分类代码
     */
    private String categoryCode;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类图标
     */
    private String categoryIcon;

    /**
     * 反馈标题
     */
    private String title;

    /**
     * 反馈内容预览
     */
    private String contentPreview;

    /**
     * 反馈状态
     */
    private String status;

    /**
     * 状态文案
     */
    private String statusText;

    /**
     * 是否置顶（0-否 1-是）
     */
    private Integer isPinned;

    /**
     * 置顶排序（1-3）
     */
    private Integer pinOrder;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 收藏数量
     */
    private Integer favoriteCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
