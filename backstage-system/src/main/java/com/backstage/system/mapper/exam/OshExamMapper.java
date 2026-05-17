package com.backstage.system.mapper.exam;

import com.backstage.system.domain.exam.OshExamination;
import com.backstage.system.domain.dto.exam.ExamSearchDto;
import com.backstage.system.domain.vo.exam.ExamDetailVo;
import com.backstage.system.domain.vo.exam.ExamVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OshExamMapper extends BaseMapper<OshExamination> {

    /** 考试列表（支持关键词/标签/资源过滤） */
    List<ExamVo> selectExamList(@Param("dto") ExamSearchDto dto);

    /** 考试详情（含题目） */
    ExamDetailVo selectExamById(@Param("id") Long id);

    /** 查询用户已完成的考试ID列表 */
    List<Long> selectFinishedExamIdsByUserId(@Param("userId") Long userId);

    /** 软删除考试 */
    int deleteExamById(@Param("id") Long id, @Param("operator") String operator);

    /** 按题目表重算试卷题量与总分 */
    int updateExamQuestionStats(@Param("examId") Long examId);
}
