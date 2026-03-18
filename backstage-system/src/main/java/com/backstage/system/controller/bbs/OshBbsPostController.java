package com.backstage.system.controller.bbs;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.core.domain.R;
import com.backstage.common.utils.SecurityUtils;
import com.backstage.system.domain.bbs.OshBbsPost;
import com.backstage.system.service.bbs.IOshBbsPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/pc/post")
public class OshBbsPostController extends BaseController {

    @Autowired
    private IOshBbsPostService postService;

    /**
     * 获取帖子列表
     * 逻辑全部下沉到 Service
     */
    @GetMapping("/list")
    @Anonymous
    public R list(Long bbs_id, Integer is_top) {
        Map<String, Object> data = postService.selectPostListVo(bbs_id, is_top);
        return R.ok(data);
    }

    /**
     * 获取帖子详情
     * @param id 帖子ID
     */
    @GetMapping("/read")
    @Anonymous
    public R read(@RequestParam("id") Long id) {
        Map<String, Object> data = postService.selectPostByIdVo(id);
        return R.ok(data);
    }

    @GetMapping("/post_comment")
    @Anonymous
    public R getComments(Long post_id,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        // 开启分页并获取转换后的数据
        Map<String, Object> data = postService.selectCommentListVo(post_id, page, pageSize);

        return R.ok(data);
    }

    /**
     * 点赞帖子 (测试阶段：写死用户ID)
     */
    @PostMapping("/support")
    @Anonymous // 允许不带 Token 访问
    public R support(@RequestBody Map<String, Object> params) {
        Long postId = Long.valueOf(params.get("post_id").toString());

        // --- 临时写死：假设当前登录的是 ID 为 1 的用户 ---
        Long userId = 1L;

        // 执行点赞
        return postService.supportPost(postId, userId);
    }

    /**
     * 删除帖子 (软删除)
     */
    @PostMapping("/unsupport")
    @Anonymous
    public R delete(@RequestBody Map<String, Long> params) {
        Long postId = params.get("id"); // 假设前端传的是 id
        if (postId == null) {
            return R.fail("帖子ID不能为空");
        }

        // 获取当前登录用户
        Long userId = 1L;

        return postService.unsupportPost(postId, userId);
    }

    @PostMapping("/reply")
    @Anonymous
    public R reply(@RequestBody Map<String, Object> params) {
        // 直接把原始 Map 传进去，逻辑在 Service 里撕开
        return postService.replyPost(params);
    }

    /**
     * 删除帖子接口 (Content-Type: application/x-www-form-urlencoded)
     */
    @PostMapping("/delete")
    @Anonymous
    public R deletePost(Long id) {
        // 获取当前用户，用于鉴权（只有发帖人或管理员能删）
        Long loginUserId = 1L; // 建议改为 SecurityUtils.getUserId()

        // 逻辑全部丢给 service
        return postService.deletePostById(id, loginUserId);
    }

    /**
     * 我的帖子列表 (GET 请求)
     */
    @GetMapping("/mypost")
    @Anonymous
    public R getMyPostList(@RequestParam(defaultValue = "1") Integer page) {
        // 获取当前登录用户 ID
        Long userId = 1L; // 建议从 SecurityUtils 获取
        // 逻辑全部写在 Service
        return postService.getMyPostList(page, userId);
    }

    @PostMapping("/save")
    @Anonymous
    public R savePost(@RequestBody Map<String, Object> params) {
        // 获取当前登录用户ID
        Long userId = 1L; // 建议从 SecurityUtils.getUserId() 获取

        return postService.savePost(params, userId);
    }
}