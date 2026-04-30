package com.backstage.system.controller.user;

import com.backstage.common.annotation.OshUserLevel;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.domain.user.dto.*;
import com.backstage.system.domain.user.vo.OshUserLoginVO;
import com.backstage.system.request.UserListRequest;
import com.backstage.system.service.user.IOshUserService;
import com.backstage.system.utils.UserContextUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/7
 * Time: 16:37
 */
@RestController
@RequestMapping("/pc/user")
public class OshUserController extends BaseController {

    private final IOshUserService userService;

    @Autowired
    public OshUserController(IOshUserService userService) {
        this.userService = userService;
    }

    @ApiOperation("账号登录")
    @PostMapping("/login")
    public R<OshUserLoginVO> login(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserLoginDTO userLoginDTO) {
        return userService.login(userLoginDTO.getUsername(),userLoginDTO.getPassword());
    }

    @ApiOperation("注册请求")
    @PostMapping("/register/submit")
    public R<String> registerSubmit(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserRegisterDTO userRegisterDTO) throws MessagingException {
        return userService.registerSubmit(userRegisterDTO.getUsername(),userRegisterDTO.getPassword(),userRegisterDTO.getRepassword(),userRegisterDTO.getEmail());
    }

    @ApiOperation("账号注册")
    @PostMapping("/register/verity")
    public R<String> registerVerity(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("用户的唯一标识") @RequestBody VerityRequestDTO verityRequestDTO) {
        return userService.registerVerity(verityRequestDTO.getUniqueId());
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('user:logout')")
    public R<String> logout(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid) {
        return userService.logout();
    }

    @ApiOperation("改绑邮箱请求 根据唯一标识改绑邮箱")
    @PostMapping("/changeEmail/submit")
    @PreAuthorize("hasAuthority('user:email:change:submit')")
    public R<String> changeEmailSubmit(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserChangeEmailDTO userChangeEmailDTO) throws MessagingException {
        return userService.changeEmailSubmit(userChangeEmailDTO.getUniqueId(), userChangeEmailDTO.getNewEmail());
    }

    @ApiOperation("改绑邮箱验证")
    @PostMapping("/changeEmail/verity")
    @PreAuthorize("hasAuthority('user:email:change:verity')")
    public R<String> changeEmailVerity(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @ApiParam("用户的唯一标识") @RequestBody VerityRequestDTO verityRequestDTO) {
        return userService.changeEmailVerity(verityRequestDTO.getUniqueId());
    }

    @ApiOperation("找回密码")
    @PostMapping("/forget")
    @PreAuthorize("hasAuthority('user:password:forget')")
    public R<String> forget(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserForgetDTO userForgetDTO) {
        return userService.forget(userForgetDTO.getUniqueId(), userForgetDTO.getPassword(), userForgetDTO.getRepassword());
    }

    @ApiOperation("修改资料")
    @PostMapping("/update_info")
    @PreAuthorize("hasAuthority('user:info:update')")
    public R<String> updateInfo(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserUpdateInfoDTO userUpdateInfoDTO) {
        return userService.updateInfo(userUpdateInfoDTO.getAvatar(),userUpdateInfoDTO.getNickname(),userUpdateInfoDTO.getSex());
    }

    @ApiOperation("修改密码")
    @PostMapping("/update_password")
    @PreAuthorize("hasAuthority('user:password:update')")
    public R<String> updatePassword(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserPasswordDTO userPasswordDTO) {
        return userService.updatePassword(userPasswordDTO.getOpassword(),userPasswordDTO.getPassword(),userPasswordDTO.getRepassword());
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/getinfo")
    public R<OshUser> getUserInfo(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid) {
        return userService.getUserInfo();
    }

    @ApiOperation("注销用户")
    @PostMapping("/deleteUser")
    @PreAuthorize("hasAuthority('user:delete')")
    public R<String> deleteUser(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid) {
        return userService.deleteUser();
    }

    @PostMapping("/violation/record")
    @PreAuthorize("hasAuthority('user:violation:record')")
    public R<String> record(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserRecordDTO userRecordDTO) {
        return userService.record(userRecordDTO.getUserId(), userRecordDTO.getViolationType(), userRecordDTO.getReason());
    }

    @PostMapping("/violation/record/cancel")
    @PreAuthorize("hasAuthority('user:violation:record:cancel')")
    public R<String> cancelRecord(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserCancelRecordDTO userCancelRecordDTO) {
        return userService.cancelRecord(userCancelRecordDTO.getUserId(), UserContextUtil.getCurrentUser());
    }

    @PostMapping("/asset/update")
    @PreAuthorize("hasAuthority('user:asset:update')")
    public R<String> updateAsset(
            @ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid,
            @RequestBody UserAssetDTO userAssetDTO) {
        return userService.updateAsset(userAssetDTO.getChangeType(), userAssetDTO.getChangeSource(), userAssetDTO.getAssetType(), userAssetDTO.getChangeAmount(), userAssetDTO.getRemark());
    }

    /**
     * 查询用户列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('user:list')")
    @OshUserLevel(value = 5)
    public R<List<OshUser>> list(UserListRequest req) {
        return R.ok(userService.selectUserList(req));
    }
//
//    /**
//     * 导出用户列表
//     */
//    @PreAuthorize("@ss.hasPermi('system:user:export')")
//    @Log(title = "用户", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, OshUser oshUser)
//    {
//        List<OshUser> list = userService.selectUserList(oshUser);
//        ExcelUtil<OshUser> util = new ExcelUtil<OshUser>(OshUser.class);
//        util.exportExcel(response, list, "用户数据");
//    }
//
//    /**
//     * 获取用户详细信息
//     */
//    @PreAuthorize("@ss.hasPermi('system:user:query')")
//    @GetMapping(value = "/{id}")
//    public AjaxResult getInfo(@PathVariable("id") Long id)
//    {
//        return success(userService.selectUserById(id));
//    }
//
//    /**
//     * 新增用户
//     */
//    @PreAuthorize("@ss.hasPermi('system:user:add')")
//    @Log(title = "用户", businessType = BusinessType.INSERT)
//    @PostMapping
//    public AjaxResult add(@RequestBody OshUser oshUser)
//    {
//        return toAjax(userService.insertUser(oshUser));
//    }
//
//    /**
//     * 修改用户
//     */
//    @PreAuthorize("@ss.hasPermi('system:user:edit')")
//    @Log(title = "用户", businessType = BusinessType.UPDATE)
//    @PutMapping
//    public AjaxResult edit(@RequestBody OshUser oshUser)
//    {
//        return toAjax(userService.updateUser(oshUser));
//    }
//
//    /**
//     * 删除用户
//     */
//    @PreAuthorize("@ss.hasPermi('system:user:remove')")
//    @Log(title = "用户", businessType = BusinessType.DELETE)
//    @DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable Long[] ids)
//    {
//        return toAjax(userService.deleteUserByIds(ids));
//    }
}