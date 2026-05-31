package com.backstage.system.mapper.website;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backstage.system.domain.website.OshWebsiteTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 实用网站标签 Mapper 接口
 */
public interface OshWebsiteTagMapper extends BaseMapper<OshWebsiteTag> {

    /**
     * 根据标签名称精确查询（用于判断是否已存在）
     *
     * @param tagName 标签名称
     * @return 标签对象，不存在返回 null
     */
    OshWebsiteTag selectByTagName(@Param("tagName") String tagName);

    /**
     * 新增标签
     *
     * @param tag 标签对象
     * @return 影响行数
     */
    int insertWebsiteTag(OshWebsiteTag tag);

    /**
     * 增加标签使用次数
     *
     * @param id 标签 ID
     * @return 影响行数
     */
    int increaseUseCount(@Param("id") Long id);

    /**
     * 减少标签使用次数（删除网站时调用）
     *
     * @param id 标签 ID
     * @return 影响行数
     */
    int decreaseUseCount(@Param("id") Long id);

    /**
     * 查询所有启用标签，按使用次数降序（供前端标签筛选器使用）
     *
     * @return 标签列表（含 id、tagName、useCount）
     */
    List<Map<String, Object>> selectAllTags();

    /**
     * 根据关键字模糊查询标签，按使用次数降序，最多返回 20 条
     *
     * @param keyword 关键字（可选，为空则查全部）
     * @return 标签列表（含 id、tagName、useCount）
     */
    List<Map<String, Object>> selectTagsByKeyword(@Param("keyword") String keyword);

    /**
     * 根据标签名称列表批量查询（旧接口保留，供兼容使用）
     *
     * @param tagNames 标签名称列表
     * @return 标签列表
     */
    List<OshWebsiteTag> selectByTagNames(@Param("tagNames") List<String> tagNames);
}
