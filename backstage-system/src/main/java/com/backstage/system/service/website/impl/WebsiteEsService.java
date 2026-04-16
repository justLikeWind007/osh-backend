package com.backstage.system.service.website.impl;
import com.alibaba.fastjson2.JSON;
import com.backstage.system.domain.dto.website.WebsiteQueryDTO;
import com.backstage.system.domain.website.WebsiteEsDoc;
import com.backstage.system.domain.vo.website.OshPracticalWebsiteVO;
import com.backstage.system.domain.website.WebsiteEsDoc;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 网站 ES 搜索服务
 * 负责：
 *   1. 从 ES 搜索网站列表
 *   2. 向 ES 写入/更新网站文档（审核通过时调用）
 */
@Service
public class WebsiteEsService {

    private static final Logger log = LoggerFactory.getLogger(WebsiteEsService.class);

    /** ES 索引名称，与你在 ES 中建的索引名保持一致 */
    private static final String INDEX_NAME = "osh_practical_website";

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 从 ES 搜索网站列表
     *
     * @param queryDTO 查询条件（名称、标签、分页）
     * @return 搜索结果列表，搜索不到返回空列表
     */
    public List<OshPracticalWebsiteVO> searchFromEs(WebsiteQueryDTO queryDTO) {
        try {
            // 1. 构建搜索请求
            SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // 2. 构建查询条件（bool 查询，可以组合多个条件）
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            // 2.1 按名称或描述全文搜索（只要名称或描述包含关键词就能匹配）
            if (queryDTO.getWebsiteName() != null && !queryDTO.getWebsiteName().isEmpty()) {
                boolQuery.must(
                        QueryBuilders.multiMatchQuery(queryDTO.getWebsiteName(), "name", "description")
                );
            }

            // 2.2 按标签精确筛选（每个标签都必须匹配）
            if (queryDTO.getTagNames() != null && !queryDTO.getTagNames().isEmpty()) {
                for (String tagName : queryDTO.getTagNames()) {
                    boolQuery.filter(QueryBuilders.termQuery("tags", tagName));
                }
            }

            // 如果没有任何条件，查全部
            if (!boolQuery.hasClauses()) {
                sourceBuilder.query(QueryBuilders.matchAllQuery());
            } else {
                sourceBuilder.query(boolQuery);
            }

            // 3. 分页设置
            int pageNum = queryDTO.getPageNum() == null ? 1 : queryDTO.getPageNum();
            int pageSize = queryDTO.getPageSize() == null ? 10 : queryDTO.getPageSize();
            sourceBuilder.from((pageNum - 1) * pageSize);  // 从第几条开始
            sourceBuilder.size(pageSize);                   // 每页多少条

            // 4. 按评分降序排列（与 MySQL 保持一致）
            sourceBuilder.sort("ratingScore", SortOrder.DESC);

            searchRequest.source(sourceBuilder);

            // 5. 执行搜索
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            // 6. 解析结果
            SearchHit[] hits = response.getHits().getHits();
            if (hits == null || hits.length == 0) {
                // ES 没有搜到结果，返回空列表，外层会降级走 MySQL
                return new ArrayList<>();
            }

            // 7. 把 ES 文档转换成 VO 对象返回
            List<OshPracticalWebsiteVO> result = new ArrayList<>();
            for (SearchHit hit : hits) {
                Map<String, Object> sourceMap = hit.getSourceAsMap();
                OshPracticalWebsiteVO vo = convertToVO(sourceMap);
                result.add(vo);
            }
            return result;

        } catch (Exception e) {
            // ES 查询异常（比如 ES 服务挂了），打日志后返回 null
            // 返回 null 表示 ES 不可用，外层会降级走 MySQL
            log.error("ES 搜索网站失败，将降级走 MySQL 查询", e);
            return null;
        }
    }

    /**
     * 向 ES 写入一条网站文档
     * 在网站审核通过时调用，把 MySQL 数据同步到 ES
     *
     * @param doc 要写入的网站文档
     */
    public void saveToEs(WebsiteEsDoc doc) {
        try {
            IndexRequest request = new IndexRequest(INDEX_NAME);
            // 用网站 ID 作为 ES 文档 ID，保证同一网站不会重复写入
            request.id(String.valueOf(doc.getId()));
            // 把对象转成 JSON 字符串写入 ES
            request.source(JSON.toJSONString(doc), XContentType.JSON);
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
            log.info("网站 ID={} 同步到 ES 成功", doc.getId());
        } catch (Exception e) {
            // ES 写入失败不影响主流程，只打日志
            log.error("网站 ID={} 同步到 ES 失败", doc.getId(), e);
        }
    }

    /**
     * 把 ES 返回的 Map 转换成 VO 对象
     */
    private OshPracticalWebsiteVO convertToVO(Map<String, Object> sourceMap) {
        OshPracticalWebsiteVO vo = new OshPracticalWebsiteVO();
        vo.setId(sourceMap.get("id") == null ? null : Long.valueOf(sourceMap.get("id").toString()));
        vo.setName((String) sourceMap.get("name"));
        vo.setUrl((String) sourceMap.get("url"));
        vo.setDescription((String) sourceMap.get("description"));
        vo.setLogoUrl((String) sourceMap.get("logoUrl"));
        vo.setClickCount(sourceMap.get("clickCount") == null ? 0 : (Integer) sourceMap.get("clickCount"));
        vo.setGoodCount(sourceMap.get("goodCount") == null ? 0 : (Integer) sourceMap.get("goodCount"));
        vo.setMidCount(sourceMap.get("midCount") == null ? 0 : (Integer) sourceMap.get("midCount"));
        vo.setBadCount(sourceMap.get("badCount") == null ? 0 : (Integer) sourceMap.get("badCount"));
        vo.setCollectionCount(sourceMap.get("collectionCount") == null ? 0 : (Integer) sourceMap.get("collectionCount"));
        if (sourceMap.get("ratingScore") != null) {
            vo.setRatingScore(new BigDecimal(sourceMap.get("ratingScore").toString()));
        }
        // tags 在 ES 里是数组，转成逗号分隔的字符串（与现有 VO 格式保持一致）
        if (sourceMap.get("tags") != null) {
            List<String> tagList = (List<String>) sourceMap.get("tags");
            vo.setTags(String.join(",", tagList));
        }
        return vo;
    }
}
