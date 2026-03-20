package com.backstage.system.service.bbs;

import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.bbs.OshBbsPost;

import java.util.Map;

public interface IOshBbsPostService {
    Map<String, Object> selectPostListVo(Long categoryId, Integer isTop);

    Map<String, Object> selectPostByIdVo(Long id);

    Map<String, Object> selectCommentListVo(Long postId, Integer page, Integer pageSize);

    R supportPost(Long postId, Long userId);

    R unsupportPost(Long postId, Long userId);

    R replyPost(Map<String, Object> params);

    R deletePostById(Long id, Long loginUserId);

    R getMyPostList(Integer page, Long userId);

    R savePost(Map<String, Object> params, Long userId);
}
