package com.backstage.system.domain.seckill;

import com.backstage.common.annotation.Excel;
import com.backstage.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品池对象 osh_seckill_goods
 *
 * @author backstage
 * @date 2026-04-28
 */
@ApiModel(description = "秒杀商品池")
public class OshSeckillGoods extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @ApiModelProperty("主键ID")
    private Long id;

    /** 关联商品ID */
    @Excel(name = "商品ID")
    @ApiModelProperty("关联商品ID（课程/书籍等）")
    private Long goodsId;

    /** 商品类型：1-课程 2-书籍 3-实物商品 */
    @Excel(name = "商品类型")
    @ApiModelProperty("商品类型：1-课程 2-书籍 3-实物商品")
    private Integer goodsType;

    /** 商品名称快照 */
    @Excel(name = "商品名称")
    @ApiModelProperty("商品名称快照")
    private String goodsName;

    /** 商品封面快照 */
    @Excel(name = "商品封面")
    @ApiModelProperty("商品封面快照")
    private String goodsCover;

    /** 商品原价快照 */
    @Excel(name = "原价")
    @ApiModelProperty("商品原价快照")
    private BigDecimal originPrice;

    /** 允许的最低秒杀价 */
    @Excel(name = "最低秒杀价")
    @ApiModelProperty("允许的最低秒杀价，防止运营定价过低")
    private BigDecimal minSeckillPrice;

    /** 状态：0-待审核 1-已上架 2-已下架 */
    @Excel(name = "状态")
    @ApiModelProperty("状态：0-待审核 1-已上架 2-已下架")
    private Integer status;

    /** 排序权重，数值越大越靠前 */
    @Excel(name = "排序权重")
    @ApiModelProperty("排序权重，数值越大越靠前")
    private Integer sort;

    /** 创建人 */
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新人 */
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 删除标记：0-正常 1-已删除 */
    @ApiModelProperty("删除标记：0-正常 1-已删除")
    private Integer deleteFlag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGoodsId() { return goodsId; }
    public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }

    public Integer getGoodsType() { return goodsType; }
    public void setGoodsType(Integer goodsType) { this.goodsType = goodsType; }

    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }

    public String getGoodsCover() { return goodsCover; }
    public void setGoodsCover(String goodsCover) { this.goodsCover = goodsCover; }

    public BigDecimal getOriginPrice() { return originPrice; }
    public void setOriginPrice(BigDecimal originPrice) { this.originPrice = originPrice; }

    public BigDecimal getMinSeckillPrice() { return minSeckillPrice; }
    public void setMinSeckillPrice(BigDecimal minSeckillPrice) { this.minSeckillPrice = minSeckillPrice; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getDeleteFlag() { return deleteFlag; }
    public void setDeleteFlag(Integer deleteFlag) { this.deleteFlag = deleteFlag; }

    @Override
    public String toString() {
        return "OshSeckillGoods{" +
                "id=" + id +
                ", goodsId=" + goodsId +
                ", goodsType=" + goodsType +
                ", goodsName='" + goodsName + '\'' +
                ", goodsCover='" + goodsCover + '\'' +
                ", originPrice=" + originPrice +
                ", minSeckillPrice=" + minSeckillPrice +
                ", status=" + status +
                ", sort=" + sort +
                ", createBy='" + createBy + '\'' +
                ", createTime=" + createTime +
                ", updateBy='" + updateBy + '\'' +
                ", updateTime=" + updateTime +
                ", deleteFlag=" + deleteFlag +
                '}';
    }
}
