package com.backstage.system.mapper.questionanswer;

import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/24
 * Time: 21:51
 */
@Mapper
public interface QAMapper {
    int addQuestionAnswer(Long userId, Long resourceNo, String resourceType, String title, String content, Integer isPaidOnly);

    int addQuestionTags(Long tag);
}
