package com.backstage.system.service.exam;

import com.backstage.common.core.domain.R;
import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.dto.exam.ExamQuestionSaveDto;
import com.backstage.system.domain.dto.exam.ExamSaveDto;
import com.backstage.system.domain.dto.exam.ExamSearchDto;
import com.backstage.system.domain.exam.OshExamTag;
import com.backstage.system.domain.vo.exam.ExamDetailVo;
import com.backstage.system.domain.vo.exam.ExamVo;

import java.util.List;

public interface IOshExamService {

    /** 考试列表（分页，支持搜索/标签/资源过滤，回填 is_test/is_collected） */
    PageResponse<ExamVo> searchExams(ExamSearchDto dto, Long currentUserId);

    /** 考试详情（含题目，创建考试记录并返回 user_test_id） */
    ExamDetailVo getExamDetail(Long examId, Long currentUserId);

    /** 新增/修改考试 */
    R<Long> saveExam(ExamSaveDto dto, String operator);

    /** 删除考试（软删除） */
    R<String> deleteExam(Long examId, String operator);

    /** 收藏/取消收藏 */
    R<String> toggleCollect(Long examId, Long userId, String operator);

    /** 获取所有启用标签 */
    List<OshExamTag> getTagList();

    /** 新增或修改一道考试题目 */
    R<Long> saveExamQuestion(ExamQuestionSaveDto dto, String operator);

    /** 软删除一道考试题目 */
    R<String> deleteExamQuestion(Long questionId, Long examId, String operator);
}
