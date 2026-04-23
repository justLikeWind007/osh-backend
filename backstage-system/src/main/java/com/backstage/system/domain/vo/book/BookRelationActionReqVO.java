package com.backstage.system.domain.vo.book;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 收藏/关注电子书请求 VO
 */
public class BookRelationActionReqVO extends BookRelationReqVO {

    private static final long serialVersionUID = 1L;

    /** 状态（0-取消，1-操作） */
    @NotNull(message = "status 参数无效，请传 0 或 1")
    @Min(value = 0, message = "status 参数无效，请传 0 或 1")
    @Max(value = 1, message = "status 参数无效，请传 0 或 1")
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
