package com.backstage.system.domain.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 订单创建请求参数
 */
@ApiModel("订单创建实体类")
public class OrderCreateRequest {
    
    /** 商品 ID */
    @ApiModelProperty("商品的ID")
    @JsonProperty("goods_id")
    private Long goodsId;
    
    /** 类型：course 课程，column 专栏，book 电子书 */
    @ApiModelProperty("商品类型")
    private String type;
    
    /** 用户优惠券 ID */
    @ApiModelProperty("用户优惠券ID")
    @JsonProperty("user_coupon_id")
    private Long userCouponId;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserCouponId() {
        return userCouponId;
    }

    public void setUserCouponId(Long userCouponId) {
        this.userCouponId = userCouponId;
    }
}
