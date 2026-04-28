package com.backstage.system.service.website.impl;

import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.dto.website.WebsiteQueryDTO;
import com.backstage.system.domain.vo.website.EsPageResult;
import com.backstage.system.domain.vo.website.OshPracticalWebsiteVO;
import com.backstage.system.domain.website.WebsiteEsDoc;
import com.backstage.system.mapper.website.OshPracticalWebsiteMapper;
import com.backstage.system.mapper.website.OshWebsiteEsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 实用网站 ES Service
 *
 * 职责：业务逻辑层，不直接操作 ES，委托给 OshWebsiteEsMapper
 *   1. 搜索网站（含降级逻辑：ES 异常时返回 null，上层降级走 MySQL）
 *   2. 单条写入 ES（审核通过时调用）
 *   3. 全量同步（先清空再批量写入）
 *   4. 重建索引
 */
@Service
public class WebsiteEsService {

    private static final Logger log = LoggerFactory.getLogger(WebsiteEsService.class);

    @Autowired
    private OshWebsiteEsMapper oshWebsiteEsMapper;

    @Autowired
    private OshPracticalWebsiteMapper oshPracticalWebsiteMapper;

    /**
     * 从 ES 搜索网站列表
     *
     * @param queryDTO 查询条件
     * @return 搜索结果；ES 不可用时返回 null，上层降级走 MySQL
     */
    public EsPageResult<OshPracticalWebsiteVO> searchFromEs(WebsiteQueryDTO queryDTO) {
        try {
            PageResponse<OshPracticalWebsiteVO> pageResponse = oshWebsiteEsMapper.searchWebsites(queryDTO);
            log.info("ES 查询结果: total={}, rows={}", pageResponse.getTotal(), pageResponse.getRows().size());
            return new EsPageResult<>(pageResponse.getRows(), pageResponse.getTotal());
        } catch (Exception e) {
            log.error("ES 搜索网站失败，将降级走 MySQL 查询", e);
            // 返回 null 触发上层降级逻辑
            return null;
        }
    }

    /**
     * 向 ES 写入单条网站文档
     * 在网站审核通过时调用，把 MySQL 数据同步到 ES
     *
     * @param doc 要写入的网站文档
     */
    public void saveToEs(WebsiteEsDoc doc) {
        try {
            oshWebsiteEsMapper.bulkUpsertWebsites(Collections.singletonList(doc));
            log.info("网站 ID={} 同步到 ES 成功", doc.getId());
        } catch (Exception e) {
            // ES 写入失败不影响主流程，只打日志
            log.error("网站 ID={} 同步到 ES 失败", doc.getId(), e);
        }
    }

    /**
     * 全量同步网站到 ES
     * 从 MySQL 查出所有已审核通过的网站，先清空 ES 再批量写入
     *
     * @return 成功同步的文档数量
     */
    public int syncAllToEs() {
        // 1. 从 MySQL 查出所有已审核通过的网站（含标签）
        List<OshPracticalWebsiteVO> voList = oshPracticalWebsiteMapper.selectAllPublishedWebsites();
        if (voList == null || voList.isEmpty()) {
            log.info("没有已审核通过的网站，跳过全量同步");
            return 0;
        }

        // 2. VO 转 EsDoc
        List<WebsiteEsDoc> docs = new ArrayList<>(voList.size());
        for (OshPracticalWebsiteVO vo : voList) {
            docs.add(convertVoToEsDoc(vo));
        }

        // 3. 清空 ES + 批量写入
        try {
            int deleted = oshWebsiteEsMapper.deleteAllWebsites();
            log.info("全量同步前清空 ES，共删除 {} 条文档", deleted);
            int synced = oshWebsiteEsMapper.bulkUpsertWebsites(docs);
            log.info("全量同步完成，共写入 {} 条文档", synced);
            return synced;
        } catch (Exception e) {
            throw new IllegalStateException("全量同步网站到 ES 失败", e);
        }
    }

    /**
     * 重建 ES 索引
     * 删除旧索引并按传入的 mapping 定义重新创建
     *
     * @param indexDefinitionJson 索引定义 JSON（来自 osh_practical_website_index.json）
     */
    public void recreateIndex(String indexDefinitionJson) {
        try {
            oshWebsiteEsMapper.recreateWebsiteSearchIndex(indexDefinitionJson);
            log.info("网站 ES 索引重建成功");
        } catch (Exception e) {
            throw new IllegalStateException("重建网站 ES 索引失败", e);
        }
    }

    /**
     * VO 转 ES 文档
     * tags 字段：VO 里是逗号分隔字符串，EsDoc 里是 List<String>
     */
    private WebsiteEsDoc convertVoToEsDoc(OshPracticalWebsiteVO vo) {
        WebsiteEsDoc doc = new WebsiteEsDoc();
        doc.setId(vo.getId());
        doc.setName(vo.getName());
        doc.setUrl(vo.getUrl());
        doc.setDescription(vo.getDescription());
        doc.setLogoUrl(vo.getLogoUrl());
        doc.setClickCount(vo.getClickCount());
        doc.setGoodCount(vo.getGoodCount());
        doc.setMidCount(vo.getMidCount());
        doc.setBadCount(vo.getBadCount());
        doc.setCollectionCount(vo.getCollectionCount());
        doc.setRatingScore(vo.getRatingScore());
        doc.setAuditTime(vo.getAuditTime());
        // tags：逗号分隔字符串 → List<String>，trim 掉空格防止精确匹配失败
        if (vo.getTags() != null && !vo.getTags().isEmpty()) {
            doc.setTags(
                Arrays.stream(vo.getTags().split(","))
                      .map(String::trim)
                      .filter(s -> !s.isEmpty())
                      .collect(java.util.stream.Collectors.toList())
            );
        }
        return doc;
    }
}
