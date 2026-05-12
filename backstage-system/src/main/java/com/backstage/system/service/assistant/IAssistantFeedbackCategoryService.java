package com.backstage.system.service.assistant;

import com.backstage.system.domain.assistant.AssistantFeedbackCategory;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackCategoryVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * AI 助手反馈分类 Service 接口
 *
 * @author backstage
 */
public interface IAssistantFeedbackCategoryService extends IService<AssistantFeedbackCategory> {

    /**
     * 获取用户可见的分类列表（排除仅管理员可用的分类）
     *
     * @return 分类列表
     */
    List<AssistantFeedbackCategoryVO> listUserCategories();

    /**
     * 获取所有启用的分类列表（包含管理员分类）
     *
     * @return 分类列表
     */
    List<AssistantFeedbackCategoryVO> listAllCategories();

    /**
     * 根据分类代码获取分类信息
     *
     * @param code 分类代码
     * @return 分类信息
     */
    AssistantFeedbackCategory getCategoryByCode(String code);

    /**
     * 检查分类是否允许评论
     *
     * @param categoryId 分类 ID
     * @return true-允许 false-不允许
     */
    boolean isCommentAllowed(Long categoryId);

    /**
     * 检查分类是否仅管理员可用
     *
     * @param categoryId 分类 ID
     * @return true-仅管理员 false-所有用户
     */
    boolean isAdminOnly(Long categoryId);
}
