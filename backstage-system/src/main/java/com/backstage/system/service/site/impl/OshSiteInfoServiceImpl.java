package com.backstage.system.service.site.impl;

import com.alibaba.fastjson2.JSON;
import com.backstage.common.async.AsyncExecutorNames;
import com.backstage.common.enums.SiteTypeEnum;
import com.backstage.system.domain.site.OshSiteInfo;
import com.backstage.system.domain.site.OshSiteMaintainer;
import com.backstage.system.domain.site.OshSiteUsage;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.site.OshSiteInfoMapper;
import com.backstage.system.mapper.site.OshSiteMaintainerMapper;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.service.site.IOshSiteInfoService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 内部网站信息 Service 业务层处理
 *
 * @author backstage
 */
@Service
public class OshSiteInfoServiceImpl extends ServiceImpl<OshSiteInfoMapper, OshSiteInfo> implements IOshSiteInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(OshSiteInfoServiceImpl.class);

    @Autowired
    private OshSiteInfoMapper oshSiteInfoMapper;

    @Autowired
    private OshSiteMaintainerMapper oshSiteMaintainerMapper;

    @Autowired
    private OshUserMapper userMapper;

    @Value(value = "${backstage.site.test.maxConnectionTimeout:5000}")
    private int maxConnectionTimeout;

    @Value("${email.from}")
    private String from;

    @Resource(name = AsyncExecutorNames.NOTIFICATION)
    private Executor notificationTaskExecutor;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    @Qualifier(value = AsyncExecutorNames.DEFAULT)
    ThreadPoolTaskExecutor executor;

    /**
     * 新增网站使用记录
     *
     * @param siteInfo 网站信息
     * @return 结果
     */
    @Override
    public int insertUsage(OshSiteInfo siteInfo, OshUser oshUser) {
        OshSiteUsage oshSiteUsage = new OshSiteUsage();
        oshSiteUsage.setSiteId(siteInfo.getId());
        oshSiteUsage.setUserId(oshUser.getId());
        oshSiteUsage.setDeleted(false);
        return oshSiteInfoMapper.insertUsage(oshSiteUsage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSiteInfo(OshSiteInfo siteInfo) {
        save(siteInfo);
        return saveMaintainers(siteInfo.getId(), siteInfo.getMaintainerUserIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSiteInfo(OshSiteInfo siteInfo) {
        updateById(siteInfo);
        removeAllMaintainers(siteInfo.getId());
        saveMaintainers(siteInfo.getId(), siteInfo.getMaintainerUserIds());
        return true;
    }

    private void removeAllMaintainers(Long siteId) {
        oshSiteMaintainerMapper.update(Wrappers.<OshSiteMaintainer>lambdaUpdate()
                .set(OshSiteMaintainer::getDeleteFlag, 1)
                .eq(OshSiteMaintainer::getSiteId, siteId)
                .eq(OshSiteMaintainer::getDeleteFlag, 0));
    }

    private boolean saveMaintainers(Long siteId, List<String> maintainerUserIds) {
        List<OshSiteMaintainer> maintainers = new ArrayList<>();
        for (String maintainerUserId : maintainerUserIds) {
            OshSiteMaintainer maintainer = new OshSiteMaintainer();
            maintainer.setSiteId(siteId);
            maintainer.setUserId(Long.valueOf(maintainerUserId));
            maintainer.setDeleted(false);
            maintainers.add(maintainer);
        }
        return Db.saveBatch(maintainers);
    }

    @Override
    public List<OshSiteInfo> listSites(OshSiteInfo siteInfo) {
        List<OshSiteInfo> list = this.lambdaQuery().select(
                        OshSiteInfo::getId,
                        OshSiteInfo::getSiteName,
                        OshSiteInfo::getCover,
                        OshSiteInfo::getDescription,
                        OshSiteInfo::getStatus,
                        OshSiteInfo::getLastCheckTime,
                        OshSiteInfo::getSiteType,
                        OshSiteInfo::getSiteConfig
                ).like(StringUtils.isNoneBlank(siteInfo.getSiteName()), OshSiteInfo::getSiteName, siteInfo.getSiteName())
                .eq(siteInfo.getStatus() != null, OshSiteInfo::getStatus, siteInfo.getStatus())
                .eq(StringUtils.isNoneBlank(siteInfo.getSiteType()), OshSiteInfo::getSiteType, siteInfo.getSiteType())
                .list();
        // 填充网站类型名称
        for (OshSiteInfo info : list) {
            SiteTypeEnum siteTypeEnum = SiteTypeEnum.fromCode(info.getSiteType());
            info.setSiteTypeName(siteTypeEnum != null ? siteTypeEnum.getName() : null);
        }
        setMaintainers(list);
        return list;
    }

    private void setMaintainers(List<OshSiteInfo> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Set<Long> siteIds = list.stream().map(OshSiteInfo::getId).collect(Collectors.toSet());
        Map<Long, List<OshSiteMaintainer>> maintainersMap = oshSiteInfoMapper.selectMaintainersBySiteIds(siteIds).stream().collect(Collectors.groupingBy(OshSiteMaintainer::getSiteId));

        for (OshSiteInfo oshSiteInfo : list) {
            List<OshSiteMaintainer> maintainers = maintainersMap.getOrDefault(oshSiteInfo.getId(), Collections.emptyList());
            oshSiteInfo.setMaintainerUserIds(maintainers.stream().map(OshSiteMaintainer::getUserId).map(String::valueOf).collect(Collectors.toList()));
            oshSiteInfo.setMaintainers(maintainers);
        }
    }

    /**
     * 每10分钟测试1次
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void testSiteInfoResponseStatus() {
        try {
            List<OshSiteInfo> oshSiteInfos = oshSiteInfoMapper.selectList(Wrappers.<OshSiteInfo>lambdaQuery().eq(OshSiteInfo::getDeleteFlag, 0));
            List<OshSiteInfo> siteList = checkConnectionStatus(oshSiteInfos, maxConnectionTimeout);
            updateBatchById(siteList);
            List<OshSiteInfo> failedList = siteList.stream().filter(site -> site.getStatus() == 0).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(failedList)) {
                sendEmail(failedList);
            }
        } catch (Throwable throwable) {
            log.error("测试网站状态异常", throwable);
        }
    }

    private void sendEmail(List<OshSiteInfo> failedList) {
        List<Long> siteIds = failedList.stream().map(OshSiteInfo::getId).collect(Collectors.toList());
        MultiValueMap<Long, OshSiteMaintainer> siteMaintainers = getSiteMaintainers(siteIds);
        MultiValueMap<Long, OshSiteInfo> userIdSiteMap = new LinkedMultiValueMap<>();
        for (OshSiteInfo oshSiteInfo : failedList) {
            List<OshSiteMaintainer> maintainers = siteMaintainers.get(oshSiteInfo.getId());
            if (CollectionUtils.isEmpty(maintainers)) {
                continue;
            }
            for (OshSiteMaintainer maintainer : maintainers) {
                Long userId = maintainer.getUserId();
                userIdSiteMap.add(userId, oshSiteInfo);
            }
        }
        if (CollectionUtils.isEmpty(userIdSiteMap)) {
            return;
        }
        final Map<Long, OshUser> userMap = userMapper.selectList(Wrappers.<OshUser>lambdaQuery()
                        .in(OshUser::getId, userIdSiteMap.keySet()))
                .stream()
                .collect(Collectors.toMap(OshUser::getId, Function.identity()));
        if (CollectionUtils.isEmpty(userMap)) {
            return;
        }
        for (Map.Entry<Long, List<OshSiteInfo>> entry : userIdSiteMap.entrySet()) {
            Long userId = entry.getKey();
            OshUser user = userMap.get(userId);
            StringBuilder emailContent = new StringBuilder("用户" + user.getUsername() + "，您负责的如下网站有异常，请及时处理：\n");
            int i = 1;
            for (OshSiteInfo site : entry.getValue()) {
                emailContent.append(i++).append("：");
                emailContent.append(site.getSiteName()).append("：").append(site.getSiteUrl()).append("\n");
            }
            CompletableFuture.runAsync(() -> {
                try {
                    sendEmail("网站异常通知", from, emailContent.toString(), user.getEmail());
                } catch (MessagingException e) {
                    LOG.info("发送网站异常通知邮件成功: {}", user.getEmail());
                }
            }, notificationTaskExecutor).whenComplete((v, t) -> LOG.info("发送网站异常通知邮件成功 {}", user.getEmail()));
        }
    }

    private void sendEmail(String subject, String from, String content, String receiver) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);
        // 设置邮件内容
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, false);
        mimeMessageHelper.setTo(receiver);
        mimeMessageHelper.setFrom(from);
        javaMailSender.send(mimeMessage);
    }

    @Override
    public MultiValueMap<Long, OshSiteMaintainer> getSiteMaintainers(Collection<Long> siteIds) {
        List<OshSiteMaintainer> maintainers = oshSiteMaintainerMapper.selectList(Wrappers.<OshSiteMaintainer>lambdaQuery()
                .eq(OshSiteMaintainer::getDeleteFlag, 0)
                .in(CollectionUtils.isNotEmpty(siteIds), OshSiteMaintainer::getSiteId, siteIds));
        MultiValueMap<Long, OshSiteMaintainer> siteMaintainers = new LinkedMultiValueMap<>();
        for (OshSiteMaintainer maintainer : maintainers) {
            siteMaintainers.add(maintainer.getSiteId(), maintainer);
        }
        return siteMaintainers;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean testConnection(List<OshSiteInfo> oshSiteInfos) {
        return testConnection(oshSiteInfos, maxConnectionTimeout);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean testConnection(List<OshSiteInfo> oshSiteInfos, int timeout) {
        List<OshSiteInfo> siteList = checkConnectionStatus(oshSiteInfos, timeout);
        updateBatchById(siteList);
        return true;
    }

    @Override
    public List<OshSiteInfo> checkConnectionStatus(List<OshSiteInfo> oshSiteInfos, int timeout) {
        for (OshSiteInfo siteInfo : oshSiteInfos) {
            if (StringUtils.isBlank(siteInfo.getSiteUrl())) {
                continue;
            }
            siteInfo.setLastCheckStatus(siteInfo.getStatus());
            try {
                boolean connectionOk = testUrlConnection(siteInfo.getSiteUrl(), timeout, timeout);
                siteInfo.setStatus(connectionOk ? 1 : 0);
            } catch (IOException exception) {
                siteInfo.setStatus(0);
            } catch (Throwable throwable) {
                LOG.error("failed to test site connection, {}", JSON.toJSONString(siteInfo));
            }
            siteInfo.setLastCheckTime(new Date());
        }
        return oshSiteInfos;
    }

    /**
     * 测试 URL 是否可以正常访问
     *
     * @param urlStr         要测试的地址
     * @param connectTimeout 连接超时时间（毫秒）
     * @param readTimeout    读取超时时间（毫秒）
     * @return true=能连通，false=访问不通/超时/异常
     */
    private static boolean testUrlConnection(String urlStr, int connectTimeout, int readTimeout) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            // 设置请求方式（HEAD 比 GET 更快，只拿响应头不拿内容）
            conn.setRequestMethod("HEAD");
            // 设置超时时间
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            // 获取响应码，200 ~ 399 都算正常连通
            int responseCode = conn.getResponseCode();
            return responseCode >= 200 && responseCode < 400;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // ==================== 异步启动演示站点 ====================

    private static final org.slf4j.Logger demoStartLogger =
            LoggerFactory.getLogger(OshSiteInfoServiceImpl.class);

    private final ConcurrentHashMap<Long, DemoStartTask> demoStartTasks = new ConcurrentHashMap<>();

    @Override
    public Map<String, Object> startDemoAsync(Long siteId) {
        DemoSiteConfig cfg = loadDemoSiteConfig(siteId);

        // already validated by loadDemoSiteConfig — just check scripts
        requireConfigField("启动脚本", cfg.getStartupScript());
        requireConfigField("健康检查脚本", cfg.getHealthCheckScript());

        // 已有进行中的任务 → 直接返回当前状态
        DemoStartTask existing = demoStartTasks.get(siteId);
        if (existing != null && existing.isInProgress()) {
            return existing.toResultMap();
        }

        DemoStartTask task = new DemoStartTask(
                cfg.getSiteId(), cfg.getFrontendUrl(), cfg.getLoginUsername(), cfg.getLoginPassword());
        demoStartTasks.put(siteId, task);

        CompletableFuture.runAsync(() -> {
            executeAsyncStart(task, cfg.getBackendHost(), cfg.getBackendPort(), cfg.getBackendUser(),
                    cfg.getAuthMethod(), cfg.getSshCredential(),
                    cfg.getStartupScript(), cfg.getHealthCheckScript());
        }, executor);
        return task.toResultMap();
    }

    private void executeAsyncStart(DemoStartTask task, String host, int port, String user,
                                   RemoteShellExecutor.AuthMethod authMethod, String sshCredential,
                                   String startupScript, String healthCheckScript) {
        try {
            String pid = RemoteShellExecutor.executeScriptBackground(
                    host, port, user, authMethod, sshCredential, startupScript);
            task.updateStarting(pid);
            demoStartLogger.info("Async demo startup: script submitted, PID: {}, siteId: {}", pid, task.siteId);

            int maxRetries = 100;
            int intervalSeconds = 3;
            for (int i = 0; i < maxRetries; i++) {
                Thread.sleep(intervalSeconds * 1000L);
                try {
                    ScriptResult checkResult = RemoteShellExecutor.executeScript(
                            host, port, user, authMethod, sshCredential, healthCheckScript, 15);
                    task.updateCheck(i + 1, checkResult.getOutput());

                    if (checkResult.isSuccess()) {
                        task.updateRunning(checkResult.getOutput());
                        demoStartLogger.info("Async demo startup SUCCESS after {} checks, siteId: {}", i + 1, task.siteId);
                        scheduleTaskCleanup(task.siteId, 10 * 60 * 1000L);
                        return;
                    }
                } catch (Exception e) {
                    demoStartLogger.warn("Async demo check {} error for siteId {}: {}", i + 1, task.siteId, e.getMessage());
                }
            }

            task.updateTimeout(maxRetries, intervalSeconds);
            demoStartLogger.warn("Async demo startup TIMEOUT after {} attempts, siteId: {}", maxRetries, task.siteId);
            scheduleTaskCleanup(task.siteId, 10 * 60 * 1000L);

        } catch (Exception e) {
            task.updateFailed("启动异常：" + e.getMessage());
            demoStartLogger.error("Async demo startup FAILED for siteId: {}", task.siteId, e);
            scheduleTaskCleanup(task.siteId, 10 * 60 * 1000L);
        }
    }

    private void scheduleTaskCleanup(Long siteId, long delayMs) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            demoStartTasks.remove(siteId);
        }, "demo-cleanup-" + siteId).start();
    }

    // ==================== 演示站点公共方法 ====================

    @Override
    public DemoSiteConfig loadDemoSiteConfig(Long siteId) {
        OshSiteInfo siteInfo = getById(siteId);
        if (siteInfo == null) {
            throw new IllegalArgumentException("网站不存在");
        }
        if (!SiteTypeEnum.DEMO.getCode().equals(siteInfo.getSiteType())) {
            throw new IllegalArgumentException("该网站不是演示站点");
        }
        Map<String, Object> config = siteInfo.getSiteConfig();
        if (config == null || config.isEmpty()) {
            throw new IllegalArgumentException("演示站点未配置");
        }

        DemoSiteConfig cfg = DemoSiteConfig.from(siteInfo);

        // validate SSH connection params
        if (cfg.getBackendHost() == null || cfg.getBackendUser() == null) {
            throw new IllegalArgumentException("SSH配置不完整，请检查后端服务器IP、用户名");
        }
        if (cfg.getAuthMethod() == RemoteShellExecutor.AuthMethod.PRIVATE_KEY
                && (cfg.getPrivateKey() == null || cfg.getPrivateKey().isEmpty())) {
            throw new IllegalArgumentException("登录方式为私钥，但私钥未配置");
        }
        if (cfg.getAuthMethod() == RemoteShellExecutor.AuthMethod.PASSWORD
                && (cfg.getBackendPassword() == null || cfg.getBackendPassword().isEmpty())) {
            throw new IllegalArgumentException("登录方式为密码，但密码未配置");
        }
        return cfg;
    }

    @Override
    public void requireConfigField(String fieldName, String fieldValue) {
        if (fieldValue == null || fieldValue.isEmpty()) {
            throw new IllegalArgumentException(fieldName + "未配置");
        }
    }

    @Override
    public Map<String, Object> startDemo(DemoSiteConfig cfg) {
        String pid;
        try {
            pid = RemoteShellExecutor.executeScriptBackground(
                    cfg.getBackendHost(), cfg.getBackendPort(), cfg.getBackendUser(),
                    cfg.getAuthMethod(), cfg.getSshCredential(), cfg.getStartupScript());
        } catch (Exception e) {
            throw new RuntimeException("SSH执行启动脚本失败：" + e.getMessage(), e);
        }
        LOG.info("Demo startup script started, PID: {}, siteId: {}", pid, cfg.getSiteId());

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
                        cfg.getBackendHost(), cfg.getBackendPort(), cfg.getBackendUser(),
                        cfg.getAuthMethod(), cfg.getSshCredential(),
                        cfg.getHealthCheckScript(), 15);

                if (checkResult.isSuccess()) {
                    Map<String, Object> result = new HashMap<>();
                    result.put("started", true);
                    result.put("frontendUrl", cfg.getFrontendUrl());
                    result.put("loginUsername", cfg.getLoginUsername());
                    result.put("loginPassword", cfg.getLoginPassword());
                    result.put("retries", i + 1);
                    result.put("healthCheckOutput", checkResult.getOutput());
                    LOG.info("Demo service started successfully after {} retries, siteId: {}", i + 1, cfg.getSiteId());
                    return result;
                }

                LOG.debug("Health check attempt {} failed, exitCode: {}, siteId: {}",
                        i + 1, checkResult.getExitCode(), cfg.getSiteId());
            } catch (Exception e) {
                LOG.warn("Health check attempt {} error: {}, siteId: {}", i + 1, e.getMessage(), cfg.getSiteId());
            }
        }

        // timeout
        Map<String, Object> result = new HashMap<>();
        result.put("started", false);
        result.put("message", "服务启动超时（已等待 " + (maxRetries * retryIntervalSeconds) + " 秒），请手动检查");
        result.put("frontendUrl", cfg.getFrontendUrl());
        result.put("loginUsername", cfg.getLoginUsername());
        result.put("loginPassword", cfg.getLoginPassword());
        return result;
    }

    @Override
    public Map<String, Object> checkDemo(DemoSiteConfig cfg) {
        Map<String, Object> result = new HashMap<>();
        result.put("siteId", cfg.getSiteId());
        result.put("frontendUrl", cfg.getFrontendUrl());
        result.put("loginUsername", cfg.getLoginUsername());
        result.put("loginPassword", cfg.getLoginPassword());

        try {
            ScriptResult checkResult = RemoteShellExecutor.executeScript(
                    cfg.getBackendHost(), cfg.getBackendPort(), cfg.getBackendUser(),
                    cfg.getAuthMethod(), cfg.getSshCredential(),
                    cfg.getHealthCheckScript(), 15);
            result.put("healthy", checkResult.isSuccess());
            result.put("exitCode", checkResult.getExitCode());
            result.put("output", checkResult.getOutput());
        } catch (Exception e) {
            result.put("healthy", false);
            result.put("error", e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> stopDemo(DemoSiteConfig cfg) {
        ScriptResult stopResult;
        try {
            stopResult = RemoteShellExecutor.executeScript(
                    cfg.getBackendHost(), cfg.getBackendPort(), cfg.getBackendUser(),
                    cfg.getAuthMethod(), cfg.getSshCredential(),
                    cfg.getStopScript(), 30);
        } catch (Exception e) {
            throw new RuntimeException("SSH执行停止脚本失败：" + e.getMessage(), e);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("stopped", stopResult.isSuccess());
        result.put("exitCode", stopResult.getExitCode());
        result.put("output", stopResult.getOutput());

        if (stopResult.isSuccess()) {
            LOG.info("Demo service stopped successfully, siteId: {}", cfg.getSiteId());
        } else {
            LOG.warn("Demo stop script exited with non-zero code: {}, siteId: {}", stopResult.getExitCode(), cfg.getSiteId());
        }
        return result;
    }

}
