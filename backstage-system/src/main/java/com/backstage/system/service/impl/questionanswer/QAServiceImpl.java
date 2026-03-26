package com.backstage.system.service.impl.questionanswer;

import com.backstage.common.core.domain.R;
import com.backstage.system.service.questionanswer.IQAService;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:50
 */
@Service
public class QAServiceImpl implements IQAService {
    @Override
    public R<String> checkQuestionPermission(String token, String resourceType, String resourceId) {
        return null;
    }
}
