package com.backstage.system.service.site;

import com.backstage.system.domain.site.OshSiteInfo;
import com.backstage.system.domain.site.OshSiteMaintainer;
import com.backstage.system.domain.user.OshUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 内部网站信息 Service 接口
 *
 * @author backstage
 */
public interface IOshSiteInfoService extends IService<OshSiteInfo> {

    /**
     * 新增网站使用记录
     *
     * @param siteInfo 网站信息
     * @return 结果
     */
    int insertUsage(OshSiteInfo siteInfo, OshUser oshUser);

    boolean saveSiteInfo(OshSiteInfo siteInfo);

    boolean updateSiteInfo(OshSiteInfo siteInfo);

    List<OshSiteInfo> listSites(OshSiteInfo siteInfo);

    MultiValueMap<Long, OshSiteMaintainer> getSiteMaintainers(Collection<Long> siteIds);

    boolean testConnection(List<OshSiteInfo> oshSiteInfos);

    boolean testConnection(List<OshSiteInfo> oshSiteInfos, int timeout);

    List<OshSiteInfo> checkConnectionStatus(List<OshSiteInfo> oshSiteInfos, int timeout);

    /**
     * 异步启动演示站点（供外部轮询）。
     * 首次调用提交后台启动任务；后续调用返回当前任务状态。
     *
     * @param siteId 站点ID
     * @return 当前任务状态（含 status, statusName, message, checkCount 等字段）
     */
    Map<String, Object> startDemoAsync(Long siteId);
}
