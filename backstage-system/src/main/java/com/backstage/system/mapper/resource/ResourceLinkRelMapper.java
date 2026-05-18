package com.backstage.system.mapper.resource;

import com.backstage.system.domain.resource.ResourceLink;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资源-链接 关联数据层
 *
 * @author backstage
 */
@Mapper
public interface ResourceLinkRelMapper extends BaseMapper<ResourceLink> {
}
