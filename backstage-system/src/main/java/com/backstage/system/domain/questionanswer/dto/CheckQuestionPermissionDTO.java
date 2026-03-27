package com.backstage.system.domain.questionanswer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:46
 */
@ApiModel(description = "前端传递的校验用户权限请求实体类")
public class CheckQuestionPermissionDTO {
    @ApiModelProperty(
            value = "资源类型",
            required = true
    )
    private String resource_type;
    @ApiModelProperty(
            value = "资源id",
            required = true
    )
    private String resource_id;

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    @Override
    public String toString() {
        return "CheckQuestionPermissionDTO{" +
                ", resource_type='" + resource_type + '\'' +
                ", resource_id='" + resource_id + '\'' +
                '}';
    }
}
