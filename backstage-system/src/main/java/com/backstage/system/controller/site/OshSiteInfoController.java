package com.backstage.system.controller.site;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.BusinessType;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.site.OshSiteInfo;
import com.backstage.system.domain.site.OshSiteTag;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.service.common.OssService;
import com.backstage.system.service.site.IOshSiteInfoService;
import com.backstage.system.service.site.IOshSiteTagsService;
import com.backstage.system.service.site.impl.RemoteShellExecutor;
import com.backstage.system.service.site.impl.ScriptResult;
import com.backstage.system.service.user.IOshUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 内部网站信息 Controller
 *
 * @author backstage
 */
@Api(tags = "内部网站管理")
@RestController
@RequestMapping("/pc/site")
public class OshSiteInfoController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    IOshSiteInfoService oshSiteInfoService;

    @Autowired
    IOshSiteTagsService oshSiteTagsService;

    @Autowired
    OssService ossService;

    @Autowired
    IOshUserService oshUserService;

    /**
     * 查询网站列表
     */
    @Anonymous
    @ApiOperation("查询网站列表")
    @GetMapping("/list")
    public R<TableDataInfo> list(OshSiteInfo siteInfo) {
        List<OshSiteInfo> list = oshSiteInfoService.listSites(siteInfo);
        // 获取封面图片访问
        for (OshSiteInfo oshSiteInfo : list) {
            if (StringUtils.isNotEmpty(oshSiteInfo.getCover())) {
                // 30 分钟过期
                oshSiteInfo.setCover(ossService.getLimitedUrl(oshSiteInfo.getCover(), 30));
            }
        }
        Set<Long> siteIds = list.stream().map(OshSiteInfo::getId).collect(Collectors.toSet());
        Map<Long, List<OshSiteTag>> siteTagMap = oshSiteTagsService.getAllTag(siteIds)
                .stream()
                .collect(Collectors.groupingBy(OshSiteTag::getSiteId));
        for (OshSiteInfo oshSiteInfo : list) {
            oshSiteInfo.setTagList(siteTagMap.get(oshSiteInfo.getId()));
        }
        return R.ok(getDataTable(list));
    }

    @Anonymous
    @ApiOperation("获取网站详细信息")
    @GetMapping(value = "/{id}")
    public R<OshSiteInfo> getSiteInfo(@PathVariable Long id, @RequestParam(required = false, defaultValue = "false") Boolean needUrl) {
        OshSiteInfo siteInfo = oshSiteInfoService.getById(id);
        if (siteInfo == null) {
            return R.fail("网站不存在");
        }
        if (!needUrl) {
            siteInfo.setSiteUrl(null);
        }
        siteInfo.setTagList(oshSiteTagsService.getTagsBySiteId(id));
        return R.ok(siteInfo);
    }

    /**
     * 点击网站，统计网站使用
     */
    @Anonymous
    @ApiOperation("获取网站详细信息")
    @PostMapping(value = "/use/{id}")
    public R<OshSiteInfo> useSite(@PathVariable Long id) {
        OshSiteInfo siteInfo = oshSiteInfoService.getById(id);
        if (siteInfo == null) {
            return R.fail("网站不存在");
        }
        R<OshUser> userInfo = oshUserService.getUserInfo();
        oshSiteInfoService.insertUsage(siteInfo, userInfo.getData());
        return R.ok(siteInfo);
    }

    /**
     * 新增网站信息
     */
    @Anonymous
    @Log(title = "内部网站", businessType = BusinessType.INSERT)
    @ApiOperation("新增网站信息")
    @PostMapping
    public R<Void> add(@Validated @RequestBody OshSiteInfo siteInfo) {
        long currentUserId = ThreadLocalUtil.getCurrentUserId();
        siteInfo.setCreateBy(currentUserId);
        siteInfo.setCreateTime(LocalDateTime.now());
        siteInfo.setUpdateTime(LocalDateTime.now());
        siteInfo.setUpdateBy(currentUserId);
        siteInfo.setStatus(1);
        if (oshSiteInfoService.saveSiteInfo(siteInfo)) {
            // 保存标签
            if (siteInfo.getTagList() != null && !siteInfo.getTagList().isEmpty()) {
                oshSiteTagsService.saveSiteTags(siteInfo.getId(), siteInfo.getTagList(), currentUserId);
            }
            return R.ok();
        } else {
            return R.fail("新增失败");
        }
    }

    /**
     * 修改网站信息
     */
    @Anonymous
    @Log(title = "内部网站", businessType = BusinessType.UPDATE)
    @ApiOperation("修改网站信息")
    @PutMapping
    public R<Void> edit(@RequestBody OshSiteInfo siteInfo) {
        long currentUserId = ThreadLocalUtil.getCurrentUserId();
        siteInfo.setUpdateBy(currentUserId);
        siteInfo.setUpdateTime(LocalDateTime.now());
        if (oshSiteInfoService.updateSiteInfo(siteInfo)) {
            // 更新标签
            if (siteInfo.getTagList() != null) {
                oshSiteTagsService.saveSiteTags(siteInfo.getId(), siteInfo.getTagList(), currentUserId);
            }
            return R.ok();
        } else {
            return R.fail("修改失败");
        }
    }

    /**
     * 删除网站信息
     */
    @Anonymous
    @Log(title = "内部网站", businessType = BusinessType.DELETE)
    @ApiOperation("删除网站信息")
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        if (oshSiteInfoService.removeByIds(Arrays.asList(ids))) {
            return R.ok();
        } else {
            return R.fail("删除失败");
        }
    }

    /**
     * 检查网站连接状态
     */
    @Anonymous
    @ApiOperation("检查网站连接状态")
    @PostMapping("/check")
    public R<Boolean> checkSiteConnection(@RequestBody List<String> ids) {
        List<OshSiteInfo> oshSiteInfos = oshSiteInfoService.listByIds(ids.stream().map(Long::valueOf).collect(Collectors.toList()));
        if (CollectionUtils.isEmpty(oshSiteInfos)) {
            return R.fail("网站不存在");
        }
        return R.ok(oshSiteInfoService.testConnection(oshSiteInfos));
    }

    /**
     * 检查网站连接状态
     */
    @Anonymous
    @ApiOperation("检查网站连接状态")
    @PostMapping("/check-all")
    public R<List<OshSiteInfo>> checkAllSiteConnection() {
        List<OshSiteInfo> oshSiteInfos = oshSiteInfoService.list();
        if (CollectionUtils.isEmpty(oshSiteInfos)) {
            return R.fail("网站不存在");
        }
        oshSiteInfoService.testConnection(oshSiteInfos);
        return R.ok(oshSiteInfos);
    }

    /**
     * 启动演示站点后端服务
     * 1. 通过 SSH 在远程服务器后台执行启动脚本
     * 2. 定期执行健康检查脚本，轮询直到服务启动成功或超时
     */
    @Anonymous
    @ApiOperation("启动演示站点后端服务")
    @PostMapping("/demo/start/{id}")
    public R<Map<String, Object>> startDemoService(@PathVariable Long id) {
        OshSiteInfo siteInfo = oshSiteInfoService.getById(id);
        if (siteInfo == null) {
            return R.fail("网站不存在");
        }
        if (!"demo".equals(siteInfo.getSiteType())) {
            return R.fail("该网站不是演示站点");
        }
        Map<String, Object> config = siteInfo.getSiteConfig();
        if (config == null || config.isEmpty()) {
            return R.fail("演示站点未配置");
        }

        try {
            String backendHost = (String) config.get("backendHost");
            int backendPort = config.containsKey("backendPort") ? ((Number) config.get("backendPort")).intValue() : 22;
            String backendUser = (String) config.get("backendUser");
            String backendPassword = (String) config.get("backendPassword");
            String startupScript = (String) config.get("startupScript");
            String healthCheckScript = (String) config.get("healthCheckScript");
            String frontendUrl = (String) config.get("frontendUrl");
            String loginUsername = (String) config.get("loginUsername");
            String loginPassword = (String) config.get("loginPassword");

            // SSH 登录方式
            String loginMethod = (String) config.getOrDefault("loginMethod", "password");
            String privateKey = (String) config.get("privateKey");
            RemoteShellExecutor.AuthMethod authMethod = "privateKey".equals(loginMethod)
                    ? RemoteShellExecutor.AuthMethod.PRIVATE_KEY
                    : RemoteShellExecutor.AuthMethod.PASSWORD;
            String sshCredential = authMethod == RemoteShellExecutor.AuthMethod.PRIVATE_KEY ? privateKey : backendPassword;

            if (backendHost == null || backendUser == null) {
                return R.fail("SSH配置不完整，请检查后端服务器IP、用户名");
            }
            if (authMethod == RemoteShellExecutor.AuthMethod.PRIVATE_KEY && (privateKey == null || privateKey.isEmpty())) {
                return R.fail("登录方式为私钥，但私钥未配置");
            }
            if (authMethod == RemoteShellExecutor.AuthMethod.PASSWORD && (backendPassword == null || backendPassword.isEmpty())) {
                return R.fail("登录方式为密码，但密码未配置");
            }
            if (startupScript == null || startupScript.isEmpty()) {
                return R.fail("启动脚本未配置");
            }
            if (healthCheckScript == null || healthCheckScript.isEmpty()) {
                return R.fail("健康检查脚本未配置");
            }

            String pid = RemoteShellExecutor.executeScriptBackground(
                    backendHost, backendPort, backendUser, authMethod, sshCredential, startupScript);
            log.info("Demo startup script started, PID: {}, siteId: {}", pid, id);

            int maxRetries = 60;
            int retryIntervalSeconds = 5;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    Thread.sleep(retryIntervalSeconds * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                try {
                    ScriptResult checkResult = RemoteShellExecutor.executeScript(
                            backendHost, backendPort, backendUser, authMethod, sshCredential,
                            healthCheckScript, 15);

                    if (checkResult.isSuccess()) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("started", true);
                        result.put("frontendUrl", frontendUrl);
                        result.put("loginUsername", loginUsername);
                        result.put("loginPassword", loginPassword);
                        result.put("retries", i + 1);
                        result.put("healthCheckOutput", checkResult.getOutput());
                        log.info("Demo service started successfully after {} retries, siteId: {}", i + 1, id);
                        return R.ok(result, "服务启动成功");
                    }

                    log.debug("Health check attempt {} failed, exitCode: {}, siteId: {}",
                                    i + 1, checkResult.getExitCode(), id);
                } catch (Exception e) {
                    log.warn("Health check attempt {} error: {}, siteId: {}", i + 1, e.getMessage(), id);
                }
            }

            // 超时
            Map<String, Object> result = new HashMap<>();
            result.put("started", false);
            result.put("message", "服务启动超时（已等待 " + (maxRetries * retryIntervalSeconds) + " 秒），请手动检查");
            result.put("frontendUrl", frontendUrl);
            result.put("loginUsername", loginUsername);
            result.put("loginPassword", loginPassword);
            return R.ok(result, "启动脚本已执行，但健康检查未通过");

        } catch (Exception e) {
            log.error("启动演示服务失败", e);
            return R.fail("启动失败：" + e.getMessage());
        }
    }

    /**
     * 检查演示站点后端服务状态
     * 通过 SSH 在远程服务器执行健康检查脚本
     */
    @Anonymous
    @ApiOperation("检查演示站点后端服务状态")
    @PostMapping("/demo/check/{id}")
    public R<Map<String, Object>> checkDemoServiceStatus(@PathVariable Long id) {
        OshSiteInfo siteInfo = oshSiteInfoService.getById(id);
        if (siteInfo == null) {
            return R.fail("网站不存在");
        }
        if (!"demo".equals(siteInfo.getSiteType())) {
            return R.fail("该网站不是演示站点");
        }
        Map<String, Object> config = siteInfo.getSiteConfig();
        if (config == null || config.isEmpty()) {
            return R.fail("演示站点未配置");
        }

        try {
            String backendHost = (String) config.get("backendHost");
            int backendPort = config.containsKey("backendPort") ? ((Number) config.get("backendPort")).intValue() : 22;
            String backendUser = (String) config.get("backendUser");
            String backendPassword = (String) config.get("backendPassword");
            String healthCheckScript = (String) config.get("healthCheckScript");
            String frontendUrl = (String) config.get("frontendUrl");
            String loginUsername = (String) config.get("loginUsername");
            String loginPassword = (String) config.get("loginPassword");

            // SSH 登录方式
            String loginMethod = (String) config.getOrDefault("loginMethod", "password");
            String privateKey = (String) config.get("privateKey");
            RemoteShellExecutor.AuthMethod authMethod = "privateKey".equals(loginMethod)
                    ? RemoteShellExecutor.AuthMethod.PRIVATE_KEY
                    : RemoteShellExecutor.AuthMethod.PASSWORD;
            String sshCredential = authMethod == RemoteShellExecutor.AuthMethod.PRIVATE_KEY ? privateKey : backendPassword;

            if (healthCheckScript == null || healthCheckScript.isEmpty()) {
                return R.fail("健康检查脚本未配置");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("siteId", id);
            result.put("frontendUrl", frontendUrl);
            result.put("loginUsername", loginUsername);
            result.put("loginPassword", loginPassword);

            // 执行健康检查脚本
            if (backendHost != null && backendUser != null) {
                try {
                    ScriptResult checkResult = RemoteShellExecutor.executeScript(
                            backendHost, backendPort, backendUser, authMethod, sshCredential,
                            healthCheckScript, 15);
                    result.put("healthy", checkResult.isSuccess());
                    result.put("exitCode", checkResult.getExitCode());
                    result.put("output", checkResult.getOutput());
                } catch (Exception e) {
                    result.put("healthy", false);
                    result.put("error", e.getMessage());
                }
            } else {
                result.put("healthy", false);
                result.put("error", "SSH配置不完整");
            }

            return R.ok(result, "状态检查完成");
        } catch (Exception e) {
            log.error("检查演示服务状态失败", e);
            return R.fail("检查失败：" + e.getMessage());
        }
    }

    /**
     * 异步启动演示站点（调用方轮询此接口）。
     * 首次调用提交后台启动任务；后续调用返回当前任务状态。
     */
    @ApiOperation("异步启动演示站点（轮询接口）")
    @PostMapping("/demo/start-async/{id}")
    public R<Map<String, Object>> startDemoAsync(@PathVariable Long id) {
        try {
            Map<String, Object> result = oshSiteInfoService.startDemoAsync(id);
            return R.ok(result);
        } catch (IllegalArgumentException e) {
            return R.fail(e.getMessage());
        } catch (Exception e) {
            log.error("异步启动演示站点失败", e);
            return R.fail("启动失败：" + e.getMessage());
        }
    }

    /**
     * 停止演示站点服务
     * 通过 SSH 在远程服务器执行停止脚本
     */
    @Anonymous
    @ApiOperation("停止演示站点服务")
    @PostMapping("/demo/stop/{id}")
    public R<Map<String, Object>> stopDemoService(@PathVariable Long id) {
        OshSiteInfo siteInfo = oshSiteInfoService.getById(id);
        if (siteInfo == null) {
            return R.fail("网站不存在");
        }
        if (!"demo".equals(siteInfo.getSiteType())) {
            return R.fail("该网站不是演示站点");
        }
        Map<String, Object> config = siteInfo.getSiteConfig();
        if (config == null || config.isEmpty()) {
            return R.fail("演示站点未配置");
        }

        try {
            String backendHost = (String) config.get("backendHost");
            int backendPort = config.containsKey("backendPort") ? ((Number) config.get("backendPort")).intValue() : 22;
            String backendUser = (String) config.get("backendUser");
            String backendPassword = (String) config.get("backendPassword");
            String stopScript = (String) config.get("stopScript");

            // SSH 登录方式
            String loginMethod = (String) config.getOrDefault("loginMethod", "password");
            String privateKey = (String) config.get("privateKey");
            RemoteShellExecutor.AuthMethod authMethod = "privateKey".equals(loginMethod)
                    ? RemoteShellExecutor.AuthMethod.PRIVATE_KEY
                    : RemoteShellExecutor.AuthMethod.PASSWORD;
            String sshCredential = authMethod == RemoteShellExecutor.AuthMethod.PRIVATE_KEY ? privateKey : backendPassword;

            if (backendHost == null || backendUser == null) {
                return R.fail("SSH配置不完整");
            }
            if (stopScript == null || stopScript.isEmpty()) {
                return R.fail("停止脚本未配置");
            }

            ScriptResult stopResult = RemoteShellExecutor.executeScript(
                    backendHost, backendPort, backendUser, authMethod, sshCredential,
                    stopScript, 30);

            Map<String, Object> result = new HashMap<>();
            result.put("stopped", stopResult.isSuccess());
            result.put("exitCode", stopResult.getExitCode());
            result.put("output", stopResult.getOutput());

            if (stopResult.isSuccess()) {
                log.info("Demo service stopped successfully, siteId: {}", id);
                return R.ok(result, "服务已停止");
            } else {
                log.warn("Demo stop script exited with non-zero code: {}, siteId: {}", stopResult.getExitCode(), id);
                return R.ok(result, "停止脚本已执行（退出码: " + stopResult.getExitCode() + "）");
            }
        } catch (Exception e) {
            log.error("停止演示服务失败", e);
            return R.fail("停止失败：" + e.getMessage());
        }
    }
}
