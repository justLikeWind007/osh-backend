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
import com.backstage.system.service.site.IOshSiteInfoService;
import com.backstage.system.service.site.IOshSiteTagsService;
import com.backstage.system.service.common.OssService;
import com.backstage.system.service.user.IOshUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    List<OshSiteInfo> list = oshSiteInfoService.lambdaQuery()
            .select(OshSiteInfo::getId,
                    OshSiteInfo::getSiteName,
                    OshSiteInfo::getCover,
                    OshSiteInfo::getDescription,
                    OshSiteInfo::getStatus)
            .like(StringUtils.hasText(siteInfo.getSiteName()), OshSiteInfo::getSiteName, siteInfo.getSiteName())
            .eq(siteInfo.getStatus() != null, OshSiteInfo::getStatus, siteInfo.getStatus())
            .list();
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
    if (oshSiteInfoService.save(siteInfo)) {
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
    if (oshSiteInfoService.updateById(siteInfo)) {
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
}
