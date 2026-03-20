package com.backstage.system.controller.user;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.BusinessType;
import com.backstage.common.utils.poi.ExcelUtil;
import com.backstage.system.domain.user.User;
import com.backstage.system.domain.user.dto.*;
import com.backstage.system.domain.user.vo.UserLoginVo;
import com.backstage.system.service.IUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/7
 * Time: 16:37
 */
@RestController
@RequestMapping("/pc/user")
public class UserController extends BaseController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Anonymous
    @ApiOperation("账号登录")
    @PostMapping("/login")
    public R<UserLoginVo> login(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO.getName(),userLoginDTO.getPid());
    }

    @Anonymous
    @ApiOperation("注册请求")
    @PostMapping("/register/submit")
    public R<String> registerSubmit(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserRegisterDTO userRegisterDTO) throws MessagingException {
        return userService.registerSubmit(userRegisterDTO.getUsername(),userRegisterDTO.getPassword(),userRegisterDTO.getRepassword(),userRegisterDTO.getEmail());
    }

    @Anonymous
    @ApiOperation("账号注册")
    @PostMapping("/register/verity")
    public R<String> registerVerity(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("用户的唯一标识") @RequestParam(value = "uniqueId") String uniqueId) {
        return userService.registerVerity(uniqueId);
    }

    @Anonymous
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public R<String> logout(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("token") @RequestHeader(value = "token") String token) {
        return userService.logout(token);
    }

    @Anonymous
    @ApiOperation("改绑邮箱请求 根据唯一标识改绑邮箱")
    @PostMapping("/changeEmail/submit")
    public R<String> changeEmailSubmit(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("token") @RequestHeader(value = "token") String token,
            @ApiParam("用户的唯一标识") @RequestParam(value = "uniqueId") String uniqueId,
            @ApiParam("新的邮箱账号") @RequestParam(value = "newEmail") String newEmail) throws MessagingException {
        return userService.changeEmailSubmit(token, uniqueId, newEmail);
    }

    @Anonymous
    @ApiOperation("改绑邮箱验证")
    @PostMapping("/changeEmail/verity")
    public R<String> changeEmailVerity(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("token") @RequestHeader(value = "token") String token,
            @ApiParam("用户的唯一标识") @RequestParam(value = "uniqueId") String uniqueId) {
        return userService.changeEmailVerity(token, uniqueId);
    }

    //todo
    @Anonymous
    @ApiOperation("找回密码")
    @PostMapping("/forget")
    public R<String> forget(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("token") @RequestHeader(value = "token") String token,
            @RequestBody ForgetDTO forgetDTO) {
        return userService.forget(token,forgetDTO.getUniqueId(),forgetDTO.getPassword(),forgetDTO.getRepassword());
    }

    @Anonymous
    @ApiOperation("修改资料")
    @PostMapping("/update_info")
    public R<String> updateInfo(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserUpdateInfoDTO userUpdateInfoDTO,
            @ApiParam("token") @RequestHeader(value = "token") String token) {
        return userService.updateInfo(userUpdateInfoDTO.getAvatar(),userUpdateInfoDTO.getNickname(),userUpdateInfoDTO.getSex(), token);
    }

    @Anonymous
    @ApiOperation("修改密码")
    @PostMapping("/update_password")
    public R<String> updatePassword(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserPasswordDTO userPasswordDTO,
            @ApiParam("token") @RequestHeader(value = "token") String token) {
        return userService.updatePassword(userPasswordDTO.getOpassword(),userPasswordDTO.getPassword(),userPasswordDTO.getRepassword(), token);
    }

    @Anonymous
    @ApiOperation("获取用户信息")
    @GetMapping("/getinfo")
    public R<User> getUserInfo(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("token") @RequestHeader(value = "token") String token) {
        return userService.getUserInfo(token);
    }





    /**
     * 查询用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(User user)
    {
        startPage();
        List<User> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    /**
     * 导出用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @Log(title = "用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, User user)
    {
        List<User> list = userService.selectUserList(user);
        ExcelUtil<User> util = new ExcelUtil<User>(User.class);
        util.exportExcel(response, list, "用户数据");
    }

    /**
     * 获取用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(userService.selectUserById(id));
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody User user)
    {
        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody User user)
    {
        return toAjax(userService.updateUser(user));
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(userService.deleteUserByIds(ids));
    }
}