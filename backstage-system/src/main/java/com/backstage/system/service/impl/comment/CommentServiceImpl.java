package com.backstage.system.service.impl.comment;

import com.backstage.common.core.domain.R;
import com.backstage.common.core.redis.RedisCache;
import com.backstage.common.enums.CommentType;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.comment.Comment;
import com.backstage.system.domain.comment.dto.CourseCommentAddDTO;
import com.backstage.system.domain.user.User;
import com.backstage.system.domain.vo.CommentVo;
import com.backstage.system.mapper.comment.CommentMapper;
import com.backstage.system.mapper.user.UserMapper;
import com.backstage.system.service.comment.ICommentService;
import com.backstage.common.utils.PageUtils;
import com.backstage.system.service.comment.CommentForbiddenWordFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private CommentForbiddenWordFilter commentForbiddenWordFilter;

    @Value("${token.secret}")
    private String secret;

    @Override
    public boolean existsCourseInColumn(Long columnId, Long courseId) {
        return commentMapper.existsCourseInColumn(columnId, courseId);
    }

    @Override
    public List<CommentVo> listCourseComments(Long courseId) {
        List<CommentVo> rootComments = commentMapper.selectCourseCommentList(courseId, CommentType.COURSE.getCode());
        PageUtils.clearPage();
        if (rootComments == null || rootComments.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, CommentVo> rootCommentMap = new LinkedHashMap<>(rootComments.size());
        for (CommentVo rootComment : rootComments) {
            rootComment.setReplyList(new ArrayList<>());
            rootCommentMap.put(rootComment.getId(), rootComment);
        }

        List<Long> rootIds = rootComments.stream()
                .map(CommentVo::getId)
                .collect(Collectors.toList());
        List<CommentVo> replyList = commentMapper.selectCourseReplyList(courseId, rootIds, CommentType.COURSE.getCode());
        for (CommentVo reply : replyList) {
            CommentVo rootComment = rootCommentMap.get(reply.getParentId());
            if (rootComment != null) {
                rootComment.getReplyList().add(reply);
            }
        }
        return rootComments;
    }

    // TODO 后续优化成使用ES来处理评论内容审查
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<CommentVo> addCourseComment(String token, CourseCommentAddDTO addDTO) {
        if (addDTO == null) {
            return R.fail("请求参数不能为空");
        }
        if (addDTO.getColumnId() == null || addDTO.getCourseId() == null) {
            return R.fail("专栏ID和课程ID不能为空");
        }

        Long parentId = addDTO.getParentId() == null ? 0L : addDTO.getParentId();
        String content = StringUtils.trim(addDTO.getContent());
        if (StringUtils.isEmpty(content)) {
            return R.fail("评论内容不能为空");
        }
        if (content.length() > 1000) {
            return R.fail("评论内容不能超过1000个字符");
        }
        if (!existsCourseInColumn(addDTO.getColumnId(), addDTO.getCourseId())) {
            return R.fail("该课程不属于当前专栏");
        }
        String matchedForbiddenWord = commentForbiddenWordFilter.matchForbiddenWord(content);
        if (StringUtils.isNotEmpty(matchedForbiddenWord)) {
            return R.fail(String.format("评论内容包含违禁词【%s】，请调整后再提交", matchedForbiddenWord));
        }

        // TODO 登录状态问题应该封装在过滤器或拦截器中处理,进入到接口中只需要获取userId
        Long userId = parseUserId(token);
        if (userId == null) {
            return R.fail("登录状态已过期");
        }

        String cacheToken = redisCache.getCacheObject("LoginUser:" + userId);
        if (StringUtils.isEmpty(cacheToken) || !StringUtils.equals(cacheToken, token)) {
            return R.fail("登录状态已过期");
        }

        User user = userMapper.getUserInfoById(userId);
        if (user == null) {
            return R.fail("用户不存在或已被禁用");
        }


        if (parentId > 0 && !commentMapper.existsRootCommentInCourse(addDTO.getCourseId(), parentId, CommentType.COURSE.getCode())) {
            return R.fail("父评论不存在，或当前仅支持回复一级评论");
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setParentId(parentId);
        comment.setContent(content);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setStatus(CommentType.COURSE.getCode());
        commentMapper.insertComment(comment);
        commentMapper.insertCommentRelation(comment.getId(), CommentType.COURSE.getCode(), addDTO.getCourseId());
        if (parentId > 0) {
            commentMapper.increaseReplyCount(parentId);
        }

        CommentVo commentVo = commentMapper.selectCommentById(comment.getId());
        if (commentVo != null) {
            commentVo.setReplyList(new ArrayList<>());
        }
        return R.ok(commentVo, "评论成功");
    }


    // TODO 后续切换成系统统一封装好的解析方法
    private Long parseUserId(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("user_id", Long.class);
        } catch (SignatureException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
