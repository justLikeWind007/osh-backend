package com.backstage.system.service.questionanswer;

import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.system.domain.questionanswer.vo.QueryQuestionDetailVO;
import com.backstage.system.domain.user.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:50
 */
public interface IOshQAQuestionService {

    R<String> addQuestion(User user, Long resourceNo, String resourceType, String content, Byte isPaidOnly, List<Long> tags, Byte status);

    R<String> publishQuestion(User user, Long questionId);

    R<String> deleteQuestion(User user, Long questionId);

    R<String> followQuestion(User user, Long questionId);

    R<String> cancelFollowQuestion(User user, Long questionId);

    TableDataInfo list(Long userId, Long resourceNo, String resourceType, String type, String keyword, Integer pageNum, Integer pageSize);

    R<String> solve(User user, Long questionId, Long answerId);

    R<String> cancelSolve(User user, Long questionId, Long answerId);

    R<QueryQuestionDetailVO> detail(Long id, Long questionId);

    R<String> vote(Long id, Long answerId);

    R<String> cancelVote(Long id, Long answerId);
}
