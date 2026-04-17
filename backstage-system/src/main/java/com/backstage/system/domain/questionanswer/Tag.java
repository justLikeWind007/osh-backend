package com.backstage.system.domain.questionanswer;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:20
 */
@TableName("osh_question_answer_tag")
public class Tag extends OSHBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签类型
     */
    private String type;

    /**
     * 使用次数
     */
    private Integer useCount;

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

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", useCount=" + useCount +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
