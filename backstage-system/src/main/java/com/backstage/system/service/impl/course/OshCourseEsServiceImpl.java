package com.backstage.system.service.impl.course;

import com.backstage.common.response.PageResponse;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.course.es.OshCourseEsDocument;
import com.backstage.system.domain.course.vo.CourseSearchLoginVo;
import com.backstage.system.domain.user.CurrentUser;
import com.backstage.system.enums.CourseResourceEnum;
import com.backstage.system.mapper.course.OshCourseEsMapper;
import com.backstage.system.mapper.course.OshCourseCollectionMapper;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.mapper.course.OshCourseTagMapper;
import com.backstage.system.request.CourseSearchRequest;
import com.backstage.system.service.course.ICourseManageService;
import com.backstage.system.service.course.IOshCourseEsService;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OshCourseEsServiceImpl implements IOshCourseEsService {

    private static final Logger log = LoggerFactory.getLogger(OshCourseEsServiceImpl.class);

    @Autowired
    private OshCourseEsMapper oshCourseEsMapper;

    @Autowired
    private OshCourseMapper oshCourseMapper;

    @Autowired
    private OshCourseCollectionMapper oshCourseCollectionMapper;

    @Autowired
    private OshCourseTagMapper oshCourseTagMapper;

    @Autowired
    private ICourseManageService courseManageService;

    @Override
    public PageResponse<CourseSearchLoginVo> searchCourses(CourseSearchRequest request, Long userId) {
        PageResponse<CourseSearchLoginVo> pageResponse;
        try {
            if (Integer.valueOf(1).equals(request.getCollectionFlag())) {
                pageResponse = searchCollectedCourses(request, userId);
            } else {
                pageResponse = oshCourseEsMapper.searchCourses(request);
            }
        } catch (Exception ex) {
            log.error("search courses from es failed, request={}, userId={}", request, userId, ex);
            throw new IllegalStateException("search courses from es failed", ex);
        }

        List<CourseSearchLoginVo> rows = pageResponse.getRows();
        if (StringUtils.isEmpty(rows)) {
            return pageResponse;
        }

        fillCollectionFlag(rows, userId);
        fillBuyFlag(rows, userId);
        fillResourceTypeDesc(rows, userId);
        convertToExpiryUrls(rows);
        return pageResponse;
    }

    private PageResponse<CourseSearchLoginVo> searchCollectedCourses(CourseSearchRequest request, Long userId) throws Exception {
        if (userId == null) {
            return PageResponse.of(Collections.emptyList(), 0L, request.getPageNum(), request.getPageSize());
        }

        List<Long> collectedCourseIds = oshCourseCollectionMapper.selectActiveCourseIdsByUserId(userId);
        if (StringUtils.isEmpty(collectedCourseIds)) {
            return PageResponse.of(Collections.emptyList(), 0L, request.getPageNum(), request.getPageSize());
        }

        CourseSearchRequest collectionSearchRequest = buildCollectionSearchRequest(request, collectedCourseIds.size());
        PageResponse<CourseSearchLoginVo> searchResponse = oshCourseEsMapper.searchCourses(collectionSearchRequest, collectedCourseIds);
        List<CourseSearchLoginVo> orderedRows = sortRowsByCollectedOrder(searchResponse.getRows(), collectedCourseIds);
        return buildPagedResponse(orderedRows, request.getPageNum(), request.getPageSize());
    }

    /**
     * 全量同步课程数据到Elasticsearch索引
     * <p>
     * 功能说明：
     * 1. 清空ES索引中的所有课程文档（保留索引结构）
     * 2. 分页从MySQL数据库查询课程信息（每页200条）
     * 3. 将MySQL数据转换为ES文档格式（包括标签、搜索文本等增强字段）
     * 4. 使用Bulk API批量写入ES，提高同步效率
     * 5. 返回成功同步的课程总数
     * <p>
     * 使用场景：
     * - 系统初始化时建立ES索引
     * - ES数据丢失或损坏时的数据修复
     * - 手动触发的全量同步任务（通常由管理员操作）
     * - 定时任务（如每日凌晨全量同步一次）
     * <p>
     * 技术要点：
     * - 分页同步：避免一次性加载全部数据导致内存溢出
     * - 批量写入：使用ES Bulk API减少网络往返次数
     * - 错误处理：任何一步失败都会抛出异常终止同步
     * - 事务安全：先删除后写入，确保数据一致性
     * <p>
     * 性能指标（预估）：
     * - 单次同步万级课程数据约需1-3分钟
     * - Bulk批量写入可达到1000条/秒的写入速度
     *
     * @return int 成功同步的课程总数
     * @throws IllegalStateException 清空索引失败或写入ES失败时抛出
     */
    @Override
    public int syncAllCoursesToEs() {
        int pageNum = 1;
        int pageSize = 200;
        int total = 0;

        // 步骤1：清空ES索引中的所有课程文档
        // 使用Delete By Query API删除所有文档，保留索引结构以便重新写入
        try {
            oshCourseEsMapper.deleteAllCourses();
        } catch (Exception ex) {
            throw new IllegalStateException("clear courses in es failed", ex);
        }

        // 步骤2：分页从MySQL查询课程数据并同步到ES
        // 使用while循环实现游标分页，每页200条，避免一次性加载全部数据
        while (true) {
            // 构建分页查询请求
            CourseSearchRequest request = new CourseSearchRequest();
            request.setPageNum(pageNum);
            request.setPageSize(pageSize);

            // 使用PageHelper分页插件进行MySQL分页查询
            PageHelper.startPage(pageNum, pageSize);
            CurrentUser currentUser = new CurrentUser();
            List<CourseSearchLoginVo> rows = oshCourseMapper.pageQuerySearchCourse(request, currentUser.getId());
            
            // 无数据时退出循环，同步完成
            if (StringUtils.isEmpty(rows)) {
                break;
            }

            // 步骤3：将MySQL查询结果转换为ES文档格式
            // 转换过程中会补充标签信息、构建搜索文本等增强字段
            List<OshCourseEsDocument> documents = new ArrayList<>(rows.size());
            for (CourseSearchLoginVo row : rows) {
                documents.add(buildEsDocument(row));
            }

            // 步骤4：使用ES Bulk API批量写入文档
            // Bulk API可以显著减少网络往返次数，提高写入效率
            try {
                total += oshCourseEsMapper.bulkUpsertCourses(documents);
            } catch (Exception ex) {
                throw new IllegalStateException("sync courses to es failed", ex);
            }

            // 步骤5：判断是否为最后一页
            // 如果返回的数据量小于pageSize，说明已经是最后一页，可以结束循环
            if (rows.size() < pageSize) {
                break;
            }
            pageNum++;
        }

        // 返回成功同步的课程总数
        return total;
    }

    private void fillBuyFlag(List<CourseSearchLoginVo> rows, Long userId) {
        if (userId == null || StringUtils.isEmpty(rows)) {
            return;
        }
        List<Long> courseIds = rows.stream()
                .map(CourseSearchLoginVo::getId)
                .collect(Collectors.toList());
        if (StringUtils.isEmpty(courseIds)) {
            return;
        }

        List<Long> boughtCourseIds = oshCourseMapper.selectUserBoughtCourseIds(userId, courseIds);
        if (StringUtils.isEmpty(boughtCourseIds)) {
            return;
        }
        Set<Long> boughtCourseIdSet = new HashSet<>(boughtCourseIds);

        for (CourseSearchLoginVo row : rows) {
            if (boughtCourseIdSet.contains(row.getId())) {
                row.setBuyFlag(1);
            }
        }
    }

    private void fillCollectionFlag(List<CourseSearchLoginVo> rows, Long userId) {
        if (userId == null || StringUtils.isEmpty(rows)) {
            return;
        }
        List<Long> courseIds = rows.stream()
                .map(CourseSearchLoginVo::getId)
                .collect(Collectors.toList());
        if (StringUtils.isEmpty(courseIds)) {
            return;
        }

        List<Long> collectedCourseIds = oshCourseCollectionMapper.selectActiveCourseIdsByUserIdAndCourseIds(userId, courseIds);
        if (StringUtils.isEmpty(collectedCourseIds)) {
            return;
        }
        Set<Long> collectedCourseIdSet = new HashSet<>(collectedCourseIds);

        for (CourseSearchLoginVo row : rows) {
            if (collectedCourseIdSet.contains(row.getId())) {
                row.setCollectionFlag(1);
            }
        }
    }

    private List<CourseSearchLoginVo> convertToExpiryUrls(List<CourseSearchLoginVo> rows) {
        if (StringUtils.isEmpty(rows)) {
            return rows;
        }
        List<Long> courseIds = rows.stream()
                .map(CourseSearchLoginVo::getId)
                .collect(Collectors.toList());
        Map<Long, String> signedUrlMap = courseManageService.batchGetCourseCoverUrls(courseIds, 60);
        if (signedUrlMap == null) {
            signedUrlMap = Collections.emptyMap();
        }
        for (CourseSearchLoginVo row : rows) {
            String signedUrl = signedUrlMap.get(row.getId());
            if (StringUtils.isNotEmpty(signedUrl)) {
                row.setCover(signedUrl);
            }
        }
        return rows;
    }

    private void fillResourceTypeDesc(List<CourseSearchLoginVo> rows, Long userId) {
        for (CourseSearchLoginVo row : rows) {
            boolean needPurchasedDesc = CourseResourceEnum.CASH_ONLY.getCode().equals(row.getResourceType())
                    || CourseResourceEnum.CASH_POINT.getCode().equals(row.getResourceType());
            if (userId != null && needPurchasedDesc && Integer.valueOf(1).equals(row.getBuyFlag())) {
                row.setResourceTypeDesc("已购买");
                continue;
            }
            CourseResourceEnum resourceEnum = CourseResourceEnum.fromCode(row.getResourceType());
            row.setResourceTypeDesc(resourceEnum == null ? row.getResourceType() : resourceEnum.getDesc());
        }
    }

    private CourseSearchRequest buildCollectionSearchRequest(CourseSearchRequest request, int courseCount) {
        CourseSearchRequest collectionSearchRequest = new CourseSearchRequest();
        collectionSearchRequest.setTags(request.getTags());
        collectionSearchRequest.setKeyword(request.getKeyword());
        collectionSearchRequest.setResourceType(request.getResourceType());
        collectionSearchRequest.setCollectionFlag(request.getCollectionFlag());
        collectionSearchRequest.setPageNum(1);
        collectionSearchRequest.setPageSize(courseCount);
        return collectionSearchRequest;
    }

    private List<CourseSearchLoginVo> sortRowsByCollectedOrder(List<CourseSearchLoginVo> rows, List<Long> collectedCourseIds) {
        if (StringUtils.isEmpty(rows) || StringUtils.isEmpty(collectedCourseIds)) {
            return rows;
        }

        Map<Long, Integer> orderMap = new HashMap<>(collectedCourseIds.size());
        for (int i = 0; i < collectedCourseIds.size(); i++) {
            orderMap.put(collectedCourseIds.get(i), i);
        }

        rows.sort((left, right) -> {
            Integer leftOrder = orderMap.get(left.getId());
            Integer rightOrder = orderMap.get(right.getId());
            if (leftOrder == null && rightOrder == null) {
                return 0;
            }
            if (leftOrder == null) {
                return 1;
            }
            if (rightOrder == null) {
                return -1;
            }
            return Integer.compare(leftOrder, rightOrder);
        });
        return rows;
    }

    private PageResponse<CourseSearchLoginVo> buildPagedResponse(List<CourseSearchLoginVo> rows, int pageNum, int pageSize) {
        if (StringUtils.isEmpty(rows)) {
            return PageResponse.of(Collections.emptyList(), 0L, pageNum, pageSize);
        }

        int fromIndex = Math.max((pageNum - 1) * pageSize, 0);
        if (fromIndex >= rows.size()) {
            return PageResponse.of(Collections.emptyList(), rows.size(), pageNum, pageSize);
        }

        int toIndex = Math.min(fromIndex + pageSize, rows.size());
        List<CourseSearchLoginVo> pageRows = new ArrayList<>(rows.subList(fromIndex, toIndex));
        return PageResponse.of(pageRows, rows.size(), pageNum, pageSize);
    }

    private OshCourseEsDocument buildEsDocument(CourseSearchLoginVo row) {
        OshCourseEsDocument document = new OshCourseEsDocument();
        document.setId(row.getId());
        document.setTitle(row.getTitle());
        document.setIntro(row.getIntro());
        document.setServiceContent(row.getServiceContent());
        document.setCover(row.getCover());
        document.setPrice(row.getPrice());
        document.setTPrice(row.getTPrice());
        document.setType(row.getType());
        document.setSubCount(row.getSubCount());
        document.setRemark(row.getRemark());
        document.setCreateBy(row.getCreateBy());
        document.setUpdateBy(row.getUpdateBy());
        document.setTotalDuration(row.getTotalDuration());
        document.setFreeLessonCount(row.getFreeLessonCount());
        document.setVideoCount(row.getVideoCount());
        document.setSalesCount(row.getSalesCount());
        document.setViewCount(row.getViewCount());
        document.setLikeCount(row.getLikeCount());
        document.setCommentCount(row.getCommentCount());
        document.setQuestionCount(row.getQuestionCount());
        document.setCollectionCount(row.getCollectionCount());
        document.setRatingScore(row.getRatingScore());
        document.setFreeType(row.getFreeType());
        document.setAfterServiceDays(row.getAfterServiceDays());
        document.setResourceType(row.getResourceType());
        document.setLevel(row.getLevel());
        document.setStatus(row.getStatus());
        document.setExamId(row.getExamId());
        document.setDeleteFlag(0);
        document.setCreateTime(row.getCreateTime());
        document.setUpdateTime(row.getUpdateTime());

        List<String> tagNames = extractTagNames(row.getId());
        document.setTagNames(tagNames);
        String tagText = String.join(" ", tagNames);
        document.setTagNamesText(tagText);
        document.setSearchText(buildSearchText(row, tagText));
        return document;
    }

    private List<String> extractTagNames(Long courseId) {
        List<Map<String, Object>> tags = oshCourseTagMapper.selectTagsByCourseId(courseId);
        if (StringUtils.isEmpty(tags)) {
            return Collections.emptyList();
        }

        List<String> tagNames = new ArrayList<>(tags.size());
        for (Map<String, Object> tag : tags) {
            Object name = tag.get("name");
            if (name != null && StringUtils.isNotEmpty(String.valueOf(name))) {
                tagNames.add(String.valueOf(name));
            }
        }
        return tagNames;
    }

    private String buildSearchText(CourseSearchLoginVo row, String tagText) {
        StringBuilder builder = new StringBuilder();
        appendSearchField(builder, row.getTitle());
        appendSearchField(builder, row.getIntro());
        appendSearchField(builder, row.getServiceContent());
        appendSearchField(builder, tagText);
        return builder.toString().trim();
    }

    private void appendSearchField(StringBuilder builder, String value) {
        if (StringUtils.isEmpty(value)) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(value.trim());
    }
}
