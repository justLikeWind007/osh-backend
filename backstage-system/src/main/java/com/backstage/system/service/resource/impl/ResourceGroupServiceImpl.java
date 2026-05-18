package com.backstage.system.service.resource.impl;

import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.resource.ResourceGroup;
import com.backstage.system.domain.resource.ResourceGroupLink;
import com.backstage.system.domain.resource.ResourceGroupResource;
import com.backstage.system.domain.vo.resource.ResourceGroupListReqVO;
import com.backstage.system.domain.vo.resource.ResourceGroupSaveVO;
import com.backstage.system.domain.vo.resource.ResourceGroupVO;
import com.backstage.system.domain.vo.resource.ResourceLinkVO;
import com.backstage.system.domain.vo.resource.ResourceVO;
import com.backstage.system.mapper.resource.ResourceGroupLinkMapper;
import com.backstage.system.mapper.resource.ResourceGroupMapper;
import com.backstage.system.mapper.resource.ResourceGroupResourceMapper;
import com.backstage.system.mapper.resource.ResourceLinkMapper;
import com.backstage.system.mapper.resource.ResourceMapper;
import com.backstage.system.service.resource.IResourceGroupService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 资源组 服务实现
 *
 * @author backstage
 */
@Service
public class ResourceGroupServiceImpl extends ServiceImpl<ResourceGroupMapper, ResourceGroup> implements IResourceGroupService {

    @Resource
    private ResourceMapper resourceMapper;

    @Resource
    private ResourceLinkMapper resourceLinkMapper;

    @Resource
    private ResourceGroupResourceMapper resourceGroupResourceMapper;

    @Resource
    private ResourceGroupLinkMapper resourceGroupLinkMapper;

    @Override
    public List<ResourceGroupVO> listByCursor(ResourceGroupListReqVO reqVO) {
        List<ResourceGroup> groups = baseMapper.selectPageByCursor(reqVO);
        if (groups == null || groups.isEmpty()) {
            return Collections.emptyList();
        }
        List<ResourceGroupVO> result = new ArrayList<>(groups.size());
        for (ResourceGroup g : groups) {
            result.add(toVOWithRelations(g));
        }
        return result;
    }

    @Override
    public ResourceGroupVO getDetail(Long id) {
        ResourceGroup group = this.getById(id);
        if (group == null) {
            throw new ServiceException("资源组不存在");
        }
        return toVOWithRelations(group);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGroup(ResourceGroupSaveVO reqVO) {
        checkNameUnique(reqVO.getName(), null);
        ResourceGroup group = new ResourceGroup();
        group.setName(reqVO.getName());
        group.setDescription(reqVO.getDescription());
        group.setRemark(reqVO.getRemark());
        this.save(group);

        saveGroupResources(group.getId(), reqVO.getResourceIds());
        saveGroupLinks(group.getId(), reqVO.getLinkIds());
        return group.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGroup(ResourceGroupSaveVO reqVO) {
        if (reqVO.getId() == null) {
            throw new ServiceException("资源组ID不能为空");
        }
        ResourceGroup exist = this.getById(reqVO.getId());
        if (exist == null) {
            throw new ServiceException("资源组不存在");
        }
        checkNameUnique(reqVO.getName(), reqVO.getId());

        exist.setName(reqVO.getName());
        exist.setDescription(reqVO.getDescription());
        exist.setRemark(reqVO.getRemark());
        this.updateById(exist);

        // 重建关联
        resourceGroupResourceMapper.delete(new LambdaQueryWrapper<ResourceGroupResource>()
                .eq(ResourceGroupResource::getGroupId, reqVO.getId()));
        resourceGroupLinkMapper.delete(new LambdaQueryWrapper<ResourceGroupLink>()
                .eq(ResourceGroupLink::getGroupId, reqVO.getId()));

        saveGroupResources(reqVO.getId(), reqVO.getResourceIds());
        saveGroupLinks(reqVO.getId(), reqVO.getLinkIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGroup(Long id) {
        this.removeById(id);
        resourceGroupResourceMapper.delete(new LambdaQueryWrapper<ResourceGroupResource>()
                .eq(ResourceGroupResource::getGroupId, id));
        resourceGroupLinkMapper.delete(new LambdaQueryWrapper<ResourceGroupLink>()
                .eq(ResourceGroupLink::getGroupId, id));
    }

    // ------------------------------------------------------------------ private helpers

    private ResourceGroupVO toVOWithRelations(ResourceGroup group) {
        ResourceGroupVO vo = new ResourceGroupVO();
        BeanUtils.copyProperties(group, vo);
        List<ResourceVO> resources = resourceMapper.selectVOByGroupId(group.getId());
        List<ResourceLinkVO> links = resourceLinkMapper.selectVOByGroupId(group.getId());
        vo.setResources(resources == null ? Collections.emptyList() : resources);
        vo.setLinks(links == null ? Collections.emptyList() : links);
        return vo;
    }

    private void checkNameUnique(String name, Long excludeId) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        LambdaQueryWrapper<ResourceGroup> wrapper = new LambdaQueryWrapper<ResourceGroup>()
                .eq(ResourceGroup::getName, name);
        if (excludeId != null) {
            wrapper.ne(ResourceGroup::getId, excludeId);
        }
        long count = this.count(wrapper);
        if (count > 0) {
            throw new ServiceException("资源组名称已存在");
        }
    }

    private void saveGroupResources(Long groupId, List<Long> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return;
        }
        for (Long rid : resourceIds) {
            if (rid == null) {
                continue;
            }
            ResourceGroupResource rel = new ResourceGroupResource();
            rel.setGroupId(groupId);
            rel.setResourceId(rid);
            resourceGroupResourceMapper.insert(rel);
        }
    }

    private void saveGroupLinks(Long groupId, List<Long> linkIds) {
        if (linkIds == null || linkIds.isEmpty()) {
            return;
        }
        for (Long lid : linkIds) {
            if (lid == null) {
                continue;
            }
            ResourceGroupLink rel = new ResourceGroupLink();
            rel.setGroupId(groupId);
            rel.setLinkId(lid);
            resourceGroupLinkMapper.insert(rel);
        }
    }
}
