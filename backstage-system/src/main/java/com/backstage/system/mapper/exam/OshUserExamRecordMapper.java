package com.backstage.system.mapper.exam;

import com.backstage.system.domain.exam.OshUserExamRecord;
import com.backstage.system.domain.vo.exam.QuestionVo;
import com.backstage.system.domain.vo.exam.UserExamRecordVo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface OshUserExamRecordMapper {
    /**
     * 查询用户考试记录列表
     */
    List<UserExamRecordVo> selectUserTestList(@Param("userId") Long userId);

    List<QuestionVo> selectQuestionsByExamId(Long userExamId);

    int updateOshUserExam(OshUserExamRecord record);
}