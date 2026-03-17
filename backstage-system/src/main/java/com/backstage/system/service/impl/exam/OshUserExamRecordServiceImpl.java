package com.backstage.system.service.impl.exam;

import com.alibaba.fastjson2.JSON;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.dto.exam.UserExamSaveDto;
import com.backstage.system.domain.exam.OshUserExamRecord;
import com.backstage.system.domain.vo.exam.QuestionVo;
import com.backstage.system.domain.vo.exam.UserExamRecordVo;
import com.backstage.system.mapper.exam.OshUserExamRecordMapper;
import com.backstage.system.service.exam.IOshUserExamRecordService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OshUserExamRecordServiceImpl implements IOshUserExamRecordService {
    @Autowired
    private OshUserExamRecordMapper userTestMapper;

    @Override
    public List<UserExamRecordVo> selectUserTestList(Integer page) {
        // 实际开发中 userId 应从 SecurityUtils 获取，此处写死为 1 进行演示
        Long userId = 1L; 
        
        // 开启分页（配合 PageHelper）
        PageHelper.startPage(page, 10);
        return userTestMapper.selectUserTestList(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveUserExam(UserExamSaveDto saveDto) {
        // 1. 获取这套试卷的所有题目
        // 注意：XML里的SQL一定要用 ORDER BY q.id ASC，确保与前端提交的数组顺序一一对应
        List<QuestionVo> questions = userTestMapper.selectQuestionsByExamId(saveDto.getUser_test_id());
        List<Object> userAnswers = saveDto.getValue();

        // 严谨性检查：防止数组越界
        if (questions == null || userAnswers == null || questions.size() != userAnswers.size()) {
            throw new ServiceException("提交的答案数量与题目总数不匹配，请重新提交");
        }

        int finalScore = 0;
        int isNeedManualRead = 0; // 是否包含简答题标记

        // 2. 自动批阅逻辑
        for (int i = 0; i < questions.size(); i++) {
            QuestionVo q = questions.get(i);
            Object uAns = userAnswers.get(i); // 获取用户针对当前题目的答案

            // 【关键补全】从 VO 中获取正确答案，并转为 String 方便比对
            // 因为你的 correct_answer 是 Object，这里用 String.valueOf 保证不为 null
            String correct = q.getCorrect_answer() != null ? String.valueOf(q.getCorrect_answer()) : "";

            // 如果是简答题 (answer)
            if ("answer".equals(q.getType())) {
                isNeedManualRead = 1; // 标记需要人工介入
                continue; // 跳过自动算分
            }

            // 处理客观题比对（单选、多选、判断、填空）
            boolean isCorrect = false;
            if (uAns != null) {
                // 技巧：统统转成 JSON 字符串去掉空格比对，可以兼容数组、数字和字符串
                // 例如：前端传 [0,1]，转成字符串是 "[0,1]"，与数据库存的 "[0,1]" 完美匹配
                String userAnsStr = JSON.toJSONString(uAns).replace(" ", "");
                String correctAnsStr = correct.trim().replace(" ", "");

                if (userAnsStr.equals(correctAnsStr)) {
                    isCorrect = true;
                }
            }

            if (isCorrect) {
                // 如果对了，累加该题的分值
                finalScore += q.getScore();
            }
        }
        if (questions == null || userAnswers == null || questions.size() != userAnswers.size()) {
            // 加上这行打印，你就知道数据库到底查出来几道题了
            System.out.println("数据库题目数: " + (questions != null ? questions.size() : 0));
            System.out.println("前端提交答案数: " + (userAnswers != null ? userAnswers.size() : 0));

            throw new ServiceException("提交的答案数量(" + userAnswers.size() + ")与题目总数(" + questions.size() + ")不匹配");
        }

        // 3. 构造更新对象（更新数据库 osh_user_exam_record 表）
        OshUserExamRecord record = new OshUserExamRecord();
        record.setId(saveDto.getUser_test_id());
        record.setScore(finalScore);
        record.setAnswer_status(1); // 1-已交卷

        // 如果包含简答题，read_status 设为 0（未批改），否则设为 1（系统已自动批改完成）
        record.setRead_status(isNeedManualRead == 1 ? 0 : 1);

        // 将用户提交的原始答案数组转成字符串存入数据库，方便以后“查看试卷”时回显
        record.setAnswer_json(JSON.toJSONString(userAnswers));

        // 执行更新操作
        return userTestMapper.updateOshUserExam(record) > 0;
    }
}