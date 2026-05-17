package com.backstage.system.mapper.resource;

import com.backstage.system.domain.resource.Link;
import com.backstage.system.domain.vo.resource.ResourceLinkVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 资源链接 数据层
 *
 * @author backstage
 */
@Mapper
public interface ResourceLinkMapper extends BaseMapper<Link> {

    /**
     * 按链接ID集合查询链接VO列表
     *
     * @param ids 链接ID集合
     * @return 链接VO集合
     */
    List<ResourceLinkVO> selectVOByIds(@Param("ids") Collection<Long> ids);

    /**
     * 按资源组ID查询关联的链接VO列表
     *
     * @param groupId 资源组ID
     * @return 链接VO集合
     */
    List<ResourceLinkVO> selectVOByGroupId(@Param("groupId") Long groupId);
}
