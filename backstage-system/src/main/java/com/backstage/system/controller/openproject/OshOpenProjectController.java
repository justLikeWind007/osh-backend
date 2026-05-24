package com.backstage.system.controller.openproject;

import com.backstage.common.annotation.OshUserEvent;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.openproject.OshOpenProjectTag;
import com.backstage.system.domain.openproject.dto.OpenProjectAuditDTO;
import com.backstage.system.domain.openproject.dto.OpenProjectQueryDTO;
import com.backstage.system.domain.openproject.dto.OpenProjectSubmitDTO;
import com.backstage.system.domain.openproject.vo.OpenProjectRankVO;
import com.backstage.system.domain.openproject.vo.OpenProjectVO;
import com.backstage.system.service.openproject.IOshOpenProjectFavoriteService;
import com.backstage.system.service.openproject.IOshOpenProjectRankService;
import com.backstage.system.service.openproject.IOshOpenProjectService;
import com.backstage.system.utils.UserContextUtil;
import com.backstage.system.domain.vo.tool.ToolAnnouncementVO;
import com.backstage.system.mapper.openproject.OshOpenProjectAnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/pc/openproject")
public class OshOpenProjectController {

    @Autowired
    private IOshOpenProjectService openProjectService;

    @Autowired
    private IOshOpenProjectFavoriteService favoriteService;

    @Autowired
    private IOshOpenProjectRankService rankService;

    @Autowired
    private OshOpenProjectAnnouncementMapper announcementMapper;

    /** 获取开源项目最新公告 */
    @GetMapping("/announcements")
    public R<List<ToolAnnouncementVO>> getAnnouncements() {
        return R.ok(announcementMapper.selectLatestOpenProjectAnnouncements());
    }

    /** 分页查询已通过的开源项目列表 */
    @PostMapping("/list")
    @OshUserEvent(module = "开源项目", actionType = "查询", resourceType = "开源项目")
    @PreAuthorize("hasAuthority('op:list')")
    public R<Map<String, Object>> list(@RequestBody OpenProjectQueryDTO queryDTO) {
        return R.ok(openProjectService.listPage(queryDTO));
    }

    /** 查询待审核列表 */
    @PostMapping("/pending")
    @PreAuthorize("hasAuthority('op:audit')")
    @OshUserEvent(module = "开源项目", actionType = "查询", resourceType = "开源项目")
    public R<Map<String, Object>> pending(@RequestBody OpenProjectQueryDTO queryDTO) {
        return R.ok(openProjectService.listPending(queryDTO));
    }

    /** 审核开源项目 */
    @PostMapping("/audit")
    @PreAuthorize("hasAuthority('op:audit')")
    @OshUserEvent(module = "开源项目", actionType = "审核", resourceType = "开源项目")
    public R<Void> audit(@RequestBody OpenProjectAuditDTO dto) {
        try {
            openProjectService.audit(dto);
            return R.ok();
        } catch (IllegalArgumentException e) {
            return R.fail(e.getMessage());
        } catch (Exception e) {
            return R.fail("审核失败，请稍后重试");
        }
    }

    /** 查询所有标签 */
    @PreAuthorize("hasAuthority('op:tags')")
    @OshUserEvent(module = "开源项目", actionType = "查询", resourceType = "开源项目")
    @GetMapping("/tags")
    public R<List<OshOpenProjectTag>> tags() {
        return R.ok(openProjectService.listTags());
    }

    /** 查询项目详情 */
    @GetMapping("/detail/{id}")
    @PreAuthorize("hasAuthority('op:detail')")
    @OshUserEvent(module = "开源项目", actionType = "查询", resourceType = "开源项目")
    public R<OpenProjectVO> detail(@PathVariable Long id) {
        OpenProjectVO vo = openProjectService.getDetail(id);
        if (vo == null) return R.fail("项目不存在");
        return R.ok(vo);
    }

    /** 增加点击次数 */
    @PutMapping("/click")
    @OshUserEvent(module = "开源项目", actionType = "点击", resourceType = "开源项目")
    @PreAuthorize("hasAuthority('op:click')")
    public R<Void> click(@RequestParam Long id) {
        openProjectService.incrementClickCount(id);
        return R.ok();
    }

    /** 用户提交开源项目 */
    @PostMapping("/submit")
    @PreAuthorize("hasAuthority('op:submit')")
    @OshUserEvent(module = "开源项目", actionType = "提交", resourceType = "开源项目")
    public R<Void> submit(@RequestBody OpenProjectSubmitDTO dto) {
        try {
            openProjectService.submit(dto);
            return R.ok();
        } catch (IllegalArgumentException e) {
            return R.fail(e.getMessage());
        } catch (Exception e) {
            return R.fail("提交失败，请稍后重试");
        }
    }

    /** 收藏项目 */
    @PostMapping("/favorite")
    @PreAuthorize("hasAuthority('op:collection')")
    public R<Void> favorite(@RequestParam Long projectId) {
        Long userId = UserContextUtil.getCurrentUserId();
        favoriteService.favorite(userId, projectId);
        return R.ok();
    }

    /** 取消收藏 */
    @PostMapping("/favorite/cancel")
    @PreAuthorize("hasAuthority('op:cancel:collection')")
    public R<Void> cancelFavorite(@RequestParam Long projectId) {
        Long userId = UserContextUtil.getCurrentUserId();
        favoriteService.cancelFavorite(userId, projectId);
        return R.ok();
    }

    /**
     * 排行榜
     * @param rankType star / fork
     * @param period   7 / 30（天）
     * @param topN     返回前 N 名，默认 10
     */
    @GetMapping("/rank")
    @PreAuthorize("hasAuthority('op:rank')")
    public R<List<OpenProjectRankVO>> rank(
            @RequestParam(defaultValue = "star") String rankType,
            @RequestParam(defaultValue = "7")    int period,
            @RequestParam(defaultValue = "10")   int topN) {
        return R.ok(rankService.getRank(rankType, period, topN));
    }
}
