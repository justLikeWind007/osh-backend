package com.backstage.system.service.impl.exam;

import com.alibaba.fastjson2.JSON;
import com.backstage.system.domain.vo.exam.ExamDetailVo;
import com.backstage.system.domain.vo.exam.ExamVo;
import com.backstage.system.domain.vo.exam.QuestionVo;
import com.backstage.system.mapper.exam.OshExamMapper;
import com.backstage.system.service.exam.IOshExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OshExamServiceImpl implements IOshExamService {

    @Autowired
    private OshExamMapper examMapper;

    @Override
    public List<ExamVo> selectExamList() {
        return examMapper.selectExamList();
    }

    @Override
    public ExamDetailVo selectExamById(Long id) {
        // 1. 直接从数据库查（MyBatis 已经通过 collection 把 5 条题封装在 detail 里的 list 中了）
        ExamDetailVo detail = examMapper.selectExamById(id);

        if (detail != null && detail.getTestpaper_questions() != null) {
            // 2. 遍历数据库查出来的题目，处理一下选项格式
            for (QuestionVo q : detail.getTestpaper_questions()) {

                // 处理选项：数据库存的是 JSON 字符串 '["正确","错误"]'，转成数组给前端
                if (q.getOptions() != null && q.getOptions() instanceof String) {
                    String optStr = (String) q.getOptions();
                    // 使用 Fastjson 转换（如果没有 Fastjson 可以换成 Jackson）
                    q.setOptions(JSON.parseArray(optStr, String.class));
                }

                // 初始化 user_value：给前端一个默认值，防止答题时报错
                if ("checkbox".equals(q.getType()) || "answer".equals(q.getType()) || "completion".equals(q.getType())) {
                    q.setUser_value(new String[]{""}); // 多选、简答、填空给数组
                } else if ("trueOrfalse".equals(q.getType()) || "radio".equals(q.getType())) {
                    q.setUser_value(-1); // 单选、判断给 -1
                }
            }

            // 3. 模拟考试记录 ID（后续需要从插入 user_test 表获取）
            detail.setUser_test_id(7L);
        }

        return detail;
    }
}