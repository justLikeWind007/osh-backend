package com.backstage.system.domain.assistant.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * AI 助手反馈创建请求 DTO
 *
 * @author backstage
 */
@Data
public class AssistantFeedbackCreateDTO {

    /**
     * 分类 ID
     */
    @NotNull(message = "分类不能为空")
    private Long categoryId;

    /**
     * 反馈标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 128, message = "标题不能超过128个字符")
    private String title;

    /**
     * 反馈详细内容
     */
    @NotBlank(message = "反馈内容不能为空")
    @Size(max = 1000, message = "反馈内容不能超过1000个字符")
    private String content;

    /**
     * 反馈来源页面路径
     */
    @Size(max = 255, message = "页面路径不能超过255个字符")
    private String pagePath;
}
