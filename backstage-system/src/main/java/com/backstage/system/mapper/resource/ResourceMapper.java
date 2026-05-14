package com.backstage.system.mapper.resource;

import com.backstage.system.domain.resource.Resource;
import com.backstage.system.domain.vo.resource.ResourceVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 内部资源 数据层
 *
 * @author backstage
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

    /**
     * 按资源ID集合查询资源VO列表
     *
     * @param ids 资源ID集合
     * @return 资源VO集合
     */
    List<ResourceVO> selectVOByIds(@Param("ids") Collection<Long> ids);

    /**
     * 按资源组ID查询关联的资源VO列表
     *
     * @param groupId 资源组ID
     * @return 资源VO集合
     */
    List<ResourceVO> selectVOByGroupId(@Param("groupId") Long groupId);
}
