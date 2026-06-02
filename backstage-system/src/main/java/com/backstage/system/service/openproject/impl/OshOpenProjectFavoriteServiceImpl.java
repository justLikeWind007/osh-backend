package com.backstage.system.service.openproject.impl;

import com.backstage.system.domain.openproject.OshOpenProject;
import com.backstage.system.domain.openproject.OshUserFavoriteOpenProject;
import com.backstage.system.mapper.openproject.OshOpenProjectMapper;
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

    @Autowired
    private OshOpenProjectMapper projectMapper;

    @Override
    public void favorite(Long userId, Long projectId) {
        validateUserAndPublishedProject(userId, projectId);
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
        if (userId == null) throw new IllegalArgumentException("请先登录");
        if (projectId == null) throw new IllegalArgumentException("项目ID不能为空");
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

    private void validateUserAndPublishedProject(Long userId, Long projectId) {
        if (userId == null) throw new IllegalArgumentException("请先登录");
        if (projectId == null) throw new IllegalArgumentException("项目ID不能为空");
        OshOpenProject project = projectMapper.selectOne(
                new LambdaQueryWrapper<OshOpenProject>()
                        .eq(OshOpenProject::getId, projectId)
                        .eq(OshOpenProject::getStatus, 1)
                        .eq(OshOpenProject::getDeleteFlag, (byte) 0)
                        .last("limit 1")
        );
        if (project == null) throw new IllegalArgumentException("项目不存在或未上线");
    }
}
