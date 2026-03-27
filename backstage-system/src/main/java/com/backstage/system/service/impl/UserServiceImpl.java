package com.backstage.system.service.impl;

import com.backstage.common.core.domain.R;
import com.backstage.common.core.redis.RedisCache;
import com.backstage.system.domain.user.User;
import com.backstage.system.domain.user.vo.UserLoginVo;
import com.backstage.system.mapper.user.UserMapper;
import com.backstage.system.service.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/7
 * Time: 16:42
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine springTemplateEngine;
    // 邮件主题
    private final String subject = "open source helper";
    // 发件人
    private final String from = "18482663265@163.com";

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    @Override
    public R<UserLoginVo> login(String name, String pid) {
        User user = userMapper.getUserByUsernameOrEmail(name);
        if (user == null) {
            return R.fail("用户不存在");
        }
        if (user.getStatus() == 0) {
            return R.fail("用户被拉黑");
        }
        if (pid.length() > 40) {
            String uniqueIdByUserId = userMapper.getUniqueIdByUserId(user.getId());
            if (!uniqueIdByUserId.equals(pid)) {
                return R.fail("唯一标识错误");
            }
        }else {
            if (!user.getPassword().equals(pid)) {
                return R.fail("密码错误");
            }
        }
        String token = createToken(user);
        UserLoginVo userLoginVo = new UserLoginVo();
        BeanUtils.copyProperties(user, userLoginVo);
        userLoginVo.setToken(token);
        redisCache.setCacheObject("LoginUser:" + user.getId(), token, 500, TimeUnit.MINUTES);
        return R.ok(userLoginVo);
    }

    @Override
    public R<String> registerSubmit(String username, String password, String repassword, String email) throws MessagingException {
        if(!password.equals(repassword)){
            return R.fail("两次输入密码不一致");
        }
        User user = userMapper.getUserByUsername(username);
        if (user != null && user.getUsername().equals(username)) {
            return R.fail("用户名已存在");
        }
        user = userMapper.getUserByEmail(email);
        if (user != null && user.getEmail().equals(email)) {
            return R.fail("邮箱已被绑定");
        }
        checkEmail(email);
        String uniqueId = sendEmail(username, email);
        Map<String,String> userMap = new HashMap<>();
        userMap.put("username", username);
        userMap.put("password", password);
        userMap.put("email", email);
        redisCache.setCacheObject("uniqueId:" + uniqueId, userMap, 500, TimeUnit.MINUTES);
        return R.ok("邮件发送成功");
    }

    @Override
    public R<String> registerVerity(String uniqueId) {
        Map<String,String> userMap = redisCache.getCacheObject("uniqueId:" + uniqueId);
        if(userMap == null) return R.fail("唯一标识错误或已过期");
        userMapper.register(userMap.get("username"), userMap.get("password"), userMap.get("email"));
        User user = userMapper.getUserByUsername(userMap.get("username"));
        userMapper.addUniqueId(user.getId(), uniqueId);
        redisCache.deleteObject("uniqueId:" + uniqueId);
        return R.ok("注册成功");
    }

    @Override
    public R<String> logout(String token) {
        Long userId = getUserIdByToken(token);
        String key = "LoginUser:" + userId;
        if (redisCache.hasKey(key)) {
            redisCache.deleteObject("LoginUser:" + userId);
            return R.ok("退出成功");
        }
        return R.fail("Token 令牌不合法，请重新登录");
    }

    @Override
    public R<String> changeEmailSubmit(String token, String uniqueId, String newEmail) throws MessagingException {
        Long userId = getUserIdByToken(token);
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return R.fail("用户不存在");
        }
        String uniqueIdByUserId = userMapper.getUniqueIdByUserId(userId);
        if (!uniqueIdByUserId.equals(uniqueId)) {
            return R.fail("唯一标识错误");
        }
        User emailUser = userMapper.getUserByEmail(newEmail);
        if (emailUser != null && user.getEmail().equals(newEmail)) {
            return R.fail("邮箱已被绑定");
        }
        String newUniqueId = sendEmail(user.getUsername(), newEmail);
        Map<String,String> userMap = new HashMap<>();
        userMap.put("userId", userId.toString());
        userMap.put("email", newEmail);
        redisCache.setCacheObject("re:uniqueId:" + newUniqueId, userMap, 500, TimeUnit.MINUTES);
        return R.ok("旧邮箱的唯一标识验证成功");
    }

    @Override
    public R<String> changeEmailVerity(String token, String uniqueId) {
        Long userId = getUserIdByToken(token);
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return R.fail("用户不存在");
        }
        Map<String,String> userMap = redisCache.getCacheObject("re:uniqueId:" + uniqueId);
        if (userMap == null) return R.fail("新的唯一标识错误或已过期");
        if (!userId.equals(Long.parseLong(userMap.get("userId")))) return R.fail("非法用户");
        userMapper.updateEmailById(userId, userMap.get("email"));
        userMapper.updateUniqueIdByUserId(userId, uniqueId);
        redisCache.deleteObject("re:uniqueId:" + uniqueId);
        return R.ok("邮箱修改成功");
    }

    @Override
    public R<String> forget(String uniqueId, String password, String repassword) {
        Long userId = userMapper.getUserIdByUniqueId(uniqueId);
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            return R.fail("用户不存在");
        }
        if(!password.equals(repassword)){
            return R.fail("两次输入密码不一致");
        }
        String uniqueIdByUserId = userMapper.getUniqueIdByUserId(userId);
        if (!uniqueIdByUserId.equals(uniqueId)) {
            return R.fail("唯一标识错误");
        }
        userMapper.updatePasswordById(userId, password);
        return R.ok("ok");
    }

    @Override
    public R<String> updateInfo(String avatar, String nickname, String sex, String token) {
        Long userId = getUserIdByToken(token);
        userMapper.updateUserInfoById(userId, avatar, nickname, sex);
        return R.ok("ok");
    }

    @Override
    public R<String> updatePassword(String opassword, String password, String repassword, String token) {
        Long userId = getUserIdByToken(token);
        String passwordById = userMapper.getPasswordById(userId);
        if (passwordById != null && !passwordById.equals(opassword)) {
            return R.fail("密码错误");
        }
        if(!password.equals(repassword)){
            return R.fail("两次输入密码不一致");
        }
        userMapper.updatePasswordById(userId, password);
        return R.ok("ok");
    }

    @Override
    public R<User> getUserInfo(String token) {
        Long userId = getUserIdByToken(token);
        User user = userMapper.getUserInfoById(userId);
        return R.ok(user);
    }

    public String createToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("password", user.getPassword());
        System.out.println(secret);
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 解析token
     */
    public Claims parseToken(String secret, String token) {
        try {
            // 使用相同的secret解析token
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (SignatureException e) {
            // 签名验证失败
            throw new RuntimeException("Token签名验证失败", e);
        } catch (Exception e) {
            // token过期或其他解析错误
            throw new RuntimeException("Token解析失败", e);
        }
    }

    public Long getUserIdByToken(String token) {
        Claims claims = parseToken(secret, token);
        return claims.get("user_id", Long.class);
    }

    public void checkEmail(String email) {
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.matches(regex)) {
            throw new RuntimeException("邮箱格式错误");
        }
    }

    public String sendEmail(String username, String email) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        // 准备模板数据
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("message", "欢迎使用我们的服务！");
        String uniqueId = UUID.randomUUID() + "-" + System.currentTimeMillis();
        context.setVariable("uniqueId", uniqueId);
        // 通过模板引擎生成HTML内容
        String content = springTemplateEngine.process("mail-template", context);
        // 设置邮件内容
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setFrom(from);
        //
        ClassPathResource imageResource = new ClassPathResource("static/open-source-helper.jpg");
        mimeMessageHelper.addInline("logoImage", imageResource);
        javaMailSender.send(mimeMessage);
        return uniqueId;
    }






    /**
     * 查询用户
     *
     * @param id 用户主键
     * @return 用户
     */
    @Override
    public User selectUserById(Long id)
    {
        return userMapper.selectUserById(id);
    }

    /**
     * 查询用户列表
     *
     * @param user 用户
     * @return 用户
     */
    @Override
    public List<User> selectUserList(User user)
    {
        return userMapper.selectUserList(user);
    }

    /**
     * 新增用户
     *
     * @param user 用户
     * @return 结果
     */
    @Override
    public int insertUser(User user)
    {
        return userMapper.insertUser(user);
    }

    /**
     * 修改用户
     *
     * @param user 用户
     * @return 结果
     */
    @Override
    public int updateUser(User user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 批量删除用户
     *
     * @param ids 需要删除的用户主键
     * @return 结果
     */
    @Override
    public int deleteUserByIds(Long[] ids)
    {
        return userMapper.deleteUserByIds(ids);
    }

    /**
     * 删除用户信息
     *
     * @param id 用户主键
     * @return 结果
     */
    @Override
    public int deleteUserById(Long id)
    {
        return userMapper.deleteUserById(id);
    }
}