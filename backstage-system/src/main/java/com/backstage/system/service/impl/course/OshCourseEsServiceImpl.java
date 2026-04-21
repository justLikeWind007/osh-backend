package com.backstage.system.service.impl.course;

import com.backstage.common.response.PageResponse;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.course.es.OshCourseEsDocument;
import com.backstage.system.domain.course.vo.CourseSearchLoginVo;
import com.backstage.system.enums.CourseResourceEnum;
import com.backstage.system.mapper.course.OshCourseEsMapper;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OshCourseEsServiceImpl implements IOshCourseEsService {

    private static final Logger log = LoggerFactory.getLogger(OshCourseEsServiceImpl.class);

    @Autowired
    private OshCourseEsMapper oshCourseEsMapper;

    @Autowired
    private OshCourseMapper oshCourseMapper;

    @Autowired
    private OshCourseTagMapper oshCourseTagMapper;

    @Autowired
    private ICourseManageService courseManageService;

    @Override
    public PageResponse<CourseSearchLoginVo> searchCourses(CourseSearchRequest request, Long userId) {
        PageResponse<CourseSearchLoginVo> pageResponse;
        try {
            pageResponse = oshCourseEsMapper.searchCourses(request);
        } catch (Exception ex) {
            log.error("search courses from es failed, request={}, userId={}", request, userId, ex);
            throw new IllegalStateException("search courses from es failed", ex);
        }

        List<CourseSearchLoginVo> rows = pageResponse.getRows();
        if (StringUtils.isEmpty(rows)) {
            return pageResponse;
        }

        fillBuyFlag(rows, userId);
        fillResourceTypeDesc(rows, userId);
        convertToExpiryUrls(rows);
        return pageResponse;
    }

    @Override
    public int syncAllCoursesToEs() {
        int pageNum = 1;
        int pageSize = 200;
        int total = 0;

        try {
            oshCourseEsMapper.deleteAllCourses();
        } catch (Exception ex) {
            throw new IllegalStateException("clear courses in es failed", ex);
        }

        while (true) {
            CourseSearchRequest request = new CourseSearchRequest();
            request.setPageNum(pageNum);
            request.setPageSize(pageSize);

            PageHelper.startPage(pageNum, pageSize);
            List<CourseSearchLoginVo> rows = oshCourseMapper.pageQuerySearchCourse(request);
            if (StringUtils.isEmpty(rows)) {
                break;
            }

            List<OshCourseEsDocument> documents = new ArrayList<>(rows.size());
            for (CourseSearchLoginVo row : rows) {
                documents.add(buildEsDocument(row));
            }

            try {
                total += oshCourseEsMapper.bulkUpsertCourses(documents);
            } catch (Exception ex) {
                throw new IllegalStateException("sync courses to es failed", ex);
            }

            if (rows.size() < pageSize) {
                break;
            }
            pageNum++;
        }

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
        java.util.Set<Long> boughtCourseIdSet = new java.util.HashSet<>(boughtCourseIds);

        for (CourseSearchLoginVo row : rows) {
            if (boughtCourseIdSet.contains(row.getId())) {
                row.setBuyFlag(1);
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
