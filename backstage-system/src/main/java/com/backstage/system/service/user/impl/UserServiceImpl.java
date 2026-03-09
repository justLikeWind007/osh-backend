package com.backstage.system.service.user.impl;

import com.backstage.common.constant.UserConstants;
import com.backstage.common.core.domain.R;
import com.backstage.common.exception.user.UserNotExistsException;
import com.backstage.common.exception.user.UserPasswordNotMatchException;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.user.User;
import com.backstage.system.domain.user.vo.UserRegisterVO;
import com.backstage.system.mapper.user.UserMapper;
import com.backstage.system.service.user.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/7
 * Time: 16:42
 */
@Service
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public R<String> login(String username, String password) {
        loginPreCheck(username, password);
        User user = userMapper.getUserByUsername(username);
        if (user == null) {
            return R.fail("用户被拉黑");
        }
        if (!user.getPassword().equals(password)) {
            return R.fail("密码错误");
        }
        // 生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("username", username);
        claims.put("password", password);
        System.out.println(secret);
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        return R.ok(token);
    }

    @Override
    public R<UserRegisterVO> register(String username, String password, String repassword) {
        if(!password.equals(repassword)){
            return R.fail("两次输入密码不一致");
        }
        User user = userMapper.getUserByUsername(username);
        if (user != null && user.getUsername().equals(username)) {
            return R.fail("用户已存在");
        }
        userMapper.register(username, password);
        User newUser = userMapper.getUserByUsername(username);
        UserRegisterVO userRegisterVO = new UserRegisterVO();
        BeanUtils.copyProperties(newUser, userRegisterVO);
        return R.ok(userRegisterVO);
    }

    @Override
    public R<String> forget(String phone, String code, String password, String repassword) {
        // TODO:校验手机号格式
        // TODO:验证码校验并获取用户id
        if(!password.equals(repassword)){
            return R.fail("两次输入密码不一致");
        }
        Long userId = 1L;
        userMapper.updatePasswordById(userId, password);
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
}