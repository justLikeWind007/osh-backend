package com.backstage.system.mapper.exam;

import com.backstage.system.domain.exam.OshExamTag;
import com.backstage.system.domain.exam.OshExamTagRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OshExamTagMapper extends BaseMapper<OshExamTag> {

    /** 根据标签名查询标签 */
    OshExamTag selectTagByName(@Param("name") String name);

    /** 查询考试的所有标签名 */
    List<String> selectTagNamesByExamId(@Param("examId") Long examId);

    /** 删除考试的所有标签关联 */
    void deleteTagRelByExamId(@Param("examId") Long examId);

    /** 插入标签关联 */
    void insertTagRel(OshExamTagRel rel);

    /** 查询所有启用标签（按 use_count 降序） */
    List<OshExamTag> selectAllActiveTags();
}
