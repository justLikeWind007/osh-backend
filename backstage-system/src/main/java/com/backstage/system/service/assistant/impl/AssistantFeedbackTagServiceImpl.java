package com.backstage.system.service.assistant.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.assistant.AssistantFeedbackTag;
import com.backstage.system.domain.assistant.AssistantFeedbackTagRel;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackTagCreateDTO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackTagVO;
import com.backstage.system.mapper.assistant.AssistantFeedbackTagMapper;
import com.backstage.system.mapper.assistant.AssistantFeedbackTagRelMapper;
import com.backstage.system.service.assistant.IAssistantFeedbackTagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 反馈标签服务实现
 *
 * @author backstage
 */
@Service
public class AssistantFeedbackTagServiceImpl extends ServiceImpl<AssistantFeedbackTagMapper, AssistantFeedbackTag>
        implements IAssistantFeedbackTagService {

    public AssistantFeedbackTagServiceImpl(AssistantFeedbackTagRelMapper feedbackTagRelMapper) {
        this.feedbackTagRelMapper = feedbackTagRelMapper;
    }

    private static final int MAX_FEEDBACK_TAG_COUNT = 3;
    private static final int DEFAULT_ENABLED_STATUS = 1;
    private static final int DEFAULT_SORT_ORDER = 0;
    private static final int DEFAULT_USE_COUNT = 0;

    private final AssistantFeedbackTagRelMapper feedbackTagRelMapper;

    @Override
    public List<AssistantFeedbackTagVO> listEnabledTags(String keyword) {
        return lambdaQuery()
                .eq(AssistantFeedbackTag::getIsEnabled, DEFAULT_ENABLED_STATUS)
                .eq(AssistantFeedbackTag::getDeleteFlag, (byte) 0)
                .and(StrUtil.isNotBlank(keyword), wrapper -> wrapper
                        .like(AssistantFeedbackTag::getName, keyword.trim())
                        .or()
                        .like(AssistantFeedbackTag::getCode, keyword.trim())
                        .or()
                        .like(AssistantFeedbackTag::getRemark, keyword.trim()))
                .orderByAsc(AssistantFeedbackTag::getSortOrder)
                .orderByDesc(AssistantFeedbackTag::getUseCount)
                .orderByAsc(AssistantFeedbackTag::getId)
                .list()
                .stream()
                .map(tag -> BeanUtil.copyProperties(tag, AssistantFeedbackTagVO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssistantFeedbackTagVO createTag(AssistantFeedbackTagCreateDTO dto, Long operatorId) {
        String tagName = dto.getName().trim();
        String tagCode = normalizeTagCode(dto.getCode(), tagName);
        validateUniqueTag(tagName, tagCode);
        AssistantFeedbackTag tag = buildTagForCreate(dto, operatorId, tagName, tagCode);
        save(tag);
        return BeanUtil.copyProperties(tag, AssistantFeedbackTagVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssistantFeedbackTagVO createOrGetTag(AssistantFeedbackTagCreateDTO dto, Long operatorId) {
        String tagName = dto.getName().trim();
        AssistantFeedbackTag existingTag = getTagByName(tagName);
        if (existingTag != null) {
            if (existingTag.getIsEnabled() == null || existingTag.getIsEnabled() != DEFAULT_ENABLED_STATUS) {
                throw new ServiceException("标签已禁用，不能选择");
            }
            return BeanUtil.copyProperties(existingTag, AssistantFeedbackTagVO.class);
        }

        String tagCode = normalizeTagCode(dto.getCode(), tagName);
        AssistantFeedbackTag tag = buildTagForCreate(dto, operatorId, tagName, tagCode);
        save(tag);
        return BeanUtil.copyProperties(tag, AssistantFeedbackTagVO.class);
    }

    @Override
    public List<Long> normalizeTagIds(List<Long> tagIds) {
        return normalizeTagIds(tagIds, true);
    }

    private List<Long> normalizeTagIds(List<Long> tagIds, boolean limitFeedbackTagCount) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> normalizedTagIds = tagIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (normalizedTagIds.isEmpty()) {
            return Collections.emptyList();
        }
        if (limitFeedbackTagCount && normalizedTagIds.size() > MAX_FEEDBACK_TAG_COUNT) {
            throw new ServiceException("最多选择3个标签");
        }
        List<AssistantFeedbackTag> enabledTags = lambdaQuery()
                .in(AssistantFeedbackTag::getId, normalizedTagIds)
                .eq(AssistantFeedbackTag::getIsEnabled, DEFAULT_ENABLED_STATUS)
                .eq(AssistantFeedbackTag::getDeleteFlag, (byte) 0)
                .list();
        if (enabledTags.size() != normalizedTagIds.size()) {
            throw new ServiceException("存在无效的反馈标签");
        }
        Map<Long, AssistantFeedbackTag> enabledTagMap = enabledTags.stream()
                .collect(Collectors.toMap(AssistantFeedbackTag::getId, Function.identity()));
        return normalizedTagIds.stream()
                .filter(enabledTagMap::containsKey)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindFeedbackTags(Long feedbackId, List<Long> tagIds, Long operatorId) {
        List<Long> normalizedTagIds = normalizeTagIds(tagIds);
        if (normalizedTagIds.isEmpty()) {
            return;
        }
        normalizedTagIds.stream()
                .map(tagId -> buildTagRelation(feedbackId, tagId, operatorId))
                .forEach(feedbackTagRelMapper::insert);
        normalizedTagIds.forEach(this::increaseUseCount);
    }

    @Override
    public Map<Long, List<AssistantFeedbackTagVO>> mapFeedbackTags(Set<Long> feedbackIds) {
        if (feedbackIds == null || feedbackIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<AssistantFeedbackTagRel> relations = feedbackTagRelMapper.selectList(new LambdaQueryWrapper<AssistantFeedbackTagRel>()
                .in(AssistantFeedbackTagRel::getFeedbackId, feedbackIds)
                .eq(AssistantFeedbackTagRel::getDeleteFlag, (byte) 0));
        if (relations == null || relations.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> tagIds = relations.stream()
                .map(AssistantFeedbackTagRel::getTagId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (tagIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, AssistantFeedbackTagVO> tagMap = lambdaQuery()
                .in(AssistantFeedbackTag::getId, tagIds)
                .eq(AssistantFeedbackTag::getDeleteFlag, (byte) 0)
                .list()
                .stream()
                .map(tag -> BeanUtil.copyProperties(tag, AssistantFeedbackTagVO.class))
                .collect(Collectors.toMap(AssistantFeedbackTagVO::getId, Function.identity()));
        Map<Long, List<AssistantFeedbackTagVO>> feedbackTagMap = new LinkedHashMap<>();
        relations.stream()
                .filter(rel -> tagMap.containsKey(rel.getTagId()))
                .forEach(rel -> feedbackTagMap.computeIfAbsent(rel.getFeedbackId(), key -> new ArrayList<>()).add(tagMap.get(rel.getTagId())));
        return feedbackTagMap;
    }

    @Override
    public Set<Long> listFeedbackIdsByTagIds(List<Long> tagIds) {
        List<Long> normalizedTagIds = normalizeTagIds(tagIds, false);
        if (normalizedTagIds.isEmpty()) {
            return Collections.emptySet();
        }
        return feedbackTagRelMapper.selectList(new LambdaQueryWrapper<AssistantFeedbackTagRel>()
                        .in(AssistantFeedbackTagRel::getTagId, normalizedTagIds)
                        .eq(AssistantFeedbackTagRel::getDeleteFlag, (byte) 0))
                .stream()
                .map(AssistantFeedbackTagRel::getFeedbackId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private AssistantFeedbackTagRel buildTagRelation(Long feedbackId, Long tagId, Long operatorId) {
        AssistantFeedbackTagRel relation = new AssistantFeedbackTagRel();
        relation.setFeedbackId(feedbackId);
        relation.setTagId(tagId);
        relation.setDeleted(false);
        relation.setCreateBy(operatorId);
        relation.setUpdateBy(operatorId);
        return relation;
    }

    private void increaseUseCount(Long tagId) {
        AssistantFeedbackTag tag = getById(tagId);
        if (tag == null) {
            return;
        }
        Integer currentUseCount = tag.getUseCount() == null ? 0 : tag.getUseCount();
        lambdaUpdate()
                .eq(AssistantFeedbackTag::getId, tagId)
                .set(AssistantFeedbackTag::getUseCount, currentUseCount + 1)
                .update();
    }

    private void validateUniqueTag(String tagName, String tagCode) {
        Long sameNameCount = getTagCountByName(tagName);
        if (sameNameCount > 0) {
            throw new ServiceException("标签名称已存在");
        }
        Long sameCodeCount = lambdaQuery()
                .eq(AssistantFeedbackTag::getCode, tagCode)
                .eq(AssistantFeedbackTag::getDeleteFlag, (byte) 0)
                .count();
        if (sameCodeCount > 0) {
            throw new ServiceException("标签编码已存在");
        }
    }

    private AssistantFeedbackTag buildTagForCreate(AssistantFeedbackTagCreateDTO dto, Long operatorId, String tagName, String tagCode) {
        AssistantFeedbackTag tag = new AssistantFeedbackTag();
        tag.setName(tagName);
        tag.setCode(tagCode);
        tag.setSortOrder(dto.getSortOrder() == null ? DEFAULT_SORT_ORDER : dto.getSortOrder());
        tag.setUseCount(DEFAULT_USE_COUNT);
        tag.setIsEnabled(dto.getIsEnabled() == null ? DEFAULT_ENABLED_STATUS : dto.getIsEnabled());
        tag.setRemark(StrUtil.isNotBlank(dto.getRemark()) ? dto.getRemark().trim() : null);
        tag.setDeleted(false);
        tag.setCreateBy(operatorId);
        tag.setUpdateBy(operatorId);
        return tag;
    }

    private AssistantFeedbackTag getTagByName(String tagName) {
        return lambdaQuery()
                .eq(AssistantFeedbackTag::getName, tagName)
                .eq(AssistantFeedbackTag::getDeleteFlag, (byte) 0)
                .last("limit 1")
                .one();
    }

    private Long getTagCountByName(String tagName) {
        return lambdaQuery()
                .eq(AssistantFeedbackTag::getName, tagName)
                .eq(AssistantFeedbackTag::getDeleteFlag, (byte) 0)
                .count();
    }

    private String normalizeTagCode(String code, String tagName) {
        if (StrUtil.isNotBlank(code)) {
            return code.trim();
        }
        String normalizedName = tagName.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-+|-+$)", "");
        if (StrUtil.isNotBlank(normalizedName)) {
            return normalizedName;
        }
        return "feedback-tag-" + System.currentTimeMillis() + "-" + Math.abs(tagName.hashCode());
    }
}
