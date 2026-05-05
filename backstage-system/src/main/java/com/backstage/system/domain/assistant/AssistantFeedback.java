package com.backstage.system.domain.assistant;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * AI 助手反馈工单实体
 *
 * @author backstage
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("assistant_feedback")
public class AssistantFeedback extends OSHBaseEntity {

    /**
     * 工单 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 提交用户 ID
     */
    private Long userId;

    /**
     * 分类 ID
     */
    private Long categoryId;

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
     * 是否置顶（0-否 1-是）
     */
    private Integer isPinned;

    /**
     * 置顶排序（1-3，0 表示不置顶）
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
    @TableField(exist = false)
    private String handlerName;

    /**
     * 最近处理时间
     */
    @TableField(exist = false)
    private LocalDateTime handledTime;

    /**
     * 关闭原因
     */
    @TableField(exist = false)
    private String closeReason;

    /**
     * 热度分（冗余字段，用于排序）
     * 计算方式：互动分 * 4 + 有效浏览 * 1
     * 有效浏览：超过互动分20倍的部分只算10%
     */
    private Integer hotScore;
}
