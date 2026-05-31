package com.backstage.system.service.website;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backstage.system.domain.website.OshWebsiteTag;

import java.util.List;
import java.util.Map;

/**
 * 实用网站标签 Service 接口
 */
public interface OshWebsiteTagService extends IService<OshWebsiteTag> {

    /**
     * 查询所有标签（按使用次数降序），供前端标签筛选器使用
     *
     * @return 标签列表（含 id、tagName、useCount）
     */
    List<Map<String, Object>> getAllTags();

    /**
     * 根据关键字模糊查询标签（最多 20 条），供前端搜索输入框使用
     *
     * @param keyword 关键字（可选，为空则返回全部）
     * @return 标签列表（含 id、tagName、useCount）
     */
    List<Map<String, Object>> searchTags(String keyword);

    /**
     * 解析标签名称：存在则直接返回，不存在则自动创建。
     * 参考课程模块 resolveCourseTag 逻辑，含并发安全兜底。
     *
     * @param tagName  标签名称
     * @param operator 操作人用户名（可为 null）
     * @return 标签对象
     */
    OshWebsiteTag resolveTag(String tagName, String operator);

    /**
     * 为网站绑定标签列表：
     * 1. 清洗去重标签名
     * 2. 逐个 resolveTag（不存在则创建）
     * 3. 插入关联记录
     * 4. 增加 use_count
     *
     * @param websiteId 网站 ID
     * @param tagNames  标签名称列表
     * @param operator  操作人用户名
     */
    void bindWebsiteTags(Long websiteId, List<String> tagNames, String operator);

    /**
     * 重建网站标签（先删旧关联，再绑新标签），用于编辑网站时更新标签。
     *
     * @param websiteId 网站 ID
     * @param tagNames  新标签名称列表
     * @param operator  操作人用户名
     */
    void rebuildWebsiteTags(Long websiteId, List<String> tagNames, String operator);
}
