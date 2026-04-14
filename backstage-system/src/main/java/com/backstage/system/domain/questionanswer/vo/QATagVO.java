package com.backstage.system.domain.questionanswer.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/31
 * Time: 20:23
 */
public class QATagVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;

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

    @Override
    public String toString() {
        return "QATagVO{" +
                "id=" + id +
                "name='" + name + '\'' +
                '}';
    }
}
