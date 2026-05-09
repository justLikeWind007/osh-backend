package com.backstage.system.mapper.exam;

import com.backstage.system.domain.exam.OshExamQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OshExamQuestionMapper {

    int insertQuestion(OshExamQuestion row);

    int updateQuestion(OshExamQuestion row);

    int softDeleteQuestion(@Param("id") Long id, @Param("examId") Long examId, @Param("operator") String operator);

    OshExamQuestion selectByIdAndExamId(@Param("id") Long id, @Param("examId") Long examId);
}
