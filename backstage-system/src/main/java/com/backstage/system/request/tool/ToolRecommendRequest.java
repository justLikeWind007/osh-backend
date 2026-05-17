package com.backstage.system.request.tool;

import com.backstage.common.request.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * 工具推荐列表请求
 */
@ApiModel(description = "工具推荐列表请求")
public class ToolRecommendRequest extends PageRequest {

    @ApiModelProperty(value = "推荐类型：HOT-最近火热，LATEST-最新发布", example = "HOT")
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = StringUtils.upperCase(StringUtils.trimToNull(type));
    }
}
