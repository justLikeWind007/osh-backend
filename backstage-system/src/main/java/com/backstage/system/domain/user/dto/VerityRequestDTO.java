package com.backstage.system.domain.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 18:41
 */
@ApiModel(description = "包含唯一标识的实体类")
public class VerityRequestDTO {
    @ApiModelProperty(
            value = "用户的唯一标识",
            required = true
    )
    private String uniqueId;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public String toString() {
        return "VerityRequestDTO{" +
                "uniqueId='" + uniqueId + '\'' +
                '}';
    }
}
