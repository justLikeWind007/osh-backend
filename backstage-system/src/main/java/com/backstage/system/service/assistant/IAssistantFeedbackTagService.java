package com.backstage.system.service.assistant;

import com.backstage.system.domain.assistant.AssistantFeedbackTag;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackTagCreateDTO;
import com.backstage.system.domain.assistant.vo.AssistantFeedbackTagVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 反馈标签服务接口
 *
 * @author backstage
 */
public interface IAssistantFeedbackTagService extends IService<AssistantFeedbackTag> {

    /**
     * 查询启用中的反馈标签列表
     *
     * @return 标签列表
     */
    List<AssistantFeedbackTagVO> listEnabledTags(String keyword);

    /**
     * 创建反馈标签
     *
     * @param dto 标签创建参数
     * @param operatorId 操作人 ID
     * @return 标签信息
     */
    AssistantFeedbackTagVO createTag(AssistantFeedbackTagCreateDTO dto, Long operatorId);

    /**
     * 创建或复用反馈标签
     *
     * @param dto 标签创建参数
     * @param operatorId 操作人 ID
     * @return 标签信息
     */
    AssistantFeedbackTagVO createOrGetTag(AssistantFeedbackTagCreateDTO dto, Long operatorId);

    /**
     * 校验并规范化标签 ID 集合
     *
     * @param tagIds 标签 ID 列表
     * @return 规范化后的标签 ID 列表
     */
    List<Long> normalizeTagIds(List<Long> tagIds);

    /**
     * 绑定反馈标签
     *
     * @param feedbackId 反馈 ID
     * @param tagIds 标签 ID 列表
     * @param operatorId 操作人 ID
     */
    void bindFeedbackTags(Long feedbackId, List<Long> tagIds, Long operatorId);

    /**
     * 查询反馈标签映射
     *
     * @param feedbackIds 反馈 ID 集合
     * @return 反馈标签映射
     */
    Map<Long, List<AssistantFeedbackTagVO>> mapFeedbackTags(Set<Long> feedbackIds);

    /**
     * 根据标签筛选反馈 ID
     *
     * @param tagIds 标签 ID 列表
     * @return 反馈 ID 集合
     */
    Set<Long> listFeedbackIdsByTagIds(List<Long> tagIds);
}
