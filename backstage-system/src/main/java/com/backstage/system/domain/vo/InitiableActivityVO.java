package com.backstage.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 可发起拼团活动VO
 * 
 * @author system
 * @date 2026-04-30
 */
@ApiModel("可发起拼团活动信息")
public class InitiableActivityVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 拼团活动ID */
    @ApiModelProperty("拼团活动ID")
    private Long id;
    
    /** 拼团活动标题 */
    @ApiModelProperty("拼团活动标题")
    private String title;
    
    /** 服务器CPU配置 */
    @ApiModelProperty("CPU配置")
    private String cpu;
    
    /** 服务器内存配置 */
    @ApiModelProperty("内存配置")
    private String memory;
    
    /** 服务器存储配置 */
    @ApiModelProperty("存储配置")
    private String storage;
    
    /** 基础拼团价格 */
    @ApiModelProperty("基础拼团价格")
    private BigDecimal basePrice;
    
    /** 默认最低人数 */
    @ApiModelProperty("默认最低人数")
    private Integer defaultMinNum;
    
    /** 默认最多人数 */
    @ApiModelProperty("默认最多人数")
    private Integer defaultMaxNum;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getCpu() {
        return cpu;
    }
    
    public void setCpu(String cpu) {
        this.cpu = cpu;
    }
    
    public String getMemory() {
        return memory;
    }
    
    public void setMemory(String memory) {
        this.memory = memory;
    }
    
    public String getStorage() {
        return storage;
    }
    
    public void setStorage(String storage) {
        this.storage = storage;
    }
    
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
    
    public Integer getDefaultMinNum() {
        return defaultMinNum;
    }
    
    public void setDefaultMinNum(Integer defaultMinNum) {
        this.defaultMinNum = defaultMinNum;
    }
    
    public Integer getDefaultMaxNum() {
        return defaultMaxNum;
    }
    
    public void setDefaultMaxNum(Integer defaultMaxNum) {
        this.defaultMaxNum = defaultMaxNum;
    }
}
