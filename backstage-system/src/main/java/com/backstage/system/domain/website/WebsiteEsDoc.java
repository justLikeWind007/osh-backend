package com.backstage.system.domain.website;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 网站 ES 文档实体
 * 对应 ES 索引 osh_practical_website 中的一条文档
 * 把 MySQL 多表数据合并成一个完整对象，方便搜索
 */
public class WebsiteEsDoc {

    /** 网站 ID（与 MySQL 主键保持一致） */
    private Long id;

    /** 网站名称（text 类型，支持全文搜索分词） */
    private String name;

    /** 网站链接（keyword 类型，精确存储） */
    private String url;

    /** 网站描述（text 类型，支持全文搜索分词） */
    private String description;

    /** 网站 Logo 地址 */
    private String logoUrl;

    /** 标签列表（keyword 类型，精确匹配） */
    private List<String> tags;

    /** 点击次数 */
    private Integer clickCount;

    /** 好评数量 */
    private Integer goodCount;

    /** 中评数量 */
    private Integer midCount;

    /** 差评数量 */
    private Integer badCount;

    /** 收藏数量 */
    private Integer collectionCount;

    /** 综合评分 */
    private BigDecimal ratingScore;

    /** 审核时间 */
    private Date auditTime;

    // ===== Getter & Setter =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Integer getClickCount() { return clickCount; }
    public void setClickCount(Integer clickCount) { this.clickCount = clickCount; }

    public Integer getGoodCount() { return goodCount; }
    public void setGoodCount(Integer goodCount) { this.goodCount = goodCount; }

    public Integer getMidCount() { return midCount; }
    public void setMidCount(Integer midCount) { this.midCount = midCount; }

    public Integer getBadCount() { return badCount; }
    public void setBadCount(Integer badCount) { this.badCount = badCount; }

    public Integer getCollectionCount() { return collectionCount; }
    public void setCollectionCount(Integer collectionCount) { this.collectionCount = collectionCount; }

    public BigDecimal getRatingScore() { return ratingScore; }
    public void setRatingScore(BigDecimal ratingScore) { this.ratingScore = ratingScore; }

    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }
}
