package com.backstage.system.mapper.questionanswer;

import com.backstage.system.domain.questionanswer.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/1
 * Time: 15:39
 */
public interface OshQATagMapper extends BaseMapper<Tag> {
    List<Long> selectTagIdsByQuestionId(Long questionId);

    Tag selectTagByName(String name);
}
