package com.backstage.system.service.resource.impl;

import com.backstage.system.domain.resource.Link;
import com.backstage.system.domain.vo.resource.ResourceLinkVO;
import com.backstage.system.mapper.resource.ResourceLinkMapper;
import com.backstage.system.service.resource.IResourceLinkService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 资源链接 服务实现
 *
 * @author backstage
 */
@Service
public class ResourceLinkServiceImpl extends ServiceImpl<ResourceLinkMapper, Link> implements IResourceLinkService {

    @Override
    public Page<Link> pageLink(String keyword, Page<Link> page) {
        LambdaQueryWrapper<Link> wrapper = new LambdaQueryWrapper<Link>()
                .like(StringUtils.isNotBlank(keyword), Link::getName, keyword)
                .orderByDesc(Link::getId);
        return this.page(page, wrapper);
    }

    @Override
    public Link getLink(Long id) {
        return this.getById(id);
    }

    @Override
    public Long createLink(Link link) {
        this.save(link);
        return link.getId();
    }

    @Override
    public void updateLink(Link link) {
        this.updateById(link);
    }

    @Override
    public void deleteLink(Long id) {
        this.removeById(id);
    }

    @Override
    public List<ResourceLinkVO> listVOByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return baseMapper.selectVOByIds(ids);
    }
}
