package com.backstage.system.service.impl.questionanswer;

import com.backstage.common.core.domain.R;
import com.backstage.system.mapper.questionanswer.QAMapper;
import com.backstage.system.service.questionanswer.IQAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:50
 */
@Service
public class QAServiceImpl implements IQAService {

    @Autowired
    private QAMapper qaMapper;

    @Override
    public R<String> addQuestionAnswer(Long userId, Long resourceNo, String resourceType, String title, String content, Integer isPaidOnly, List<Long> tags) {
        // todo 用户权限校验
        checkUserPermission(userId, resourceNo);
        qaMapper.addQuestionAnswer(userId, resourceNo, resourceType, title, content, isPaidOnly);
        for (Long tag : tags) {
            qaMapper.addQuestionTags(tag);
        }
        return R.ok("success");
    }

    private void checkUserPermission(Long userId, Long resourceNo) {
        // todo 用户权限校验
    }
}
