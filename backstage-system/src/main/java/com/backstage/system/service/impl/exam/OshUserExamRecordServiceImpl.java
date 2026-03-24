package com.backstage.system.service.impl.exam;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.backstage.common.exception.ServiceException;
import com.backstage.common.utils.SecurityUtils;
import com.backstage.common.utils.aijudge.AiUtil;
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

import java.util.ArrayList;
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
        Long examId = saveDto.getExam_id(); // 刚才我们在 DTO 里补全的字段
        Long userId = saveDto.getUser_test_id();
        OshUserExamRecord record = userTestMapper.selectByUserIdAndExamId(userId, examId);

        if (record == null) {
            // 如果不存在（即你说的 ID 为 7 但表里没数据的情况），手动创建一个存进去
            record = new OshUserExamRecord();
            record.setUser_id(saveDto.getUser_test_id());                    // 实际应从 SecurityUtils 获取
            record.setId(saveDto.getUser_test_id());                  // 此处写死为你确定的试卷 ID 12，或者从别处获取
            record.setAnswer_status(0);              // 初始为进行中
            record.setRead_status(0);                // 初始为未批阅
            record.setScore(0);
            record.setExam_id(examId);

            // 执行插入
            userTestMapper.insertOshUserExam(record);
        }
        List<QuestionVo> questions = userTestMapper.selectQuestionsByExamId(record.getExam_id());
        List<Object> userAnswers = saveDto.getValue();

        if (questions == null || userAnswers == null || questions.size() != userAnswers.size()) {
            throw new ServiceException("提交的答案数量与题目总数不匹配");
        }

        int finalScore = 0;
        // 用于存储简答题，发给 AI
        StringBuilder essayPromptBuilder = new StringBuilder();
        List<Integer> essayIndexList = new ArrayList<>(); // 记录简答题在原列表的索引，方便回填

        // 1. 自动批阅逻辑（客观题）
        for (int i = 0; i < questions.size(); i++) {
            QuestionVo q = questions.get(i);
            Object uAns = userAnswers.get(i);
            String correct = q.getCorrect_answer() != null ? String.valueOf(q.getCorrect_answer()) : "";

            // 【AI 介入点】如果是简答题 (answer)
            if ("answer".equals(q.getType())) {
                essayIndexList.add(i);
                essayPromptBuilder.append(String.format("【题目 %d】: %s\n【参考答案】: %s\n【用户回答】: %s\n【满分】: %d\n\n",
                        i + 1, q.getTitle(), correct, uAns, q.getScore()));
                continue;
            }

            // 客观题比对逻辑（保持你原来的不变）
            if (uAns != null) {
                String userAnsStr = JSON.toJSONString(uAns).replace(" ", "");
                String correctAnsStr = correct.trim().replace(" ", "");
                if (userAnsStr.equals(correctAnsStr)) {
                    finalScore += q.getScore();
                }
            }
        }

        // 2. 调用 AI 评分（处理简答题）
        if (!essayIndexList.isEmpty()) {
            String aiResponse = callAiForScoring(essayPromptBuilder.toString());
            if (aiResponse != null) {
                // 解析 AI 返回的 JSON，例如 {"scores": [15, 20], "evaluation": "..."}
                JSONObject aiResult = JSON.parseObject(aiResponse);
                JSONArray scoresArray = aiResult.getJSONArray("scores");

                if (scoresArray != null && scoresArray.size() == essayIndexList.size()) {
                    for (int j = 0; j < scoresArray.size(); j++) {
                        finalScore += scoresArray.getIntValue(j);
                    }
                }
            }
            // 注意：由于 AI 已经实时评分，read_status 可以直接设为 1（已批阅）
        }

        // 3. 构造更新对象
        record.setScore(finalScore);
        record.setAnswer_status(1);
        record.setRead_status(1); // AI 评完了，直接标记为已批阅
        record.setAnswer_json(JSON.toJSONString(userAnswers));

        return userTestMapper.updateOshUserExam(record) > 0;
    }

    @Autowired
    private AiUtil aiUtil = new AiUtil();
    /**
     * 封装业务 Prompt 并调用通用 AiUtil
     */
    private String callAiForScoring(String essayContent) {
        // 1. 构造业务指令
        String fullPrompt = "你是一位专业的数字人面试官。请针对以下简答题，结合参考答案和用户回答进行打分。改成你是个严格老师 每一分绝不乱给 不要轻易给满分 要结合参考答案 并且带上自己的判断 参考答案只是参考的\n" +
                "要求：\n" +
                "1. 评分要严谨，如果回答不着边际给0分。\n" +
                "2. 必须严格以JSON格式输出，不要包含任何解释文字。\n" +
                "3. 格式如下：{\"scores\": [分数1, 分数2], \"evaluation\": \"整体评价\"}\n\n" +
                "简答题内容如下：\n" + essayContent;

        // 2. 调用 common 模块中的 AiUtil (这里建议把 URL 和 KEY 放在配置文件里)
        String apiUrl = "https://api.deepseek.com/chat/completions";
        String apiKey = "sk-3ad11ee76b0e4e3f99f618b3344ccc6a";
        String model = "deepseek-chat";

        // 调用你刚才封装在 common 里的工具类
        return aiUtil.sendRequest(apiUrl, apiKey, model, fullPrompt);
    }
}