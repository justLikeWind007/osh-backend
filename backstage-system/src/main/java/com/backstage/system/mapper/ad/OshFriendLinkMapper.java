package com.backstage.system.mapper.ad;

import com.backstage.system.domain.ad.OshFriendLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 友情链接 Mapper
 */
@Mapper
public interface OshFriendLinkMapper {

    /**
     * 查询所有友情链接（按排序序号升序）
     */
    List<OshFriendLink> selectAll();

    /**
     * 清空所有友情链接
     */
    void deleteAll();

    /**
     * 批量插入友情链接
     */
    void batchInsert(@Param("list") List<OshFriendLink> list);
}
