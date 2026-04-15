package com.backstage.system.domain.course;

import com.backstage.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 课程标签对象 osh_course_tag
 *
 * @author ruoyi
 * @date 2026-03-24
 */
@ApiModel(description = "课程标签")
public class OshCourseTag
{
    private static final long serialVersionUID = 1L;

    /** 标签ID */
    @ApiModelProperty("标签ID")
    private Long id;

    /** 标签名称 */
    @Excel(name = "标签名称")
    @ApiModelProperty("标签名称")
    private String name;

    /** 排序权重 */
    @Excel(name = "排序权重")
    @ApiModelProperty("排序权重")
    private Integer sort;

    /** 关联课程使用数量 */
    @Excel(name = "使用次数")
    @ApiModelProperty("使用次数")
    private Integer useCount;

    /** 状态：0-禁用 1-启用 */
    @Excel(name = "状态", readConverterExp = "0=禁用,1=启用")
    @ApiModelProperty("状态：0-禁用 1-启用")
    private Integer status;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

    /** 删除标志：0-正常 1-删除 */
    @ApiModelProperty("删除标志：0-正常 1-删除")
    private Integer deleteFlag;

    /** 搜索值 */
    private String searchValue;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getSort()
    {
        return sort;
    }

    public void setSort(Integer sort)
    {
        this.sort = sort;
    }

    public Integer getUseCount()
    {
        return useCount;
    }

    public void setUseCount(Integer useCount)
    {
        this.useCount = useCount;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getSearchValue()
    {
        return searchValue;
    }

    public Integer getDeleteFlag()
    {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag)
    {
        this.deleteFlag = deleteFlag;
    }

    public void setSearchValue(String searchValue)
    {
        this.searchValue = searchValue;
    }

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getUpdateBy()
    {
        return updateBy;
    }

    public void setUpdateBy(String updateBy)
    {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("sort", getSort())
                .append("useCount", getUseCount())
                .append("status", getStatus())
                .append("remark", getRemark())
                .append("deleteFlag", getDeleteFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
