package com.backstage.system.controller.assistant;

import com.backstage.common.constant.HttpStatus;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.service.assistant.IAssistantFeedbackFavoriteService;
import com.backstage.system.service.assistant.IAssistantFeedbackLikeService;
import com.backstage.system.utils.UserContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 反馈互动接口（点赞、收藏）
 *
 * @author backstage
 */
@Api(tags = "AI助手反馈-互动接口")
@RestController
@RequestMapping("/pc/feedback")
@RequiredArgsConstructor
public class AssistantFeedbackInteractionController extends BaseController {

    private final IAssistantFeedbackLikeService likeService;
    private final IAssistantFeedbackFavoriteService favoriteService;

    /**
     * 点赞反馈
     */
    @ApiOperation("点赞反馈")
    @PostMapping("/{id}/like")
    public AjaxResult like(@PathVariable("id") Long feedbackId) {
        Long userId = getCurrentUserId();
        return likeService.like(feedbackId, userId);
    }

    /**
     * 取消点赞
     */
    @ApiOperation("取消点赞")
    @DeleteMapping("/{id}/like")
    public AjaxResult unlike(@PathVariable("id") Long feedbackId) {
        Long userId = getCurrentUserId();
        return likeService.unlike(feedbackId, userId);
    }

    /**
     * 收藏反馈
     */
    @ApiOperation("收藏反馈")
    @PostMapping("/{id}/favorite")
    public AjaxResult favorite(@PathVariable("id") Long feedbackId) {
        Long userId = getCurrentUserId();
        return favoriteService.favorite(feedbackId, userId);
    }

    /**
     * 取消收藏
     */
    @ApiOperation("取消收藏")
    @DeleteMapping("/{id}/favorite")
    public AjaxResult unfavorite(@PathVariable("id") Long feedbackId) {
        Long userId = getCurrentUserId();
        return favoriteService.unfavorite(feedbackId, userId);
    }

    /**
     * 查询用户对反馈的互动状态（点赞、收藏）
     */
    @ApiOperation("查询用户互动状态")
    @GetMapping("/{id}/interaction-status")
    public AjaxResult getInteractionStatus(@PathVariable("id") Long feedbackId) {
        Long userId = getCurrentUserId();

        boolean isLiked = likeService.isLiked(feedbackId, userId);
        boolean isFavorited = favoriteService.isFavorited(feedbackId, userId);

        Map<String, Boolean> status = new HashMap<>();
        status.put("isLiked", isLiked);
        status.put("isFavorited", isFavorited);

        return AjaxResult.success(status);
    }

    /**
     * 获取前台当前用户 ID
     */
    private Long getCurrentUserId() {
        Long userId = UserContextUtil.getCurrentUserId();
        if (userId == null) {
            throw new ServiceException("请先登录后再操作反馈", HttpStatus.UNAUTHORIZED);
        }
        return userId;
    }
}
