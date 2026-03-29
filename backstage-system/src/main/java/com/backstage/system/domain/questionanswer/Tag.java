package com.backstage.system.domain.questionanswer;

import java.io.Serializable;
import java.util.Date;
/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:20
 */
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签id
     */
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 使用次数
     */
    private Integer useCount;

    /**
     * 创建时间
     */
    private Date createdTime;

    // 无参构造方法
    public Tag() {
    }

    // 全参构造方法
    public Tag(Long id, String name, Integer useCount, Date createdTime) {
        this.id = id;
        this.name = name;
        this.useCount = useCount;
        this.createdTime = createdTime;
    }

    // getter和setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", useCount=" + useCount +
                ", createdTime=" + createdTime +
                '}';
    }
}
