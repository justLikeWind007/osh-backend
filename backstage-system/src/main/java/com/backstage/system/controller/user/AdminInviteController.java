package com.backstage.system.controller.user;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.OshUserLevel;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.redis.RedisCache;
import com.backstage.common.utils.SecurityUtils;
import com.backstage.common.utils.StringUtils;
import com.backstage.common.utils.generate.GenerateUtil;
import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.domain.user.OshUserAsset;
import com.backstage.system.mapper.user.OshUserAssetMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.mapper.user.UserManageMapper;
import com.backstage.system.utils.UserContextUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.net.URL;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 管理员邀请注册（仅创始人 level=6 可用）
 * 邀请普通管理员(level=4) 或 核心开发者(level=5)
 */
@RestController
@RequestMapping("/pc/admin/invite")
public class AdminInviteController {

    private static final Logger log = LoggerFactory.getLogger(AdminInviteController.class);

    /** Redis key 前缀 */
    private static final String INVITE_KEY_PREFIX = "admin:invite:";
    /** 邀请链接有效期：7天 */
    private static final long INVITE_EXPIRE_DAYS = 7;

    @Resource
    private RedisCache redisCache;
    @Resource
    private OshUserMapper oshUserMapper;
    @Resource
    private OshUserAssetMapper oshUserAssetMapper;
    @Resource
    private UserManageMapper userManageMapper;
    @Resource
    private JavaMailSender javaMailSender;

    private static final String MAIL_FROM = "18482663265@163.com";

    /**
     * 创建管理员邀请（创始人专属）
     * @param params: username, email, roleId (5=普通管理员对应role_id, 6=核心开发者对应role_id)
     */
    @PostMapping("/create")
    @OshUserLevel(value = 6)
    public R createInvite(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String username = (String) params.get("username");
        String email = (String) params.get("email");
        Integer roleId = Integer.valueOf(params.get("roleId").toString());

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
            return R.fail("用户名和邮箱不能为空");
        }

        // 用户名格式校验：4-20位，字母开头，字母数字下划线
        if (!username.matches(OshUserConstants.USERNAME_PATTERN)) {
            return R.fail("用户名必须是4-20位字母、数字、下划线组成，且以字母开头");
        }

        // 邮箱格式校验
        if (!email.matches("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")) {
            return R.fail("邮箱格式不正确");
        }

        // 只允许邀请普通管理员(role_id=5) 或 核心开发者(role_id=6)
        if (roleId != 5 && roleId != 6) {
            return R.fail("只能邀请普通管理员或核心开发者");
        }

