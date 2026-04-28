package com.backstage.system.domain.vo.website;

import java.util.List;

/**
 * @author xuanqing
 * @create 2026-04-21 11:58
 */
public class EsPageResult<T> {
    private final List<T> list;
    private final long total;

    public EsPageResult(List<T> list, long total) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() { return list; }
    public long getTotal() { return total; }
}
