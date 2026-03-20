package com.backstage.system.service.exam;

import com.backstage.system.domain.vo.exam.ExamDetailVo;
import com.backstage.system.domain.vo.exam.ExamVo;
import java.util.List;

public interface IOshExamService {
    public List<ExamVo> selectExamList();

    ExamDetailVo selectExamById(Long id);


}