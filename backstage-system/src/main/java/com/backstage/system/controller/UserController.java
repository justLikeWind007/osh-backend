package com.backstage.system.controller;

import com.backstage.common.core.domain.R;
import com.backstage.system.domain.user.User;
import com.backstage.system.domain.user.dto.*;
import com.backstage.system.domain.user.vo.UserRegisterVO;
import com.backstage.system.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/7
 * Time: 16:37
 */
@RestController
@RequestMapping("/pc/user")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public R<String> login(@RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO.getUsername(),userLoginDTO.getPassword());
    }

    @PostMapping("/reg")
    public R<UserRegisterVO> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return userService.register(userRegisterDTO.getUsername(),userRegisterDTO.getPassword(),userRegisterDTO.getRepassword());
    }

    @PostMapping("/logout")
    public R<String> logout() {
        // TODO
        return R.ok("退出成功");
    }

    @PostMapping("/get_captcha")
    public R<String> getCode(@RequestBody String phone) {
        // TODO
        return R.ok("123456");
    }

    @PostMapping("/bind_mobile")
    public R<String> bindMobile(@RequestBody String phone, @RequestBody String code) {
        // TODO
        return R.ok("18124912371");
    }

    @PostMapping("/forget")
    public R<String> forget(@RequestBody ForgetDTO forgetDTO) {
        return userService.forget(forgetDTO.getPhone(),forgetDTO.getCode(),forgetDTO.getPassword(),forgetDTO.getRepassword());
    }

    @PostMapping("/update_info")
    public R<String> updateInfo(@RequestBody UserUpdateInfoDTO userUpdateInfoDTO, HttpServletRequest request) {
        String token = request.getHeader("token");
        return userService.updateInfo(userUpdateInfoDTO.getAvatar(),userUpdateInfoDTO.getNickname(),userUpdateInfoDTO.getSex(), token);
    }

    @PostMapping("/update_password")
    public R<String> updatePassword(@RequestBody UserPasswordDTO userPasswordDTO, HttpServletRequest request) {
        String token = request.getHeader("token");
        return userService.updatePassword(userPasswordDTO.getOpassword(),userPasswordDTO.getPassword(),userPasswordDTO.getRepassword(), token);
    }

    @GetMapping("/getinfo")
    public R<User> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        return userService.getUserInfo(token);
    }
}