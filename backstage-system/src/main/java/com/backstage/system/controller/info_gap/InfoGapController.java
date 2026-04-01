package com.backstage.system.controller.info_gap;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.dto.info_gap.InfoGapCreateDTO;
import com.backstage.system.domain.vo.info_gap.InfoGapVO;
import com.backstage.system.service.info_gap.InfoGapService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pc/info_gap")
public class InfoGapController {

    @Autowired
    private InfoGapService infoGapService;

    @GetMapping("/list")
    @Anonymous
    public R<Page<InfoGapVO>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, // 改成 pageNum
            @RequestParam(defaultValue = "hot") String type) { // 顺便把默认排序改成 hot
        Long currentUserId = 1L; // 建议用工具类获取
        return R.ok(infoGapService.getInfoGapList(pageNum, type, currentUserId));
    }

    /**
     * 发布信息差
     */
    @PostMapping("/save")
    @Anonymous
    public R<Void> save(@RequestBody InfoGapCreateDTO dto) {
        Long loginUserId = 1L;
        infoGapService.createInfoGap(dto, loginUserId);
        return R.ok();
    }

    /**
     * 评价 (点赞/踩/中评)
     */
    @PostMapping("/vote")
    @Anonymous
    public R<Void> vote(@RequestParam Long id, @RequestParam Integer type) {
        Long loginUserId = 1L;
        infoGapService.vote(loginUserId, id, type);
        return R.ok();
    }

    /**
     * 关注/取消关注作者 (对应图片中的 +关注 按钮)
     * @param authorId 被关注的作者ID
     */
    @PostMapping("/follow/{authorId}")
    @Anonymous
    public R<Void> follow(@PathVariable Long authorId) {
        // 同样模拟当前登录用户
        Long loginUserId = 1L;
        infoGapService.toggleFollow(loginUserId, authorId);
        return R.ok();
    }
}