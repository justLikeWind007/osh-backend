package com.backstage.system.service.assistant;

import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.assistant.AssistantFeedback;
import com.backstage.system.domain.assistant.dto.AssistantFeedbackPageDTO;

public interface IAssistantFeedbackEsService {

    PageResponse<AssistantFeedback> searchFeedbacks(AssistantFeedbackPageDTO dto);

    int syncAllFeedbacksToEs();

    void upsertFeedbackById(Long feedbackId);

    void deleteFeedbackById(Long feedbackId);
}
