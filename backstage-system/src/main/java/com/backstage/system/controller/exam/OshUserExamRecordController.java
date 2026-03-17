package com.backstage.system.controller.exam;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.dto.exam.UserExamSaveDto;
import com.backstage.system.domain.vo.exam.UserExamRecordVo;
import com.backstage.system.service.exam.IOshUserExamRecordService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pc/user_test")
public class OshUserExamRecordController extends BaseController {

    @Autowired
    private IOshUserExamRecordService userTestService;

    /**
     * 我的考试记录列表
     */
    @GetMapping("/list")
    @Anonymous // 暂时跳过权限校验
    public R list(Integer page) {
        if (page == null) page = 1;
        List<UserExamRecordVo> list = userTestService.selectUserTestList(page);
        Map<String, Object> data = new HashMap<>();
        data.put("rows", list);
        data.put("count", new PageInfo(list).getTotal());
        
        return R.ok(data);
    }

    @PostMapping("/save")
    @Anonymous
    public R save(@RequestBody UserExamSaveDto saveDto) {
        boolean result = userTestService.saveUserExam(saveDto);
        if (result) {
            return R.ok("ok");
        }
        return R.fail("交卷失败");
    }
}