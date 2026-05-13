package com.backstage.system.mapper.resource;

import com.backstage.system.domain.resource.ResourceGroupResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资源组-资源 关联数据层
 *
 * @author backstage
 */
@Mapper
public interface ResourceGroupResourceMapper extends BaseMapper<ResourceGroupResource> {
}
