package com.backstage.system.mapper.assistant;

import cn.hutool.core.util.StrUtil;
import com.backstage.common.response.PageResponse;
import com.backstage.system.config.properties.SearchEsProperties;
import com.backstage.system.domain.assistant.AssistantFeedback;
import com.backstage.system.domain.assistant.AssistantTicketStatus;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackPageDTO;
import com.backstage.system.domain.assistant.es.AssistantFeedbackEsDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 反馈 ES 查询 Mapper
 *
 * @author backstage
 */
@Component
public class AssistantFeedbackEsMapper {

    private static final String FEEDBACK_SEARCH_INDEX = "osh_assistant_feedback_search";
    private static final String QUERY_MODE_MINE = "mine";
    private static final String DEFAULT_SORT_TYPE = "hot";
    private static final String SORT_TYPE_RELATED = "related";

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;
    private final SearchEsProperties searchEsProperties;

    public AssistantFeedbackEsMapper(RestHighLevelClient restHighLevelClient,
                                     ObjectMapper objectMapper,
                                     SearchEsProperties searchEsProperties) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
        this.searchEsProperties = searchEsProperties;
    }

    public PageResponse<AssistantFeedback> searchFeedbacks(AssistantFeedbackPageDTO dto, List<Long> favoriteFeedbackIds) throws Exception {
        int pageNum = resolvePageNum(dto);
        int pageSize = resolvePageSize(dto);
        if (favoriteFeedbackIds != null && favoriteFeedbackIds.isEmpty()) {
            return PageResponse.of(Collections.emptyList(), 0L, pageNum, pageSize);
        }
        ensureIndexExists();

        SearchRequest searchRequest = new SearchRequest(FEEDBACK_SEARCH_INDEX);
        searchRequest.source(buildSearchSource(dto, pageNum, pageSize, favoriteFeedbackIds));
        SearchResponse searchResponse;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException exception) {
            if (exception.status() != null && exception.status().getStatus() == 404) {
                throw new IllegalStateException("feedback search index missing", exception);
            }
            throw exception;
        }
        if (searchResponse.isTimedOut()) {
            throw new IllegalStateException("search feedbacks from es timed out");
        }

        List<AssistantFeedback> rows = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            rows.add(toFeedback(searchHit.getSourceAsMap()));
        }
        return PageResponse.of(rows, searchResponse.getHits().getTotalHits().value, pageNum, pageSize);
    }

    public int bulkUpsertFeedbacks(List<AssistantFeedbackEsDocument> documents) throws Exception {
        if (documents == null || documents.isEmpty()) {
            return 0;
        }
        BulkRequest bulkRequest = new BulkRequest();
        for (AssistantFeedbackEsDocument document : documents) {
            bulkRequest.add(new IndexRequest(FEEDBACK_SEARCH_INDEX)
                    .id(String.valueOf(document.getId()))
                    .source(writeDocument(document), XContentType.JSON));
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (bulkResponse.hasFailures()) {
            throw new IllegalStateException("bulk upsert feedbacks to es failed: " + bulkResponse.buildFailureMessage());
        }
        return documents.size();
    }

    public void upsertFeedback(AssistantFeedbackEsDocument document) throws Exception {
        if (document == null || document.getId() == null) {
            return;
        }
        IndexRequest indexRequest = new IndexRequest(FEEDBACK_SEARCH_INDEX)
                .id(String.valueOf(document.getId()))
                .source(writeDocument(document), XContentType.JSON);
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void deleteFeedback(Long feedbackId) throws Exception {
        if (feedbackId == null || !indexExists()) {
            return;
        }
        DeleteRequest deleteRequest = new DeleteRequest(FEEDBACK_SEARCH_INDEX, String.valueOf(feedbackId));
        restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
    }

    public int deleteAllFeedbacks() throws Exception {
        if (!indexExists()) {
            return 0;
        }
        Request request = new Request("POST", "/" + FEEDBACK_SEARCH_INDEX + "/_delete_by_query");
        request.addParameter("refresh", "true");
        request.addParameter("conflicts", "proceed");
        request.setJsonEntity("{\"query\":{\"match_all\":{}}}");
        Response response = restHighLevelClient.getLowLevelClient().performRequest(request);
        String json = EntityUtils.toString(response.getEntity(), "UTF-8");
        JsonNode root = objectMapper.readTree(json);
        return root.path("deleted").asInt(0);
    }

    public void deleteIndex() throws Exception {
        if (!indexExists()) {
            return;
        }
        Request request = new Request("DELETE", "/" + FEEDBACK_SEARCH_INDEX);
        restHighLevelClient.getLowLevelClient().performRequest(request);
    }

    public void createIndexWithMapping() throws Exception {
        if (indexExists()) {
            throw new IllegalStateException("feedback search index already exists");
        }
        String mappingJson = buildIndexMappingJson();
        Request request = new Request("PUT", "/" + FEEDBACK_SEARCH_INDEX);
        request.setJsonEntity(mappingJson);
        restHighLevelClient.getLowLevelClient().performRequest(request);
    }

    private String buildIndexMappingJson() {
        return "{\n" +
                "  \"settings\": {\n" +
                "    \"number_of_shards\": 1,\n" +
                "    \"number_of_replicas\": 1,\n" +
                "    \"analysis\": {\n" +
                "      \"analyzer\": {\n" +
                "        \"ik_max_word\": {\n" +
                "          \"type\": \"custom\",\n" +
                "          \"tokenizer\": \"ik_max_word\"\n" +
                "        },\n" +
                "        \"ik_smart\": {\n" +
                "          \"type\": \"custom\",\n" +
                "          \"tokenizer\": \"ik_smart\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"id\": { \"type\": \"keyword\" },\n" +
                "      \"userId\": { \"type\": \"keyword\" },\n" +
                "      \"categoryId\": { \"type\": \"keyword\" },\n" +
                "      \"categoryCode\": { \"type\": \"keyword\" },\n" +
                "      \"ticketNo\": { \"type\": \"keyword\" },\n" +
                "      \"title\": { \"type\": \"text\", \"analyzer\": \"ik_max_word\", \"search_analyzer\": \"ik_smart\" },\n" +
                "      \"content\": { \"type\": \"text\", \"analyzer\": \"ik_max_word\", \"search_analyzer\": \"ik_smart\" },\n" +
                "      \"status\": { \"type\": \"keyword\" },\n" +
                "      \"isPinned\": { \"type\": \"integer\" },\n" +
                "      \"pinOrder\": { \"type\": \"integer\" },\n" +
                "      \"commentCount\": { \"type\": \"integer\" },\n" +
                "      \"viewCount\": { \"type\": \"integer\" },\n" +
                "      \"likeCount\": { \"type\": \"integer\" },\n" +
                "      \"favoriteCount\": { \"type\": \"integer\" },\n" +
                "      \"hotScore\": { \"type\": \"integer\" },\n" +
                "      \"tagIds\": { \"type\": \"keyword\" },\n" +
                "      \"mineStatusPriority\": { \"type\": \"integer\" },\n" +
                "      \"deleteFlag\": { \"type\": \"integer\" },\n" +
                "      \"createTime\": { \"type\": \"date\", \"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss||epoch_millis\" },\n" +
                "      \"updateTime\": { \"type\": \"date\", \"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss||epoch_millis\" }\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    private SearchSourceBuilder buildSearchSource(AssistantFeedbackPageDTO dto,
                                                  int pageNum,
                                                  int pageSize,
                                                  List<Long> favoriteFeedbackIds) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .from((pageNum - 1) * pageSize)
                .size(pageSize)
                .timeout(TimeValue.timeValueMillis(searchEsProperties.getFallbackTimeoutMillis()));
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("deleteFlag", 0))
                .mustNot(QueryBuilders.termQuery("categoryCode", "announcement"));

        if (favoriteFeedbackIds != null) {
            boolQuery.filter(QueryBuilders.termsQuery("id", favoriteFeedbackIds.stream()
                    .map(String::valueOf).collect(Collectors.toList())));
        }
        if (dto.getCategoryId() != null) {
            boolQuery.filter(QueryBuilders.termQuery("categoryId", String.valueOf(dto.getCategoryId())));
        }
        if (StrUtil.isNotBlank(dto.getCategoryCode())) {
            boolQuery.filter(QueryBuilders.termQuery("categoryCode", dto.getCategoryCode().trim()));
        }
        if (StrUtil.isNotBlank(dto.getStatus())) {
            boolQuery.filter(QueryBuilders.termQuery("status", AssistantTicketStatus.normalize(dto.getStatus())));
        }
        if (dto.getIsPinned() != null) {
            boolQuery.filter(QueryBuilders.termQuery("isPinned", dto.getIsPinned()));
        }
        if (QUERY_MODE_MINE.equals(normalizeQueryMode(dto.getQueryMode())) && dto.getUserId() != null) {
            boolQuery.filter(QueryBuilders.termQuery("userId", String.valueOf(dto.getUserId())));
        }
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            boolQuery.filter(QueryBuilders.termsQuery("tagIds", dto.getTagIds().stream()
                    .map(String::valueOf).collect(Collectors.toList())));
        }
        if (StrUtil.isNotBlank(dto.getKeyword())) {
            boolQuery.must(buildKeywordQuery(dto.getKeyword().trim()));
        }

        sourceBuilder.query(boolQuery);
        appendSorts(sourceBuilder, dto);
        return sourceBuilder;
    }

    private void appendSorts(SearchSourceBuilder sourceBuilder, AssistantFeedbackPageDTO dto) {
        boolean searchMode = StrUtil.isNotBlank(dto.getKeyword());
        if (!searchMode && QUERY_MODE_MINE.equals(normalizeQueryMode(dto.getQueryMode()))) {
            sourceBuilder.sort(SortBuilders.fieldSort("mineStatusPriority").order(SortOrder.ASC).missing("_last").unmappedType("integer"));
        }
        if (!searchMode) {
            sourceBuilder.sort(SortBuilders.fieldSort("isPinned").order(SortOrder.DESC).missing("_last").unmappedType("integer"));
            sourceBuilder.sort(SortBuilders.fieldSort("pinOrder").order(SortOrder.ASC).missing("_last").unmappedType("integer"));
        }

        String sortType = resolveSortType(dto.getSortType(), searchMode);
        if (SORT_TYPE_RELATED.equals(sortType)) {
            sourceBuilder.sort(SortBuilders.scoreSort().order(SortOrder.DESC));
            sourceBuilder.sort(SortBuilders.fieldSort("hotScore").order(SortOrder.DESC).missing("_last").unmappedType("integer"));
            sourceBuilder.sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC).missing("_last").unmappedType("date"));
            return;
        }
        if ("comment".equals(sortType)) {
            sourceBuilder.sort(SortBuilders.fieldSort("commentCount").order(SortOrder.DESC).missing("_last").unmappedType("integer"));
            sourceBuilder.sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC).missing("_last").unmappedType("date"));
            return;
        }
        if ("latest".equals(sortType)) {
            sourceBuilder.sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC).missing("_last").unmappedType("date"));
            return;
        }
        sourceBuilder.sort(SortBuilders.fieldSort("hotScore").order(SortOrder.DESC).missing("_last").unmappedType("integer"));
        sourceBuilder.sort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC).missing("_last").unmappedType("date"));
    }

    private String resolveSortType(String sortType, boolean searchMode) {
        String resolvedSortType = StrUtil.isBlank(sortType) ? DEFAULT_SORT_TYPE : sortType.trim().toLowerCase();
        if (SORT_TYPE_RELATED.equals(resolvedSortType) && !searchMode) {
            return DEFAULT_SORT_TYPE;
        }
        return resolvedSortType;
    }

    private QueryBuilder buildKeywordQuery(String keyword) {
        return QueryBuilders.multiMatchQuery(keyword)
                .field("title", 8.0f)
                .field("content", 4.0f)
                .field("ticketNo", 2.0f)
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
    }

    private AssistantFeedback toFeedback(Map<String, Object> source) {
        AssistantFeedbackEsDocument document = objectMapper.convertValue(source, AssistantFeedbackEsDocument.class);
        fillTimeFields(document, source);
        AssistantFeedback feedback = new AssistantFeedback();
        feedback.setId(document.getId());
        feedback.setUserId(document.getUserId());
        feedback.setCategoryId(document.getCategoryId());
        feedback.setTicketNo(document.getTicketNo());
        feedback.setTitle(document.getTitle());
        feedback.setContent(document.getContent());
        feedback.setStatus(document.getStatus());
        feedback.setIsPinned(document.getIsPinned());
        feedback.setPinOrder(document.getPinOrder());
        feedback.setCommentCount(document.getCommentCount());
        feedback.setViewCount(document.getViewCount());
        feedback.setLikeCount(document.getLikeCount());
        feedback.setFavoriteCount(document.getFavoriteCount());
        feedback.setHotScore(document.getHotScore());
        feedback.setCreateTime(document.getCreateTime());
        feedback.setUpdateTime(document.getUpdateTime());
        feedback.setDeleteFlag(document.getDeleteFlag() == null ? (byte) 0 : document.getDeleteFlag().byteValue());
        return feedback;
    }

    private void fillTimeFields(AssistantFeedbackEsDocument document, Map<String, Object> source) {
        if (document.getCreateTime() == null) {
            document.setCreateTime(parseLocalDateTime(source.get("createTime")));
        }
        if (document.getUpdateTime() == null) {
            document.setUpdateTime(parseLocalDateTime(source.get("updateTime")));
        }
    }

    private LocalDateTime parseLocalDateTime(Object value) {
        if (value == null) {
            return null;
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

    private String normalizeQueryMode(String queryMode) {
        if (StrUtil.isBlank(queryMode)) {
            return "all";
        }
        return queryMode.trim().toLowerCase();
    }

    private int resolvePageNum(AssistantFeedbackPageDTO dto) {
        if (dto == null || dto.getPageNum() == null || dto.getPageNum() < 1) {
            return 1;
        }
        return dto.getPageNum();
    }

    private int resolvePageSize(AssistantFeedbackPageDTO dto) {
        if (dto == null || dto.getPageSize() == null || dto.getPageSize() < 1) {
            return 10;
        }
        return dto.getPageSize();
    }

    private boolean indexExists() throws Exception {
        return restHighLevelClient.indices().exists(new GetIndexRequest(FEEDBACK_SEARCH_INDEX), RequestOptions.DEFAULT);
    }

    private void ensureIndexExists() throws Exception {
        if (!indexExists()) {
            throw new IllegalStateException("feedback search index missing");
        }
    }

    private String writeDocument(AssistantFeedbackEsDocument document) throws Exception {
        return objectMapper.writeValueAsString(document);
    }
}
