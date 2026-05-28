package com.backstage.system.controller.info_gap;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.common.exception.ServiceException;
import com.backstage.common.response.PageResponse;
import com.backstage.system.domain.dto.info_gap.InfoGapCreateDTO;
import com.backstage.system.domain.dto.info_gap.InfoGapSearchReqDTO;
import com.backstage.system.domain.dto.info_gap.InfoGapUpdateReqDTO;
import com.backstage.system.domain.dto.info_gap.InfoGapVoteReqDTO;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.domain.vo.info_gap.InfoGapVO;
import com.backstage.system.service.info_gap.InfoGapCollectService;
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
    @Autowired
    private InfoGapCollectService infoGapCollectService;

    /**
     * 信息差列表
     * @param type hot、latest
     */
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
     * 信息差列表
     * @param type follow、collect
     */
    @GetMapping("/list/user")
    public R<PageResponse<InfoGapVO>> listUser(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "follow") String type) {
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
        String currentUsername = currentOshUser.getUsername();
        Long currentUserId = currentOshUser == null ? null : currentOshUser.getId();
        infoGapService.createInfoGap(dto, currentUserId, currentUsername);

        return R.ok((Void) null, "提交成功，等待审核");
    }

    /**
     * 修改我发布的信息差
     */
    @PostMapping("/update")
    public R<Void> updateInfoGap(@RequestBody InfoGapUpdateReqDTO dto) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long currentUserId = currentOshUser == null ? null : currentOshUser.getId();
        infoGapService.updateInfoGap(dto, currentUserId);

        return R.ok(null, "修改成功，等待审核");
    }

    /**
     * 删除我发布的信息差
     */
    @GetMapping("/delete")
    public R<Void> deleteInfoGap(@RequestParam("infoGapId") Long infoGapId) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long currentUserId = currentOshUser == null ? null : currentOshUser.getId();
        infoGapService.deleteInfoGap(infoGapId);

        return R.ok(null, "当前信息差删除成功！");
    }

    /**
     * 信息差点评接口，1-好评 2-中评 3-差评
     */
    @PostMapping("/vote")
    public R<Void> vote(@RequestBody InfoGapVoteReqDTO request) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long currentUserId = currentOshUser == null ? null : currentOshUser.getId();

        infoGapService.vote(currentUserId, request.getId(), request.getType());
        return R.ok();
    }

    /**
     * 精品推荐
     */
    @GetMapping("/recommend")
    @Anonymous
    public R<List> recommend() {
        List<InfoGapVO> infoGapVOList = infoGapService.recommend();

        return R.ok(infoGapVOList);
    }

    /**
     * 收藏/取消收藏信息差
     */
    @GetMapping("/collect")
    public R<String> collect(@RequestParam("infoGapId") Long infoGapId) {
        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long currentUserId = currentOshUser == null ? null : currentOshUser.getId();

        return infoGapCollectService.collectInfoGap(currentUserId, infoGapId);
    }

    /**
     * 统计信息差观看次数
     */
    @GetMapping("/view")
    public R<Void> view(@RequestParam("infoGapId") Long infoGapId) {
        infoGapService.viewCount(infoGapId);

        return R.ok();
    }

    /**
     * 搜索信息差，支持关键字、标签、类别搜索信息差
     */
    @PostMapping("/search")
    @Anonymous
    public R<PageResponse<InfoGapVO>> search(@RequestBody InfoGapSearchReqDTO request) {
        if (request.getKeyword() != null) {
            request.setKeyword(request.getKeyword().trim());
            if (request.getKeyword().isEmpty()) {
                request.setKeyword(null);
            }
        }

        if (request.getCategory() != null) {
            request.setCategory(request.getCategory().trim());
            if (request.getCategory().isEmpty()) {
                request.setCategory(null);
            }
        }

        if (request.getKeyword() == null && request.getTagId() == null && request.getCategory() == null) {
            throw new ServiceException("关键字、标签、类别不能同时为空");
        }

        OshUser currentOshUser = UserContextUtil.getCurrentUser();
        Long currentUserId = currentOshUser == null ? null : currentOshUser.getId();
        List<InfoGapVO> infoGapSearchList = infoGapService.searchInfoGap(request, currentUserId);
        PageInfo<InfoGapVO> pageInfo = new PageInfo<>(infoGapSearchList);

        return R.ok(PageResponse.of(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize()));
    }
}
