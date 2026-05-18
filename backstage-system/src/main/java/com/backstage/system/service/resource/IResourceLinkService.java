package com.backstage.system.service.resource;

import com.backstage.system.domain.resource.Link;
import com.backstage.system.domain.vo.resource.ResourceLinkVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 资源链接 服务接口
 *
 * @author backstage
 */
public interface IResourceLinkService extends IService<Link> {

    /**
     * 分页查询链接列表
     *
     * @param keyword 关键词
     * @param page    分页参数
     * @return 链接分页
     */
    Page<Link> pageLink(String keyword, Page<Link> page);

    /**
     * 查询链接详情
     *
     * @param id 链接ID
     * @return 链接
     */
    Link getLink(Long id);

    /**
     * 新增链接
     *
     * @param link 链接
     * @return 链接ID
     */
    Long createLink(Link link);

    /**
     * 修改链接
     *
     * @param link 链接
     */
    void updateLink(Link link);

    /**
     * 删除链接（逻辑删除）
     *
     * @param id 链接ID
     */
    void deleteLink(Long id);

    /**
     * 按链接ID集合查询链接VO列表
     *
     * @param ids 链接ID集合
     * @return 链接VO集合
     */
    List<ResourceLinkVO> listVOByIds(List<Long> ids);
}
