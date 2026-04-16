package com.backstage.system.service.site.impl;

import com.alibaba.fastjson2.JSON;
import com.backstage.system.domain.site.OshSiteInfo;
import com.backstage.system.domain.site.OshSiteUsage;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.mapper.site.OshSiteInfoMapper;
import com.backstage.system.service.site.IOshSiteInfoService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
    oshSiteUsage.setCreatedBy(oshUser.getUsername());
    oshSiteUsage.setCreationTime(new Date());
    oshSiteUsage.setUpdateTime(new Date());
    return oshSiteInfoMapper.insertUsage(oshSiteUsage);
  }

  /**
   * 每10分钟测试1次
   */
  @Scheduled(cron = "0 0/10 * * * ?")
  public void testSiteInfoResponseStatus() {
    List<OshSiteInfo> oshSiteInfos = oshSiteInfoMapper.selectList(Wrappers.<OshSiteInfo>lambdaQuery()
            .eq(OshSiteInfo::getIsDeleted, 0));
    for (OshSiteInfo siteInfo : oshSiteInfos) {
      if (StringUtils.isBlank(siteInfo.getSiteUrl())) {
        continue;
      }
      try {
        if (testUrlConnection(siteInfo.getSiteUrl(), 5000, 5000)) {
          if (Objects.equals(0, siteInfo.getStatus())) {
            siteInfo.setStatus(1);
            oshSiteInfoMapper.updateById(siteInfo);
          }
        } else {
          if (!Objects.equals(0, siteInfo.getStatus())) {
            siteInfo.setStatus(0);
            oshSiteInfoMapper.updateById(siteInfo);
          }
        }
      } catch (Throwable throwable) {
        LOG.error("failed to test site connection, {}", JSON.toJSONString(siteInfo));
      }
    }
  }

  /**
   * 测试 URL 是否可以正常访问
   *
   * @param urlStr         要测试的地址
   * @param connectTimeout 连接超时时间（毫秒）
   * @param readTimeout    读取超时时间（毫秒）
   * @return true=能连通，false=访问不通/超时/异常
   */
  private static boolean testUrlConnection(String urlStr, int connectTimeout, int readTimeout) throws Throwable {
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
