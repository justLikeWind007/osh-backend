package com.backstage.system.domain.assistant.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 修改处理记录备注请求 DTO
 *
 * @author backstage
 */
public class UpdateRemarkDTO {

    /**
     * 备注内容
     */
    @NotBlank(message = "备注内容不能为空")
    @Size(max = 1000, message = "备注内容不能超过1000个字符")
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
