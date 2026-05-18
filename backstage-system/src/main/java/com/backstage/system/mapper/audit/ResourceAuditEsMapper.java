package com.backstage.system.mapper.audit;

import com.backstage.common.enums.ResourceTypeEnum;
import com.backstage.common.response.PageResponse;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.config.properties.SearchEsProperties;
import com.backstage.system.domain.audit.ResourceAuditItemVO;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ResourceAuditEsMapper {

    private static final String COURSE_SEARCH_INDEX = "osh_course_search_read";
    private static final String TOOL_SEARCH_INDEX = "osh_tool_search";
    private static final int PENDING_STATUS = 2;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private SearchEsProperties searchEsProperties;

    /**
     * 是否支持ES查询, 暂时只处理了课程和工具
     * @param resourceType
     * @return
     */
    public boolean supports(ResourceTypeEnum resourceType) {
        return ResourceTypeEnum.COURSE == resourceType || ResourceTypeEnum.TOOL == resourceType;
    }

    public PageResponse<ResourceAuditItemVO> searchPending(ResourceTypeEnum resourceType,
                                                           String keyword,
                                                           int pageNum,
                                                           int pageSize) throws Exception {
        String indexName = resolveIndexName(resourceType);
        if (!restHighLevelClient.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT)) {
            return PageResponse.of(new ArrayList<>(), 0L, pageNum, pageSize);
        }

        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(buildSearchSource(resourceType, keyword, pageNum, pageSize));
        SearchResponse searchResponse;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException ex) {
            if (ex.status() != null && ex.status().getStatus() == 404) {
                return PageResponse.of(new ArrayList<>(), 0L, pageNum, pageSize);
            }
            throw ex;
        }
        if (searchResponse.isTimedOut()) {
            throw new IllegalStateException("search audit resources from es timed out");
        }

        List<ResourceAuditItemVO> rows = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            rows.add(toAuditItem(resourceType, hit.getSourceAsMap()));
        }
        return PageResponse.of(rows, searchResponse.getHits().getTotalHits().value, pageNum, pageSize);
    }

    private SearchSourceBuilder buildSearchSource(ResourceTypeEnum resourceType, String keyword, int pageNum, int pageSize) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from((pageNum - 1) * pageSize)
                .size(pageSize)
                .timeout(TimeValue.timeValueMillis(searchEsProperties.getFallbackTimeoutMillis()));
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("status", PENDING_STATUS))
                .filter(QueryBuilders.termQuery("deleteFlag", 0));

        if (StringUtils.isNotEmpty(keyword)) {
            boolQuery.must(buildKeywordQuery(resourceType, keyword));
            sourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
        }

        sourceBuilder.query(boolQuery);
        sourceBuilder.sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC));
        return sourceBuilder;
    }

    private QueryBuilder buildKeywordQuery(ResourceTypeEnum resourceType, String keyword) {
        if (ResourceTypeEnum.TOOL == resourceType) {
            return QueryBuilders.multiMatchQuery(keyword)
                    .field("toolName", 8.0f)
                    .field("tagNamesText", 4.0f)
                    .field("description", 3.0f)
                    .field("searchText", 1.0f)
                    .type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
        }
        return QueryBuilders.multiMatchQuery(keyword)
                .field("title", 8.0f)
                .field("tagNamesText", 4.0f)
                .field("intro", 3.0f)
                .field("serviceContent", 2.0f)
                .field("searchText", 1.0f)
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
    }

    private ResourceAuditItemVO toAuditItem(ResourceTypeEnum resourceType, Map<String, Object> source) {
        ResourceAuditItemVO item = new ResourceAuditItemVO();
        item.setId(toLong(source.get("id")));
        item.setStatus(toInteger(source.get("status")));
        item.setLevel(toInteger(source.get("level")));
        item.setCreateBy(toStringValue(source.get("createBy")));
        item.setCreateTime(parseLocalDateTime(source.get("createTime")));
        if (ResourceTypeEnum.TOOL == resourceType) {
            item.setTitle(toStringValue(source.get("toolName")));
            item.setDescription(toStringValue(source.get("description")));
            item.setCover(toStringValue(source.get("logoUrl")));
            item.setUrl(firstNotEmpty(source.get("routePath"), source.get("iframeUrl"), source.get("githubUrl")));
        } else {
            item.setTitle(toStringValue(source.get("title")));
            item.setDescription(firstNotEmpty(source.get("intro"), source.get("serviceContent")));
            item.setCover(toStringValue(source.get("cover")));
        }
        return item;
    }

    private String resolveIndexName(ResourceTypeEnum resourceType) {
        if (ResourceTypeEnum.TOOL == resourceType) {
            return TOOL_SEARCH_INDEX;
        }
        if (ResourceTypeEnum.COURSE == resourceType) {
            return COURSE_SEARCH_INDEX;
        }
        throw new IllegalArgumentException("资源类型不支持ES审核查询");
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.valueOf(String.valueOf(value));
    }

    private String toStringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String firstNotEmpty(Object... values) {
        for (Object value : values) {
            String text = toStringValue(value);
            if (StringUtils.isNotEmpty(text)) {
                return text;
            }
        }
        return null;
    }

    private LocalDateTime parseLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof Number) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(((Number) value).longValue()), ZoneId.systemDefault());
        }
        String text = String.valueOf(value).trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception ignored) {
            return LocalDateTime.parse(text, new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd HH:mm:ss")
                    .optionalStart()
                    .appendFraction(ChronoField.MILLI_OF_SECOND, 1, 3, true)
                    .optionalEnd()
                    .toFormatter());
        }
    }
}
