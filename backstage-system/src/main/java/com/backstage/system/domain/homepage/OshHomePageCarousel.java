package com.backstage.system.domain.homepage;


import com.backstage.common.annotation.Excel;
import com.backstage.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 首页轮播图配置对象 osh_home_page_carousel
 */
public class OshHomePageCarousel extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;


    /** 排序（越小越靠前） */
    @Excel(name = "排序")
    private Integer sort;

    /** 图标 emoji */
    @Excel(name = "图标emoji")
    private String emoji;

    /** 图标背景渐变色 CSS 值 */
    @Excel(name = "图标背景色")
    private String iconBg;

    /** 卡片背景渐变色 CSS 值 */
    @Excel(name = "卡片背景色")
    private String cardBg;

    /** 卡片标题 */
    @Excel(name = "卡片标题")
    private String title;

    /** 卡片副标题 */
    @Excel(name = "卡片副标题")
    private String subtitle;

    /** 按钮文字 */
    @Excel(name = "按钮文字")
    private String btnText;

    /** 按钮跳转路径 */
    @Excel(name = "跳转路径")
    private String path;

    /** 右侧功能描述1 */
    @Excel(name = "功能描述1")
    private String feature1;

    /** 功能描述1图标 */
    private String feature1Icon;

    /** 右侧功能描述2 */
    @Excel(name = "功能描述2")
    private String feature2;

    /** 功能描述2图标 */
    private String feature2Icon;

    /** 右侧功能描述3 */
    @Excel(name = "功能描述3")
    private String feature3;

    /** 功能描述3图标 */
    private String feature3Icon;

    /** 是否显示：1-显示 0-隐藏 */
    @Excel(name = "是否显示", readConverterExp = "1=显示,0=隐藏")
    private Integer isVisible;

    /** 逻辑删除：0-正常 1-删除 */
    private Integer deleteFlag;

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getSort() { return sort; }

    public void setEmoji(String emoji) { this.emoji = emoji; }
    public String getEmoji() { return emoji; }

    public void setIconBg(String iconBg) { this.iconBg = iconBg; }
    public String getIconBg() { return iconBg; }

    public void setCardBg(String cardBg) { this.cardBg = cardBg; }
    public String getCardBg() { return cardBg; }

    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getSubtitle() { return subtitle; }

    public void setBtnText(String btnText) { this.btnText = btnText; }
    public String getBtnText() { return btnText; }

    public void setPath(String path) { this.path = path; }
    public String getPath() { return path; }

    public void setFeature1(String feature1) { this.feature1 = feature1; }
    public String getFeature1() { return feature1; }

    public void setFeature1Icon(String feature1Icon) { this.feature1Icon = feature1Icon; }
    public String getFeature1Icon() { return feature1Icon; }

    public void setFeature2(String feature2) { this.feature2 = feature2; }
    public String getFeature2() { return feature2; }

    public void setFeature2Icon(String feature2Icon) { this.feature2Icon = feature2Icon; }
    public String getFeature2Icon() { return feature2Icon; }

    public void setFeature3(String feature3) { this.feature3 = feature3; }
    public String getFeature3() { return feature3; }

    public void setFeature3Icon(String feature3Icon) { this.feature3Icon = feature3Icon; }
    public String getFeature3Icon() { return feature3Icon; }

    public void setIsVisible(Integer isVisible) { this.isVisible = isVisible; }
    public Integer getIsVisible() { return isVisible; }

    public void setDeleteFlag(Integer deleteFlag) { this.deleteFlag = deleteFlag; }
    public Integer getDeleteFlag() { return deleteFlag; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("sort", getSort())
                .append("emoji", getEmoji())
                .append("iconBg", getIconBg())
                .append("cardBg", getCardBg())
                .append("title", getTitle())
                .append("subtitle", getSubtitle())
                .append("btnText", getBtnText())
                .append("path", getPath())
                .append("feature1", getFeature1())
                .append("feature2", getFeature2())
                .append("feature3", getFeature3())
                .append("isVisible", getIsVisible())
                .append("deleteFlag", getDeleteFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }








}
