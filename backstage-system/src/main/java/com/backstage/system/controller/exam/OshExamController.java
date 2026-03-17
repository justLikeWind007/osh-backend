package com.backstage.system.controller.exam;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.vo.exam.ExamDetailVo;
import com.backstage.system.domain.vo.exam.ExamVo;
import com.backstage.system.service.exam.IOshExamService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pc/testpaper")
public class OshExamController extends BaseController {

    @Autowired
    private IOshExamService examService;

    /**
     * 获取考场列表
     */
    @GetMapping("/list")
    @Anonymous
    public R list(Integer page, Integer limit) {
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<ExamVo> list = examService.selectExamList();
        long total = new PageInfo(list).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("rows", list);
        data.put("count", total);
        return R.ok(data);
    }

    /**
     * 开始考试 - 获取试卷详情
     */
    @GetMapping("/read")
    @Anonymous
    public R read(Long id) {
        if (id == null) {
            return R.fail("试卷ID不能为空");
        }
        ExamDetailVo detail = examService.selectExamById(id);
        if (detail == null) {
            return R.fail("未找到相关考试内容");
        }

        return R.ok(detail);
    }
}