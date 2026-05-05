package com.backstage.system.domain.assistant.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 助手反馈详情响应 VO
 *
 * @author backstage
 */
@Data
public class AssistantFeedbackDetailVO {

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
     * 是否允许评论（0-否 1-是）
     */
    private Integer allowComment;

    /**
     * 工单编号
     */
    private String ticketNo;

    /**
     * 反馈标题
     */
    private String title;

    /**
     * 反馈详细内容
     */
    private String content;

    /**
     * 反馈状态（PENDING、PROCESSING、RESOLVED、CLOSED）
     */
    private String status;

    /**
     * 状态文案
     */
    private String statusText;

    /**
     * 处理结果说明
     */
    private String result;

    /**
     * 反馈来源页面路径
     */
    private String pagePath;

    /**
     * 处理人 ID
     */
    private Long handlerId;

    /**
     * 处理人名称
     */
    private String handlerName;

    /**
     * 最近处理时间
     */
    private LocalDateTime handledTime;

    /**
     * 关闭原因
     */
    private String closeReason;

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

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;

    /**
     * 当前用户是否已收藏
     */
    private Boolean isFavorited;

    /**
     * 处理时间线
     */
    private List<AssistantFeedbackProcessRecordVO> processRecords;
}
