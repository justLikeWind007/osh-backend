package com.backstage.system.controller.site;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.enums.BusinessType;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.system.domain.site.OshSiteTags;
import com.backstage.system.service.IOshSiteTagsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 内部网站标签 Controller
 * 
 * @author backstage
 */
@Api(tags = "内部网站标签管理")
@RestController
@RequestMapping("/pc/site/tags")
public class OshSiteTagsController extends BaseController {

    @Autowired
    private IOshSiteTagsService oshSiteTagsService;

    /**
     * 查询所有标签列表（去重）
     */
    @Anonymous
    @ApiOperation("查询所有标签列表")
    @GetMapping("/all")
    public R<List<String>> getAllTags() {
        List<String> tags = oshSiteTagsService.getAllTags();
        return R.ok(tags);
    }

    /**
     * 查询所有标签及其使用次数
     */
    @Anonymous
    @ApiOperation("查询所有标签及其使用次数")
    @GetMapping("/list")
    public R<List<Map<String, Object>>> getAllTagsWithUsage() {
        List<Map<String, Object>> tags = oshSiteTagsService.getAllTagsWithUsage();
        return R.ok(tags);
    }

    /**
     * 查询网站的标签列表
     */
    @Anonymous
    @ApiOperation("查询网站的标签列表")
    @GetMapping("/{siteId}")
    public R<List<String>> getTagsBySiteId(@PathVariable Long siteId) {
        List<String> tags = oshSiteTagsService.getTagsBySiteId(siteId);
        return R.ok(tags);
    }

    /**
     * 新增标签
     */
    @Anonymous
    @Log(title = "内部网站标签", businessType = BusinessType.INSERT)
    @ApiOperation("新增标签")
    @PostMapping
    public R<Void> add(@RequestBody OshSiteTags oshSiteTags) {
        long currentUserId = ThreadLocalUtil.getCurrentUserId();
        oshSiteTags.setCreatedBy(currentUserId);
        oshSiteTags.setCreationTime(new Date());
        oshSiteTags.setUpdateTime(new Date());
        oshSiteTags.setUpdateBy(currentUserId);
        oshSiteTags.setIsDeleted(0);
        
        if (oshSiteTagsService.save(oshSiteTags)) {
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
    public R<Void> edit(@RequestBody OshSiteTags oshSiteTags) {
        long currentUserId = ThreadLocalUtil.getCurrentUserId();
        oshSiteTags.setUpdateBy(currentUserId);
        oshSiteTags.setUpdateTime(new Date());
        
        if (oshSiteTagsService.updateById(oshSiteTags)) {
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
        if (oshSiteTagsService.removeByIds(java.util.Arrays.asList(ids))) {
            return R.ok();
        } else {
            return R.fail("删除标签失败");
        }
    }
}
