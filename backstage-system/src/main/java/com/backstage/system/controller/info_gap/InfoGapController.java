package com.backstage.system.controller.info_gap;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.dto.info_gap.InfoGapCreateDTO;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.domain.vo.info_gap.InfoGapVO;
import com.backstage.system.service.info_gap.InfoGapService;
import com.backstage.system.utils.UserContextUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pc/info_gap")
public class InfoGapController {

    @Autowired
    private InfoGapService infoGapService;

    @GetMapping("/list")
    @Anonymous
    public R<PageResponse<InfoGapVO>> list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "hot") String type) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long currentUserId = currentOshUser == null ? null : currentOshUser.getId();

        List<InfoGapVO> infoGapList = infoGapService.getInfoGapList(pageNum, 10, type, currentUserId);
        PageInfo<InfoGapVO> pageInfo = new PageInfo<>(infoGapList);

        return R.ok(PageResponse.of(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()));
    }

    /**
     * 发布信息差
     */
    @PostMapping("/save")
    public R<Void> save(@RequestBody InfoGapCreateDTO dto) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long currentUserId = currentOshUser == null ? null : currentOshUser.getId();

        infoGapService.createInfoGap(dto, currentUserId);
        return R.ok();
    }

    /**
     * 评价 (点赞/踩/中评)
     */
    @PostMapping("/vote")
    public R<Void> vote(@RequestParam Long id, @RequestParam Integer type) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long currentUserId = currentOshUser == null ? null : currentOshUser.getId();

        infoGapService.vote(currentUserId, id, type);
        return R.ok();
    }

    /**
     * 关注/取消关注作者 (对应图片中的 +关注 按钮)
     * @param authorId 被关注的作者ID
     */
    @PostMapping("/follow/{authorId}")
    public R<Void> follow(@PathVariable Long authorId) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long currentUserId = currentOshUser == null ? null : currentOshUser.getId();

        infoGapService.toggleFollow(currentUserId, authorId);
        return R.ok();
    }

    /**
     * 精品推荐
     * @return
     */
    @GetMapping("/recommend")
    @Anonymous
    public R<List> recommend() {
        return R.ok(infoGapService.recommend());
    }

}