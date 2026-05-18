package com.backstage.system.service.resource;

import com.backstage.system.domain.resource.ResourceGroup;
import com.backstage.system.domain.vo.resource.ResourceGroupListReqVO;
import com.backstage.system.domain.vo.resource.ResourceGroupSaveVO;
import com.backstage.system.domain.vo.resource.ResourceGroupVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 资源组 服务接口
 *
 * @author backstage
 */
public interface IResourceGroupService extends IService<ResourceGroup> {

    /**
     * 游标分页查询资源组列表（每个资源组包含关联的资源/链接）
     *
     * @param reqVO 查询条件
     * @return 资源组VO集合
     */
    List<ResourceGroupVO> listByCursor(ResourceGroupListReqVO reqVO);

    /**
     * 查询资源组详情
     *
     * @param id 资源组ID
     * @return 资源组VO
     */
    ResourceGroupVO getDetail(Long id);

    /**
     * 新增资源组，同时维护关联的资源和链接
     *
     * @param reqVO 请求VO
     * @return 资源组ID
     */
    Long createGroup(ResourceGroupSaveVO reqVO);

    /**
     * 修改资源组，同时重建关联的资源和链接
     *
     * @param reqVO 请求VO
     */
    void updateGroup(ResourceGroupSaveVO reqVO);

    /**
     * 删除资源组（逻辑删除），并移除关联记录
     *
     * @param id 资源组ID
     */
    void deleteGroup(Long id);
}
