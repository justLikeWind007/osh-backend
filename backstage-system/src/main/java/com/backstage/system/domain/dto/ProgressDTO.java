package com.backstage.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 学习进度更新 DTO
 * 用于用户提交视频播放进度
 * 
 * @author ruoyi
 * @date 2026-03-26
 */
@ApiModel(description = "学习进度更新请求")
public class ProgressDTO {
    
    /** 进度百分比（0-100） */
    @ApiModelProperty(value = "进度百分比（0-100）", required = true)
    private Integer progress;
    
    /** 上次播放位置（秒） */
    @ApiModelProperty(value = "上次播放位置（秒）", required = true)
    private Integer lastPosition;
    
    /** 本次学习时长（秒） */
    @ApiModelProperty("本次学习时长（秒）")
    private Integer learnTime;
    
    /** 学习状态：0-未开始 1-学习中 2-有疑问 3-已完成 */
    @ApiModelProperty("学习状态：0-未开始 1-学习中 2-有疑问 3-已完成")
    private Integer status;

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Integer lastPosition) {
        this.lastPosition = lastPosition;
    }

    public Integer getLearnTime() {
        return learnTime;
    }

    public void setLearnTime(Integer learnTime) {
        this.learnTime = learnTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
