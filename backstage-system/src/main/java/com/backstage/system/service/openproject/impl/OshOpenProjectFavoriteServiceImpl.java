package com.backstage.system.service.openproject.impl;

import com.backstage.system.domain.openproject.OshUserFavoriteOpenProject;
import com.backstage.system.mapper.openproject.OshUserFavoriteOpenProjectMapper;
import com.backstage.system.service.openproject.IOshOpenProjectFavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OshOpenProjectFavoriteServiceImpl implements IOshOpenProjectFavoriteService {

    @Autowired
    private OshUserFavoriteOpenProjectMapper favoriteMapper;

    @Override
    public void favorite(Long userId, Long projectId) {
        // 查询是否存在记录（包含已软删除的）
        OshUserFavoriteOpenProject existing = favoriteMapper.selectOne(
                new LambdaQueryWrapper<OshUserFavoriteOpenProject>()
                        .eq(OshUserFavoriteOpenProject::getUserId, userId)
                        .eq(OshUserFavoriteOpenProject::getProjectId, projectId)
                        .last("limit 1")
        );

        if (existing == null) {
            // 从未收藏过，新增
            OshUserFavoriteOpenProject record = new OshUserFavoriteOpenProject();
            record.setUserId(userId);
            record.setProjectId(projectId);
            record.setDeleted(false);
            favoriteMapper.insert(record);
        } else if (existing.getDeleteFlag() != null && existing.getDeleteFlag() == 1) {
            // 曾经收藏后取消，恢复
            favoriteMapper.update(null,
                    new LambdaUpdateWrapper<OshUserFavoriteOpenProject>()
                            .eq(OshUserFavoriteOpenProject::getId, existing.getId())
                            .set(OshUserFavoriteOpenProject::getDeleteFlag, (byte) 0)
            );
        }
        // deleteFlag=0 说明已收藏，忽略
    }

    @Override
    public void cancelFavorite(Long userId, Long projectId) {
        favoriteMapper.update(null,
                new LambdaUpdateWrapper<OshUserFavoriteOpenProject>()
                        .eq(OshUserFavoriteOpenProject::getUserId, userId)
                        .eq(OshUserFavoriteOpenProject::getProjectId, projectId)
                        .set(OshUserFavoriteOpenProject::getDeleteFlag, (byte) 1)
        );
    }

    @Override
    public Set<Long> getFavoriteProjectIds(Long userId) {
        if (userId == null) return Collections.emptySet();
        List<OshUserFavoriteOpenProject> list = favoriteMapper.selectList(
                new LambdaQueryWrapper<OshUserFavoriteOpenProject>()
                        .eq(OshUserFavoriteOpenProject::getUserId, userId)
                        .eq(OshUserFavoriteOpenProject::getDeleteFlag, (byte) 0)
                        .select(OshUserFavoriteOpenProject::getProjectId)
        );
        return list.stream().map(OshUserFavoriteOpenProject::getProjectId).collect(Collectors.toSet());
    }
}
