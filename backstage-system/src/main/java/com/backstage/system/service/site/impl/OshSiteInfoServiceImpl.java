package com.backstage.system.service.site.impl;

import com.alibaba.fastjson2.JSON;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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

    @Autowired
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private JavaMailSender javaMailSender;

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
        return saveMaintainers(siteInfo.getId(), siteInfo.getMaintainerUserIds());
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
        List<OshSiteInfo> list = this.lambdaQuery().select(OshSiteInfo::getId, OshSiteInfo::getSiteName, OshSiteInfo::getCover, OshSiteInfo::getDescription, OshSiteInfo::getStatus, OshSiteInfo::getLastCheckTime).like(StringUtils.isNoneBlank(siteInfo.getSiteName()), OshSiteInfo::getSiteName, siteInfo.getSiteName()).eq(siteInfo.getStatus() != null, OshSiteInfo::getStatus, siteInfo.getStatus()).list();
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
            }, taskExecutor).whenComplete((v, t) -> LOG.info("发送网站异常通知邮件成功 {}", user.getEmail()));
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
}