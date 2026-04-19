package com.backstage.system.domain.site;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 内部网站标签对象 osh_site_tags
 *
 * @author backstage
 */
@TableName("osh_site_tag")
public class OshSiteTag extends OSHBaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 记录 ID
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 标签名称
   */
  @NotBlank(message = "标签名称不能为空")
  @TableField(value = "tag_name", updateStrategy = FieldStrategy.NOT_EMPTY)
  private String tagName;


  @TableField(exist = false)
  private Long usageCount;

  @TableField(exist = false)
  private Long siteId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTagName() {
    return tagName;
  }

  public void setTagName(String tagName) {
    this.tagName = tagName;
  }

  public Long getUsageCount() {
    return usageCount;
  }

  public void setUsageCount(Long usageCount) {
    this.usageCount = usageCount;
  }

  public Long getSiteId() {
    return siteId;
  }

  public void setSiteId(Long siteId) {
    this.siteId = siteId;
  }
}
