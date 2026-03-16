package com.backstage.system.domain.fava;

import com.backstage.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 收藏对象 osh_fava
 */
public class OshFava extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private Long goodsId;
    private String type;

    public OshFava() {}

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }

    public void setUserId(Long userId) { this.userId = userId; }
    public Long getUserId() { return userId; }

    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
    public Long getGoodsId() { return goodsId; }

    public void setType(String type) { this.type = type; }
    public String getType() { return type; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("goodsId", getGoodsId())
            .append("type", getType())
            .append("createTime", getCreateTime())
            .toString();
    }
}