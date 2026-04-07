package com.backstage.system.service.questionanswer;

import com.backstage.common.core.domain.R;
import com.backstage.system.domain.user.User;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/2
 * Time: 19:59
 */
public interface IOshQAAnswerService {
    R<String> answer(User user, Long questionId, String content);
}
