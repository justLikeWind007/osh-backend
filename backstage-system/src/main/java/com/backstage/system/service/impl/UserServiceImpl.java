package com.backstage.system.service.impl;

import com.backstage.common.constant.UserConstants;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.redis.RedisCache;
import com.backstage.common.exception.user.UserNotExistsException;
import com.backstage.common.exception.user.UserPasswordNotMatchException;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.user.User;
import com.backstage.system.domain.user.vo.UserLoginVo;
import com.backstage.system.domain.user.vo.UserRegisterVo;
import com.backstage.system.mapper.user.UserMapper;
import com.backstage.system.service.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    @Override
    public R<UserLoginVo> login(String username, String password) {
        loginPreCheck(username, password);
        User user = userMapper.getUserByUsername(username);
        if (user.getStatus() == 0) {
            return R.fail("用户被拉黑");
        }
        if (!user.getPassword().equals(password)) {
            return R.fail("密码错误");
        }
        String token = createToken(user);
        UserLoginVo userLoginVo = new UserLoginVo();
        BeanUtils.copyProperties(user, userLoginVo);
        userLoginVo.setToken(token);
        redisCache.setCacheObject("LoginUser:" + user.getId(), token, 500, TimeUnit.MINUTES);
        return R.ok(userLoginVo);
    }

    @Override
    public R<UserRegisterVo> register(String username, String password, String repassword) {
        if(!password.equals(repassword)){
            return R.fail("两次输入密码不一致");
        }
        User user = userMapper.getUserByUsername(username);
        if (user != null && user.getUsername().equals(username)) {
            return R.fail("用户已存在");
        }
        userMapper.register(username, password);
        User newUser = userMapper.getUserByUsername(username);
        UserRegisterVo userRegisterVO = new UserRegisterVo();
        BeanUtils.copyProperties(newUser, userRegisterVO);
        return R.ok(userRegisterVO);
    }

    @Override
    public R<String> logout(String token) {
        Claims claims = parseToken(secret, token);
        Long userId = claims.get("user_id", Long.class);
        String key = "LoginUser:" + userId;
        if (redisCache.hasKey(key)) {
            redisCache.deleteObject("LoginUser:" + userId);
            return R.ok("退出成功");
        }
        return R.fail("Token 令牌不合法，请重新登录");
    }

    @Override
    public R<String> bindEmail(String token, String email, String code) {
        checkEmail(email);
        Claims claims = parseToken(secret, token);
        Long userId = claims.get("user_id", Long.class);
        String cacheObject = redisCache.getCacheObject("emailCode:" + email);
        if (!code.equals(cacheObject)) {
            return R.fail("验证码错误");
        }
        // 绑定email
        userMapper.updateEmailById(userId, email);
        return R.ok(email);
    }

    @Override
    public R<String> getCaptcha(String token, String email) {
        Claims claims = parseToken(secret, token);
        Long userId = claims.get("user_id", Long.class);
        if(userId == null || userMapper.selectUserById(userId) == null) return R.fail("用户不存在");
        if(!redisCache.hasKey("LoginUser:" + userId)) return R.fail("登陆状态已过期");
        checkEmail(email);
        Random random = new Random();
        int captcha = 100000 + random.nextInt(900000);
        String code = String.valueOf(captcha);
        redisCache.setCacheObject("emailCode:" + email,code,500, TimeUnit.MINUTES);
        return R.ok(code);
    }

    @Override
    public R<String> forget(String email, String code, String password, String repassword) {
        if(!password.equals(repassword)){
            return R.fail("两次输入密码不一致");
        }
        checkEmail(email);
        String cacheObject = redisCache.getCacheObject("emailCode:" + email);
        if (!code.equals(cacheObject)) {
            return R.fail("验证码错误");
        }
        userMapper.updatePasswordByEmail(email, password);
        return R.ok("ok");
    }

    @Override
    public R<String> updateInfo(String avatar, String nickname, String sex, String token) {
        Claims claims = parseToken(secret, token);
        Long userId = claims.get("user_id", Long.class);
        userMapper.updateUserInfoById(userId, avatar, nickname, sex);
        return R.ok("ok");
    }

    @Override
    public R<String> updatePassword(String opassword, String password, String repassword, String token) {
        Claims claims = parseToken(secret, token);
        Long userId = claims.get("user_id", Long.class);
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
        Claims claims = parseToken(secret, token);
        Long userId = claims.get("user_id", Long.class);
        User user = userMapper.getUserInfoById(userId);
        return R.ok(user);
    }

    /**
     * 登录前置校验
     * @param username 用户名
     * @param password 用户密码
     */
    public void loginPreCheck(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new UserPasswordNotMatchException();
        }
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

    public void checkEmail(String email) {
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.matches(regex)) {
            throw new RuntimeException("邮箱格式错误");
        }
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