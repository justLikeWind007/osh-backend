package com.backstage.system.mapper.resource;

import com.backstage.system.domain.resource.ResourceGroup;
import com.backstage.system.domain.vo.resource.ResourceGroupListReqVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源组 数据层
 *
 * @author backstage
 */
@Mapper
public interface ResourceGroupMapper extends BaseMapper<ResourceGroup> {

    /**
     * 游标分页查询资源组列表
     *
     * @param reqVO 查询条件
     * @return 资源组集合
     */
    List<ResourceGroup> selectPageByCursor(@Param("reqVO") ResourceGroupListReqVO reqVO);
}
