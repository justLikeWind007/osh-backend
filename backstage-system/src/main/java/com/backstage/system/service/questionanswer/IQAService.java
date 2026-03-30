package com.backstage.system.service.questionanswer;

import com.backstage.common.core.domain.R;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:50
 */
public interface IQAService {

    R<String> addQuestionAnswer(Long userId, Long resourceNo, String resourceType, String title, String content, Integer isPaidOnly, List<Long> tags);
}
