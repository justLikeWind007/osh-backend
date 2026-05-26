package com.backstage.system.controller.user;

import com.backstage.common.core.domain.R;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.user.OshUserInvitationMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.utils.UserContextUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户中心 - 邀请码功能
 */
@RestController
@RequestMapping("/pc/user/invitation")
public class UserInvitationController {

    @Resource
    private OshUserMapper oshUserMapper;

    @Resource
    private OshUserInvitationMapper oshUserInvitationMapper;

    /**
     * 获取当前用户的邀请码
     */
    @GetMapping("/code")
    public R getMyInviteCode() {
        Long userId = UserContextUtil.getCurrentUserId();
        if (userId == null) {
            return R.fail("请先登录");
        }
        OshUser user = oshUserMapper.getUserInfoById(userId);
        if (user == null) {
            return R.fail("用户不存在");
        }
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("inviteCode", user.getInviteCode());
        return R.ok(data);
    }

    /**
     * 获取我邀请的人列表
     */
    @GetMapping("/list")
    public R getMyInviteeList() {
        Long userId = UserContextUtil.getCurrentUserId();
        if (userId == null) {
            return R.fail("请先登录");
        }
        List<Map<String, Object>> list = oshUserInvitationMapper.selectInviteeListByInviterId(userId);
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("count", list.size());
        data.put("rows", list);
        return R.ok(data);
    }
}
