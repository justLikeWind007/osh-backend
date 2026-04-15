package com.backstage.system.domain.questionanswer.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/31
 * Time: 20:03
 */
public class ResourceVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long resourceNo;
    private String resourceName;

    public Long getResourceNo() {
        return resourceNo;
    }

    public void setResourceNo(Long resourceNo) {
        this.resourceNo = resourceNo;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String toString() {
        return "ResourceVO{" +
                "resourceNo=" + resourceNo +
                ", resourceName='" + resourceName + '\'' +
                '}';
    }
}
