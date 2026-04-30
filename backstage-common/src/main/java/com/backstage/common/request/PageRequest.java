package com.backstage.common.request;

/**
 * 分页请求
 *
 * @author Hope_Lau
 */
public class PageRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private int pageNum = 1;

    /**
     * 每页结果数
     */
    private int pageSize = 10;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
