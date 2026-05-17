package com.backstage.system.service.assistant.impl;

import cn.hutool.core.bean.BeanUtil;
import com.backstage.system.domain.assistant.AssistantFeedbackCategory;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackCategoryVO;
import com.backstage.system.mapper.assistant.AssistantFeedbackCategoryMapper;
import com.backstage.system.service.assistant.IAssistantFeedbackCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 助手反馈分类 Service 实现
 *
 * @author backstage
 */
@Service
public class AssistantFeedbackCategoryServiceImpl 
        extends ServiceImpl<AssistantFeedbackCategoryMapper, AssistantFeedbackCategory>
        implements IAssistantFeedbackCategoryService {

    @Override
    public List<AssistantFeedbackCategoryVO> listUserCategories() {
        return lambdaQuery()
                .eq(AssistantFeedbackCategory::getIsEnabled, 1)
                .eq(AssistantFeedbackCategory::getIsAdminOnly, 0)
                .orderByAsc(AssistantFeedbackCategory::getSortOrder)
                .list()
                .stream()
                .map(category -> BeanUtil.copyProperties(category, AssistantFeedbackCategoryVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AssistantFeedbackCategoryVO> listAllCategories() {
        return lambdaQuery()
                .eq(AssistantFeedbackCategory::getIsEnabled, 1)
                .orderByAsc(AssistantFeedbackCategory::getSortOrder)
                .list()
                .stream()
                .map(category -> BeanUtil.copyProperties(category, AssistantFeedbackCategoryVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AssistantFeedbackCategory getCategoryByCode(String code) {
        return lambdaQuery()
                .eq(AssistantFeedbackCategory::getCode, code)
                .eq(AssistantFeedbackCategory::getIsEnabled, 1)
                .one();
    }

    @Override
    public boolean isCommentAllowed(Long categoryId) {
        AssistantFeedbackCategory category = getById(categoryId);
        return category != null && category.getAllowComment() == 1;
    }

    @Override
    public boolean isAdminOnly(Long categoryId) {
        AssistantFeedbackCategory category = getById(categoryId);
        return category != null && category.getIsAdminOnly() == 1;
    }
}
