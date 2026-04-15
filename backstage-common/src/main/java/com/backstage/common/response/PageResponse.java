package com.backstage.common.response;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Hope_Lau
 * @createTime: 2026年04月06日 23:02:57
 * @version:
 * @Description:
 */
public class PageResponse<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 当前页码 */
    private int pageNum;

    /** 每页显示条数 */
    private int pageSize;

    /** 总页数 */
    private int pages;

    /** 总页数 */
    private int totalPage;

    /** 列表数据 */
    private List<T> rows;

    public PageResponse()
    {
    }

    public PageResponse(List<T> rows, long total)
    {
        this.rows = rows;
        this.total = total;
    }

    public PageResponse(List<T> rows, long total, int pageNum, int pageSize)
    {
        this.rows = rows;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = calcTotalPage(total, pageSize);
        this.totalPage = this.pages;
    }

    /**
     * 构建分页结果
     */
    public static <T> PageResponse<T> of(List<T> rows, long total)
    {
        return new PageResponse<>(rows, total);
    }

    /**
     * 构建分页结果（带页码信息）
     */
    public static <T> PageResponse<T> of(List<T> rows, long total, int pageNum, int pageSize)
    {
        return new PageResponse<>(rows, total, pageNum, pageSize);
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
        syncTotalPage();
    }

    public int getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(int pageNum)
    {
        this.pageNum = pageNum;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
        syncTotalPage();
    }

    public int getPages()
    {
        return pages;
    }

    public void setPages(int pages)
    {
        this.pages = pages;
        this.totalPage = pages;
    }

    public int getTotalPage()
    {
        return totalPage;
    }

    public void setTotalPage(int totalPage)
    {
        this.totalPage = totalPage;
        this.pages = totalPage;
    }

    public List<T> getRows()
    {
        return rows;
    }

    public void setRows(List<T> rows)
    {
        this.rows = rows;
    }

    private void syncTotalPage()
    {
        int calcPages = calcTotalPage(total, pageSize);
        this.pages = calcPages;
        this.totalPage = calcPages;
    }

    private int calcTotalPage(long total, int pageSize)
    {
        if (pageSize <= 0)
        {
            return 0;
        }
        return (int) Math.ceil((double) total / pageSize);
    }
}
