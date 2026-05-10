package com.backstage.system.service.openproject.impl;

import com.backstage.system.domain.openproject.OshOpenProject;
import com.backstage.system.domain.openproject.OshOpenProjectTag;
import com.backstage.system.domain.openproject.OshOpenProjectTagRel;
import com.backstage.system.domain.openproject.dto.OpenProjectAuditDTO;
import com.backstage.system.domain.openproject.dto.OpenProjectQueryDTO;
import com.backstage.system.domain.openproject.dto.OpenProjectSubmitDTO;
import com.backstage.system.domain.openproject.vo.OpenProjectVO;
import com.backstage.system.mapper.openproject.OshOpenProjectMapper;
import com.backstage.system.mapper.openproject.OshOpenProjectTagMapper;
import com.backstage.system.mapper.openproject.OshOpenProjectTagRelMapper;
import com.backstage.system.service.openproject.IOshOpenProjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OshOpenProjectServiceImpl implements IOshOpenProjectService {

    @Autowired
    private OshOpenProjectMapper projectMapper;

    @Autowired
    private OshOpenProjectTagMapper tagMapper;

    @Autowired
    private OshOpenProjectTagRelMapper tagRelMapper;

    @Override
    public Map<String, Object> listPage(OpenProjectQueryDTO queryDTO) {
        int pageNum = queryDTO.getPageNum() == null ? 1 : queryDTO.getPageNum();
        int pageSize = queryDTO.getPageSize() == null ? 10 : queryDTO.getPageSize();

        // 如果按标签筛选，先查出符合标签的 projectId
        Set<Long> tagFilterIds = null;
        if (!CollectionUtils.isEmpty(queryDTO.getTagIds())) {
            List<OshOpenProjectTagRel> rels = tagRelMapper.selectList(
                    new LambdaQueryWrapper<OshOpenProjectTagRel>()
                            .in(OshOpenProjectTagRel::getTagId, queryDTO.getTagIds())
            );
            tagFilterIds = rels.stream().map(OshOpenProjectTagRel::getProjectId).collect(Collectors.toSet());
            if (tagFilterIds.isEmpty()) {
                // 没有匹配的项目，直接返回空
                Map<String, Object> empty = new LinkedHashMap<>();
                empty.put("rows", Collections.emptyList());
                empty.put("total", 0L);
                empty.put("pageNum", pageNum);
                empty.put("pageSize", pageSize);
                return empty;
            }
        }

        // 构建查询条件
        LambdaQueryWrapper<OshOpenProject> wrapper = new LambdaQueryWrapper<OshOpenProject>()
                .eq(OshOpenProject::getStatus, 1)
                .eq(OshOpenProject::getDeleteFlag, (byte) 0);

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            String kw = "%" + queryDTO.getKeyword() + "%";
            wrapper.and(w -> w.like(OshOpenProject::getProjectName, kw)
                    .or().like(OshOpenProject::getProjectDesc, kw));
        }

        if (tagFilterIds != null) {
            wrapper.in(OshOpenProject::getId, tagFilterIds);
        }

        // 排序：白名单校验，防止 SQL 注入
        String sortField = queryDTO.getSortField();
        boolean asc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
        switch (sortField == null ? "" : sortField) {
            case "star_count":
                if (asc) wrapper.orderByAsc(OshOpenProject::getStarCount);
                else     wrapper.orderByDesc(OshOpenProject::getStarCount);
                break;
            case "fork_count":
                if (asc) wrapper.orderByAsc(OshOpenProject::getForkCount);
                else     wrapper.orderByDesc(OshOpenProject::getForkCount);
                break;
            case "last_commit_time":
                if (asc) wrapper.orderByAsc(OshOpenProject::getLastCommitTime);
                else     wrapper.orderByDesc(OshOpenProject::getLastCommitTime);
                break;
            default:
                wrapper.orderByDesc(OshOpenProject::getCreateTime);
        }

        PageHelper.startPage(pageNum, pageSize);
        List<OshOpenProject> projects = projectMapper.selectList(wrapper);
        PageInfo<OshOpenProject> pageInfo = new PageInfo<>(projects);

        // 批量查询标签关联
        List<Long> projectIds = projects.stream().map(OshOpenProject::getId).collect(Collectors.toList());
        Map<Long, List<Long>> projectTagMap = new HashMap<>();
        Map<Long, List<String>> projectTagNameMap = new HashMap<>();

        if (!projectIds.isEmpty()) {
            List<OshOpenProjectTagRel> rels = tagRelMapper.selectList(
                    new LambdaQueryWrapper<OshOpenProjectTagRel>()
                            .in(OshOpenProjectTagRel::getProjectId, projectIds)
            );
            // 查所有标签
            List<OshOpenProjectTag> allTags = tagMapper.selectList(
                    new LambdaQueryWrapper<OshOpenProjectTag>().eq(OshOpenProjectTag::getDeleteFlag, (byte) 0)
            );
            Map<Long, String> tagIdNameMap = allTags.stream()
                    .collect(Collectors.toMap(OshOpenProjectTag::getId, OshOpenProjectTag::getTagName));

            for (OshOpenProjectTagRel rel : rels) {
                projectTagMap.computeIfAbsent(rel.getProjectId(), k -> new ArrayList<>()).add(rel.getTagId());
                String tagName = tagIdNameMap.get(rel.getTagId());
                if (tagName != null) {
                    projectTagNameMap.computeIfAbsent(rel.getProjectId(), k -> new ArrayList<>()).add(tagName);
                }
            }
        }

        // 转 VO
        List<OpenProjectVO> voList = projects.stream().map(p -> {
            OpenProjectVO vo = new OpenProjectVO();
            vo.setId(p.getId());
            vo.setProjectName(p.getProjectName());
            vo.setProjectDesc(p.getProjectDesc());
            vo.setProjectUrl(p.getProjectUrl());
            vo.setAuthorName(p.getAuthorName());
            vo.setProjectCover(p.getProjectCover());
            vo.setStatus(p.getStatus());
            vo.setClickCount(p.getClickCount());
            vo.setCreateTime(p.getCreateTime());
            vo.setTagIds(projectTagMap.getOrDefault(p.getId(), Collections.emptyList()));
            vo.setTagNames(projectTagNameMap.getOrDefault(p.getId(), Collections.emptyList()));
            vo.setStarCount(p.getStarCount());
            vo.setForkCount(p.getForkCount());
            vo.setLastCommitTime(p.getLastCommitTime());
            vo.setIsArchived(p.getIsArchived());
            vo.setLastSyncTime(p.getLastSyncTime());
            vo.setCourseUrl(p.getCourseUrl());
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", voList);
        result.put("total", pageInfo.getTotal());
        result.put("pageNum", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(OpenProjectSubmitDTO dto) {
        if (!StringUtils.hasText(dto.getProjectName())) throw new IllegalArgumentException("项目名称不能为空");
        if (!StringUtils.hasText(dto.getProjectUrl())) throw new IllegalArgumentException("项目链接不能为空");
        if (!dto.getProjectUrl().matches("^https?://.*")) throw new IllegalArgumentException("请输入有效的URL地址");

        OshOpenProject project = new OshOpenProject();
        project.setProjectName(dto.getProjectName());
        project.setProjectDesc(dto.getProjectDesc());
        project.setProjectUrl(dto.getProjectUrl());
        project.setAuthorName(dto.getAuthorName());
        project.setProjectCover(dto.getProjectCover());
        project.setCourseUrl(dto.getCourseUrl());
        project.setStatus(0);
        project.setClickCount(0);
        project.setStarCount(0);
        project.setForkCount(0);
        project.setIsArchived((byte) 0);
        project.setDeleted(false);
        projectMapper.insert(project);

        // 保存已有标签关联
        if (!CollectionUtils.isEmpty(dto.getTagIds())) {
            for (Long tagId : dto.getTagIds()) {
                OshOpenProjectTagRel rel = new OshOpenProjectTagRel();
                rel.setProjectId(project.getId());
                rel.setTagId(tagId);
                tagRelMapper.insert(rel);
            }
        }

        // 处理自定义标签：不存在则创建，然后建立关联
        if (!CollectionUtils.isEmpty(dto.getCustomTags())) {
            for (String tagName : dto.getCustomTags()) {
                if (!StringUtils.hasText(tagName)) continue;
                String trimmed = tagName.trim();

                // 查询标签是否已存在（忽略大小写）
                OshOpenProjectTag existTag = tagMapper.selectOne(
                        new LambdaQueryWrapper<OshOpenProjectTag>()
                                .eq(OshOpenProjectTag::getTagName, trimmed)
                                .eq(OshOpenProjectTag::getDeleteFlag, (byte) 0)
                                .last("limit 1")
                );

                Long tagId;
                if (existTag != null) {
                    // 已存在，直接复用
                    tagId = existTag.getId();
                } else {
                    // 不存在，创建新标签
                    OshOpenProjectTag newTag = new OshOpenProjectTag();
                    newTag.setTagName(trimmed);
                    newTag.setTagCode(trimmed.toLowerCase().replaceAll("\\s+", "_"));
                    newTag.setSortOrder(999);
                    newTag.setDeleted(false);
                    tagMapper.insert(newTag);
                    tagId = newTag.getId();
                }

                OshOpenProjectTagRel rel = new OshOpenProjectTagRel();
                rel.setProjectId(project.getId());
                rel.setTagId(tagId);
                tagRelMapper.insert(rel);
            }
        }
    }

    @Override
    public Map<String, Object> listPending(OpenProjectQueryDTO queryDTO) {
        int pageNum = queryDTO.getPageNum() == null ? 1 : queryDTO.getPageNum();
        int pageSize = queryDTO.getPageSize() == null ? 10 : queryDTO.getPageSize();

        LambdaQueryWrapper<OshOpenProject> wrapper = new LambdaQueryWrapper<OshOpenProject>()
                .eq(OshOpenProject::getStatus, 0)
                .eq(OshOpenProject::getDeleteFlag, (byte) 0)
                .orderByAsc(OshOpenProject::getCreateTime);

        if (StringUtils.hasText(queryDTO.getKeyword())) {
            String kw = "%" + queryDTO.getKeyword() + "%";
            wrapper.and(w -> w.like(OshOpenProject::getProjectName, kw)
                    .or().like(OshOpenProject::getProjectDesc, kw));
        }

        PageHelper.startPage(pageNum, pageSize);
        List<OshOpenProject> projects = projectMapper.selectList(wrapper);
        PageInfo<OshOpenProject> pageInfo = new PageInfo<>(projects);

        // 批量查询标签关联
        List<Long> projectIds = projects.stream().map(OshOpenProject::getId).collect(Collectors.toList());
        Map<Long, List<String>> projectTagNameMap = new HashMap<>();
        if (!projectIds.isEmpty()) {
            List<OshOpenProjectTagRel> rels = tagRelMapper.selectList(
                    new LambdaQueryWrapper<OshOpenProjectTagRel>()
                            .in(OshOpenProjectTagRel::getProjectId, projectIds)
            );
            List<OshOpenProjectTag> allTags = tagMapper.selectList(
                    new LambdaQueryWrapper<OshOpenProjectTag>().eq(OshOpenProjectTag::getDeleteFlag, (byte) 0)
            );
            Map<Long, String> tagIdNameMap = allTags.stream()
                    .collect(Collectors.toMap(OshOpenProjectTag::getId, OshOpenProjectTag::getTagName));
            for (OshOpenProjectTagRel rel : rels) {
                String tagName = tagIdNameMap.get(rel.getTagId());
                if (tagName != null) {
                    projectTagNameMap.computeIfAbsent(rel.getProjectId(), k -> new ArrayList<>()).add(tagName);
                }
            }
        }

        List<OpenProjectVO> voList = projects.stream().map(p -> {
            OpenProjectVO vo = new OpenProjectVO();
            vo.setId(p.getId());
            vo.setProjectName(p.getProjectName());
            vo.setProjectDesc(p.getProjectDesc());
            vo.setProjectUrl(p.getProjectUrl());
            vo.setAuthorName(p.getAuthorName());
            vo.setProjectCover(p.getProjectCover());
            vo.setStatus(p.getStatus());
            vo.setCreateTime(p.getCreateTime());
            vo.setCourseUrl(p.getCourseUrl());
            vo.setTagNames(projectTagNameMap.getOrDefault(p.getId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("rows", voList);
        result.put("total", pageInfo.getTotal());
        result.put("pageNum", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());
        return result;
    }

    @Override
    public void audit(OpenProjectAuditDTO dto) {
        if (dto.getId() == null) throw new IllegalArgumentException("项目ID不能为空");
        if (dto.getStatus() == null || (dto.getStatus() != 1 && dto.getStatus() != 2)) {
            throw new IllegalArgumentException("审核状态不合法");
        }
        if (dto.getStatus() == 2 && !StringUtils.hasText(dto.getRejectReason())) {
            throw new IllegalArgumentException("拒绝时必须填写原因");
        }

        OshOpenProject project = projectMapper.selectById(dto.getId());
        if (project == null) throw new IllegalArgumentException("项目不存在");

        project.setStatus(dto.getStatus());
        project.setRejectReason(dto.getRejectReason());
        projectMapper.updateById(project);
    }

    @Override
    public void incrementClickCount(Long id) {
        projectMapper.update(null,
                new LambdaUpdateWrapper<OshOpenProject>()
                        .eq(OshOpenProject::getId, id)
                        .setSql("click_count = click_count + 1")
        );
    }

    @Override
    public OpenProjectVO getDetail(Long id) {
        OshOpenProject p = projectMapper.selectById(id);
        if (p == null || p.getDeleteFlag() == 1) return null;

        // 查标签
        List<OshOpenProjectTagRel> rels = tagRelMapper.selectList(
                new LambdaQueryWrapper<OshOpenProjectTagRel>()
                        .eq(OshOpenProjectTagRel::getProjectId, id)
        );
        List<Long> tagIds = rels.stream().map(OshOpenProjectTagRel::getTagId).collect(Collectors.toList());
        List<String> tagNames = Collections.emptyList();
        if (!tagIds.isEmpty()) {
            List<OshOpenProjectTag> tags = tagMapper.selectList(
                    new LambdaQueryWrapper<OshOpenProjectTag>()
                            .in(OshOpenProjectTag::getId, tagIds)
                            .eq(OshOpenProjectTag::getDeleteFlag, (byte) 0)
            );
            tagNames = tags.stream().map(OshOpenProjectTag::getTagName).collect(Collectors.toList());
        }

        OpenProjectVO vo = new OpenProjectVO();
        vo.setId(p.getId());
        vo.setProjectName(p.getProjectName());
        vo.setProjectDesc(p.getProjectDesc());
        vo.setProjectUrl(p.getProjectUrl());
        vo.setAuthorName(p.getAuthorName());
        vo.setProjectCover(p.getProjectCover());
        vo.setStatus(p.getStatus());
        vo.setClickCount(p.getClickCount());
        vo.setCreateTime(p.getCreateTime());
        vo.setTagIds(tagIds);
        vo.setTagNames(tagNames);
        vo.setStarCount(p.getStarCount());
        vo.setForkCount(p.getForkCount());
        vo.setLastCommitTime(p.getLastCommitTime());
        vo.setIsArchived(p.getIsArchived());
        vo.setLastSyncTime(p.getLastSyncTime());
        vo.setCourseUrl(p.getCourseUrl());
        return vo;
    }

    @Override
    public List<OshOpenProjectTag> listTags() {
        return tagMapper.selectList(
                new LambdaQueryWrapper<OshOpenProjectTag>()
                        .eq(OshOpenProjectTag::getDeleteFlag, (byte) 0)
                        .orderByAsc(OshOpenProjectTag::getSortOrder)
        );
    }
}
