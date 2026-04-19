package com.backstage.system.domain.site;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 内部网站使用统计对象 osh_site_usage
 *
 * @author backstage
 */
@TableName("osh_site_usage")
public class OshSiteUsage extends OSHBaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 使用记录编号（主键）
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 网站 ID
   */
  private Long siteId;

  /**
   * 用户 ID
   */
  private Long userId;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getSiteId() {
    return siteId;
  }

  public void setSiteId(Long siteId) {
    this.siteId = siteId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}
