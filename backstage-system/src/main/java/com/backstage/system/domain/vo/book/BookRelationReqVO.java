package com.backstage.system.domain.vo.book;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 电子书关联操作基础请求 VO
 */
public class BookRelationReqVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 电子书ID */
    @NotNull(message = "电子书ID不能为空")
    private Long bookId;

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
}
