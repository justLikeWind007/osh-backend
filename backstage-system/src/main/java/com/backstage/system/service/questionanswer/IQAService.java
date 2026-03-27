package com.backstage.system.service.questionanswer;

import com.backstage.common.core.domain.R;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:50
 */
public interface IQAService {
    R<String> checkQuestionPermission(String token, String resourceType, String resourceId);
}
