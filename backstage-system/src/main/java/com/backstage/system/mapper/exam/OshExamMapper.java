package com.backstage.system.mapper.exam;

import com.backstage.system.domain.vo.exam.ExamDetailVo;
import com.backstage.system.domain.vo.exam.ExamVo;
import java.util.List;

public interface OshExamMapper {
    /**
     * 查询考场列表
     */
    public List<ExamVo> selectExamList();
    public ExamDetailVo selectExamById(Long id);
}