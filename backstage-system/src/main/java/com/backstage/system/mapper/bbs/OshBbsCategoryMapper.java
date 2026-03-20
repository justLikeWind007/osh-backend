package com.backstage.system.mapper.bbs;

import com.backstage.system.domain.bbs.OshBbsCategory;
import java.util.List;

public interface OshBbsCategoryMapper {
    /** 查询社区分类列表 */
    public List<OshBbsCategory> selectCategoryList(OshBbsCategory category);
    
    /** 统计用户总数 */
    public Long countTotalUsers();
    
    /** 统计帖子总数 */
    public Long countTotalPosts();
}