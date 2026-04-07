package com.backstage.system.controller.site;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.enums.BusinessType;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.system.domain.site.OshSiteTag;
import com.backstage.system.service.site.IOshSiteTagsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 内部网站标签 Controller
 *
 * @author backstage
 */
@Api(tags = "内部网站标签管理")
@RestController
@RequestMapping("/pc/site/tags")
public class OshSiteTagController extends BaseController {

  @Autowired
  private IOshSiteTagsService oshSiteTagsService;

  /**
   * 查询所有标签及其使用次数
   */
  @Anonymous
  @ApiOperation("查询所有标签及其使用次数")
  @GetMapping("/list")
  public R<List<OshSiteTag>> getAllTagsWithUsage() {
    return R.ok(oshSiteTagsService.getAllTagsWithUsage());
  }

  /**
   * 查询网站的标签列表
   */
  @Anonymous
  @ApiOperation("查询网站的标签列表")
  @GetMapping("/{siteId}")
  public R<List<OshSiteTag>> getTagsBySiteId(@PathVariable Long siteId) {
    List<OshSiteTag> tags = oshSiteTagsService.getTagsBySiteId(siteId);
    return R.ok(tags);
  }

  /**
   * 新增标签
   */
  @Anonymous
  @Log(title = "内部网站标签", businessType = BusinessType.INSERT)
  @ApiOperation("新增标签")
  @PostMapping
  public R<Void> add(@RequestBody OshSiteTag oshSiteTag) {
    long currentUserId = ThreadLocalUtil.getCurrentUserId();
    oshSiteTag.setCreatedBy(currentUserId);
    oshSiteTag.setCreationTime(new Date());
    oshSiteTag.setUpdateTime(new Date());
    oshSiteTag.setUpdateBy(currentUserId);
    oshSiteTag.setIsDeleted(0);
    if (oshSiteTagsService.save(oshSiteTag)) {
      return R.ok();
    } else {
      return R.fail("新增标签失败");
    }
  }

  /**
   * 修改标签
   */
  @Anonymous
  @Log(title = "内部网站标签", businessType = BusinessType.UPDATE)
  @ApiOperation("修改标签")
  @PutMapping
  public R<Void> edit(@RequestBody OshSiteTag oshSiteTag) {
    long currentUserId = ThreadLocalUtil.getCurrentUserId();
    oshSiteTag.setUpdateBy(currentUserId);
    oshSiteTag.setUpdateTime(new Date());
    if (oshSiteTagsService.updateById(oshSiteTag)) {
      return R.ok();
    } else {
      return R.fail("修改标签失败");
    }
  }

  /**
   * 删除标签
   */
  @Anonymous
  @Log(title = "内部网站标签", businessType = BusinessType.DELETE)
  @ApiOperation("删除标签")
  @DeleteMapping("/{ids}")
  public R<Void> remove(@PathVariable Long[] ids) {
    oshSiteTagsService.removeByIds(Arrays.asList(ids));
    return R.ok();
  }
}
