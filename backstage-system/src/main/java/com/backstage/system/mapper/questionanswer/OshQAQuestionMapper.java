package com.backstage.system.mapper.questionanswer;

import com.backstage.system.domain.questionanswer.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:51
 */
public interface OshQAQuestionMapper extends BaseMapper <Question> {

    int addQuestionTags(@Param("questionId") Long questionId,@Param("tagId") Long tagId);

    int followQuestion(@Param("userId")Long userId,@Param("questionId") Long questionId,@Param("createBy") String createBy);

    int cancelFollowQuestion(@Param("userId")Long userId,@Param("questionId") Long questionId,@Param("updateBy") String updateBy);

    List<Long> getFollowQuestionIds(@Param("userId")Long userId);
}
