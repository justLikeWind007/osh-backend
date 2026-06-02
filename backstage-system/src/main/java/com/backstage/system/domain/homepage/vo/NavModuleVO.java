package com.backstage.system.domain.homepage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 首页导航模块 VO
 * <p>
 * 用于"查看全部"按钮的跳转路径，前端通过 key 匹配对应模块。
 *
 * @author jayTatum
 */
@ApiModel(description = "首页导航模块")
public class NavModuleVO {

    @ApiModelProperty("模块标识 key（与前端 getNavPath 的 key 对应）")
    private String key;

    @ApiModelProperty("模块名称")
    private String name;

    @ApiModelProperty("列表页前端路径（查看全部跳转）")
    private String frontPath;

    public NavModuleVO() {}

    public NavModuleVO(String key, String name, String frontPath) {
        this.key = key;
        this.name = name;
        this.frontPath = frontPath;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFrontPath() { return frontPath; }
    public void setFrontPath(String frontPath) { this.frontPath = frontPath; }
}
