package com.backstage.system.mapper.questionanswer;

import com.backstage.system.domain.questionanswer.Answer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/4/2
 * Time: 20:06
 */
public interface OshQAAnswerMapper extends BaseMapper<Answer> {

    Integer getVoteInfoByUserIdAndAnswerId(@Param("userId")Long userId, @Param("answerId") Long answerId);

    int voteAnswer(@Param("userId")Long userId, @Param("answerId") Long answerId, @Param("createBy") Long createBy);

    int cancelVoteAnswer(@Param("userId")Long userId, @Param("answerId") Long answerId, @Param("updateBy") Long updateBy);
}
