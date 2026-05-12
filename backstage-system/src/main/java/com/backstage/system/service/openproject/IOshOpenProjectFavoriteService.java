package com.backstage.system.service.openproject;

import java.util.Set;

public interface IOshOpenProjectFavoriteService {

    /** 收藏项目 */
    void favorite(Long userId, Long projectId);

    /** 取消收藏 */
    void cancelFavorite(Long userId, Long projectId);

    /** 查询用户收藏的项目 ID 集合 */
    Set<Long> getFavoriteProjectIds(Long userId);
}
