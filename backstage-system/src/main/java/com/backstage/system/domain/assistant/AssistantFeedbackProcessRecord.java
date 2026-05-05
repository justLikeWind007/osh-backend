package com.backstage.system.domain.assistant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈处理记录实体
 *
 * @author backstage
 */
@Data
@TableName("assistant_feedback_process_record")
public class AssistantFeedbackProcessRecord {

    /**
     * 记录 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 反馈 ID
     */
    private Long feedbackId;

    /**
     * 变更前状态
     */
    private String fromStatus;

    /**
     * 变更后状态
     */
    private String toStatus;

    /**
     * 操作人 ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 处理说明
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 更新人
     */
    private Long updateBy;

    /**
     * 删除标记（0-未删除 1-已删除）
     */
    @TableLogic
    private Integer deleteFlag;
}
