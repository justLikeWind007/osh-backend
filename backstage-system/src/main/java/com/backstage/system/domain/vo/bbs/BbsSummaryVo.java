package com.backstage.system.domain.vo.bbs;

import com.backstage.system.domain.bbs.OshBbsCategory;
import java.util.List;

/**
 * 社区概览返回对象
 */
public class BbsSummaryVo {
    private Long count;                // 分类总数
    private List<OshBbsCategory> rows; // 分类列表数据
    private Long userCount;            // 全站用户数
    private Long postCount;            // 全站帖子数

    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }

    public List<OshBbsCategory> getRows() { return rows; }
    public void setRows(List<OshBbsCategory> rows) { this.rows = rows; }

    public Long getUserCount() { return userCount; }
    public void setUserCount(Long userCount) { this.userCount = userCount; }

    public Long getPostCount() { return postCount; }
    public void setPostCount(Long postCount) { this.postCount = postCount; }
}