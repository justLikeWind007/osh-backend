package com.backstage.system.controller.studyprogress;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.vo.studyprogress.UserStudyProgressVo;
import com.backstage.system.service.studyprogress.IStudyProgressService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pc/user_study_progress")
public class OshUserStudyProgressController extends BaseController {

    @Autowired
    private IStudyProgressService studyProgressService;

    /**
     * 获取学习记录列表
     */
    @GetMapping("/list")
    public R list() {
        startPage(); // 开启 RuoYi 分页逻辑
        
        // 按照你的要求：写死 userId 为 1
        Long userId = 1L; 
        
        List<UserStudyProgressVo> list = studyProgressService.selectStudyProgressList(userId);
        
        // 获取分页总数并重命名为 count
        long total = new PageInfo(list).getTotal();
        
        Map<String, Object> data = new HashMap<>();
        data.put("rows", list);
        data.put("count", total); 
        
        return R.ok(data);
    }

    /**
     * 更新学习记录
     */
    @PostMapping("/update")
    @Anonymous
    public R update(@RequestBody UserStudyProgressVo vo) {
        // 按照要求：把 userId 写死为 1
        Long userId = 1L;

        // 执行更新逻辑
        boolean success = studyProgressService.updateProgress(vo, userId);

        if (success) {
            return R.ok("ok");
        }
        return R.fail("更新进度失败");
    }
}