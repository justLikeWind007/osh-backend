package com.backstage.system.controller.site;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.BusinessType;
import com.backstage.common.enums.UploadPathEnum;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.site.OshSiteInfo;
import com.backstage.system.service.IOshSiteInfoService;
import com.backstage.system.service.IOshSiteTagsService;
import com.backstage.system.service.common.OssService;
import com.backstage.system.utils.UserContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
  UserContextUtil userContextUtil;

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
                    // OshSiteInfo::getSiteUrl,
                    OshSiteInfo::getDescription,
                    OshSiteInfo::getStatus)
            .like(StringUtils.hasText(siteInfo.getSiteName()), OshSiteInfo::getSiteName, siteInfo.getSiteName())
            .eq(siteInfo.getStatus() != null, OshSiteInfo::getStatus, siteInfo.getStatus())
            .list();
    return R.ok(getDataTable(list));
  }

  /**
   * 获取网站详细信息
   */
  @Anonymous
  @ApiOperation("获取网站详细信息")
  @PostMapping(value = "/use/{id}")
  public R<OshSiteInfo> getInfo(@PathVariable Long id) {
    OshSiteInfo siteInfo = oshSiteInfoService.getById(id);
    if (siteInfo == null) {
      return R.fail("网站不存在");
    }
    int i = oshSiteInfoService.insertUsage(siteInfo);
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
    if (oshSiteInfoService.save(siteInfo)) {
      // 保存标签
      if (siteInfo.getTags() != null && !siteInfo.getTags().isEmpty()) {
        List<String> tagList = Arrays.asList(siteInfo.getTags().split("\\s*,\\s*"));
        oshSiteTagsService.saveSiteTags(siteInfo.getId(), tagList, currentUserId);
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
      if (siteInfo.getTags() != null) {
        List<String> tagList = siteInfo.getTags().isEmpty() ? 
          java.util.Collections.emptyList() : 
          Arrays.asList(siteInfo.getTags().split("\\s*,\\s*"));
        oshSiteTagsService.saveSiteTags(siteInfo.getId(), tagList, currentUserId);
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
   * 上传网站封面图片
   */
  @Anonymous
  @PostMapping("/cover/upload")
  public R<String> uploadCover(MultipartFile file) throws Exception {
    String path = ossService.upload(file, UploadPathEnum.INNER_SITE, "");
    return R.ok(path);
  }
}
