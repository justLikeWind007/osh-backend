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
import com.backstage.system.domain.site.OshSiteMaintainer;
import com.backstage.system.domain.site.OshSiteTag;
import com.backstage.system.mapper.site.OshSiteInfoMapper;
import com.backstage.system.service.common.OssService;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.service.site.IOshSiteInfoService;
import com.backstage.system.service.site.IOshSiteTagsService;
import com.backstage.system.service.user.IOshUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;
import java.net.URL;
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
        siteInfo.setCreatedBy(currentUserId);
        siteInfo.setCreationTime(new Date());
        siteInfo.setUpdateTime(new Date());
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
        siteInfo.setUpdateTime(new Date());
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
    @PostMapping("/check/{id}")
    public R<Map<String, Object>> checkSiteConnection(@PathVariable Long id) {
        OshSiteInfo siteInfo = oshSiteInfoService.getById(id);
        if (siteInfo == null) {
            return R.fail("网站不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("siteId", id);
        result.put("siteName", siteInfo.getSiteName());

        try {
            // 尝试连接网站
            java.net.URL url = new java.net.URL(siteInfo.getSiteUrl());
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();

            boolean isConnected = responseCode >= 200 && responseCode < 400;
            result.put("isConnected", isConnected);
            result.put("responseCode", responseCode);
            result.put("message", isConnected ? "连接正常" : "连接异常，HTTP状态码: " + responseCode);

            connection.disconnect();
        } catch (Exception e) {
            result.put("isConnected", false);
            result.put("message", "连接失败: " + e.getMessage());
        }

        // 更新最后检查时间
        siteInfo.setLastCheckTime(new Date());
        oshSiteInfoService.updateById(siteInfo);
        result.put("lastCheckTime", siteInfo.getLastCheckTime());

        return R.ok(result);
    }

    /**
     * 批量检查所有网站连接状态
     */
    @Anonymous
    @ApiOperation("批量检查所有网站连接状态")
    @PostMapping("/check-all")
    public R<List<Map<String, Object>>> checkAllSitesConnection() {
        List<OshSiteInfo> list = oshSiteInfoService.lambdaQuery()
                .select(OshSiteInfo::getId, OshSiteInfo::getSiteName, OshSiteInfo::getSiteUrl)
                .eq(OshSiteInfo::getStatus, 1)
                .list();

        List<Map<String, Object>> results = new ArrayList<>();
        for (OshSiteInfo siteInfo : list) {
            Map<String, Object> result = new HashMap<>();
            result.put("siteId", siteInfo.getId());
            result.put("siteName", siteInfo.getSiteName());

            try {
                URL url = new URL(siteInfo.getSiteUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                int responseCode = connection.getResponseCode();

                boolean isConnected = responseCode >= 200 && responseCode < 400;
                result.put("isConnected", isConnected);
                result.put("responseCode", responseCode);
                connection.disconnect();
            } catch (Exception e) {
                result.put("isConnected", false);
                result.put("message", e.getMessage());
            }
            // 更新最后检查时间
            siteInfo.setLastCheckTime(new Date());
            oshSiteInfoService.updateById(siteInfo);
            result.put("lastCheckTime", siteInfo.getLastCheckTime());
            results.add(result);
        }
        return R.ok(results);
    }
}