        // 检查用户名和邮箱是否已存在
        LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getUsername, username);
        if (oshUserMapper.selectOne(wrapper) != null) {
            return R.fail("用户名已存在");
        }
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getEmail, email);
        if (oshUserMapper.selectOne(wrapper) != null) {
            return R.fail("邮箱已被注册");
        }

        // 生成邀请 token
        String inviteToken = UUID.randomUUID().toString().replace("-", "");

        // 存入 Redis
        Map<String, String> inviteData = new HashMap<>();
        inviteData.put("username", username);
        inviteData.put("email", email);
        inviteData.put("roleId", roleId.toString());
        inviteData.put("inviterId", UserContextUtil.getCurrentUserId().toString());
        redisCache.setCacheObject(INVITE_KEY_PREFIX + inviteToken, inviteData, (int)(INVITE_EXPIRE_DAYS * 24 * 60), TimeUnit.MINUTES);

        // 发送邮件通知被邀请人
        String roleName = roleId == 5 ? "普通管理员" : "核心开发者";
        // 动态获取前端域名：优先从 Origin/Referer 头获取
        String origin = request.getHeader("Origin");
        if (StringUtils.isEmpty(origin)) {
            String referer = request.getHeader("Referer");
            if (StringUtils.isNotEmpty(referer)) {
                try {
                    URL url = new URL(referer);
                    origin = url.getProtocol() + "://" + url.getHost() + (url.getPort() > 0 && url.getPort() != 80 && url.getPort() != 443 ? ":" + url.getPort() : "");
                } catch (Exception e) {
                    origin = "http://juegeresource.top";
                }
            } else {
                origin = "http://juegeresource.top";
            }
        }
        String inviteLink = origin + "/admin-register?token=" + inviteToken;
        String subject = "您被邀请成为平台" + roleName;
        String content = "您好 " + username + "，\n\n"
                + "您已被邀请注册为平台「" + roleName + "」。\n"
                + "请点击以下链接完成注册（7天内有效）：\n\n"
                + inviteLink + "\n\n"
                + "如非本人操作，请忽略此邮件。";
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(MAIL_FROM);
            mail.setTo(email);
            mail.setSubject(subject);
            mail.setText(content);
            javaMailSender.send(mail);
        } catch (Exception e) {
            log.warn("发送邀请邮件失败, email={}", email, e);
        }

        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("inviteToken", inviteToken);
        data.put("inviteLink", inviteLink);
        data.put("expireDays", INVITE_EXPIRE_DAYS);
        return R.ok(data);
    }

    /**
     * 查询邀请信息（被邀请人打开链接时调用，匿名可访问）
     */
    @Anonymous
    @GetMapping("/info")
    public R getInviteInfo(@RequestParam String token) {
        Map<String, String> inviteData = redisCache.getCacheObject(INVITE_KEY_PREFIX + token);
        if (inviteData == null) {
            return R.fail("邀请链接无效或已过期");
        }
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("username", inviteData.get("username"));
        data.put("email", inviteData.get("email"));
        String roleId = inviteData.get("roleId");
        data.put("roleName", "5".equals(roleId) ? "普通管理员" : "核心开发者");
        return R.ok(data);
    }

    /**
     * 被邀请人确认注册（匿名可访问）
     */
    @Anonymous
    @PostMapping("/confirm")
    public R confirmInvite(@RequestBody Map<String, Object> params) {
        String token = (String) params.get("token");
        String password = (String) params.get("password");
        String repassword = (String) params.get("repassword");

        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(password)) {
            return R.fail("参数不完整");
        }
        if (!password.equals(repassword)) {
            return R.fail("两次密码不一致");
        }

        // 从 Redis 获取邀请数据
        Map<String, String> inviteData = redisCache.getCacheObject(INVITE_KEY_PREFIX + token);
        if (inviteData == null) {
            return R.fail("邀请链接无效或已过期");
        }

        String username = inviteData.get("username");
        String email = inviteData.get("email");
        Integer roleId = Integer.valueOf(inviteData.get("roleId"));

        // 再次检查用户名和邮箱
        LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getUsername, username);
        if (oshUserMapper.selectOne(wrapper) != null) {
            return R.fail("用户名已被注册");
        }
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshUser::getEmail, email);
        if (oshUserMapper.selectOne(wrapper) != null) {
            return R.fail("邮箱已被注册");
        }

        // 创建用户
        Long userId = GenerateUtil.generateSnowflakeId();
        OshUser oshUser = new OshUser();
        oshUser.setId(userId);
        oshUser.setUsername(username);
        oshUser.setPassword(SecurityUtils.encryptPassword(password));
        oshUser.setEmail(email);
        oshUser.setInviteCode(generateUniqueInviteCode());
        oshUser.setDeleteFlag((byte) 0);
        ThreadLocalUtil.set(OshUserConstants.USER_ID, userId);
        oshUserMapper.insert(oshUser);

        // 生成唯一标识并发送邮件
        String uniqueId = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        oshUserMapper.addUniqueId(userId, uniqueId);

        // 添加普通用户角色（默认）
        oshUserMapper.addRole(userId);

        // 添加管理员角色
        Long operatorId = Long.valueOf(inviteData.get("inviterId"));
        userManageMapper.insertUserRole(userId, roleId, operatorId);

        // 初始化用户资产
        OshUserAsset oshUserAsset = new OshUserAsset();
        oshUserAsset.setPoints(188L);
        oshUserAsset.setUserId(userId);
        oshUserAssetMapper.insert(oshUserAsset);

        // 删除 Redis 邀请数据
        redisCache.deleteObject(INVITE_KEY_PREFIX + token);

        // 发送注册成功邮件
        try {
            String roleName = roleId == 5 ? "普通管理员" : "核心开发者";
            String successContent = "恭喜 " + username + "，\n\n"
                    + "您已成功注册为平台「" + roleName + "」。\n"
                    + "您的唯一标识为：" + uniqueId + "\n"
                    + "请妥善保管，用于找回密码等操作。\n\n"
                    + "现在可以使用用户名和密码登录平台。";
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(MAIL_FROM);
            mail.setTo(email);
            mail.setSubject("注册成功通知");
            mail.setText(successContent);
            javaMailSender.send(mail);
        } catch (Exception e) {
            log.warn("发送注册成功邮件失败, email={}", email, e);
        }

        return R.ok("注册成功");
    }

    /**
     * 生成唯一邀请码
     */
    private String generateUniqueInviteCode() {
        for (int i = 0; i < 10; i++) {
            String code = GenerateUtil.generateInviteCode();
            LambdaQueryWrapper<OshUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OshUser::getInviteCode, code);
            if (oshUserMapper.selectCount(wrapper) == 0) {
                return code;
            }
        }
        return String.valueOf(GenerateUtil.generateSnowflakeId()).substring(10);
    }
}
