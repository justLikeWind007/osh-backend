package com.backstage.system.controller.website;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.exception.ServiceException;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.common.utils.SecurityUtils;
import com.backstage.system.domain.dto.website.WebsiteAuditDto;
import com.backstage.system.domain.dto.website.WebsiteQueryDto;
import com.backstage.system.domain.dto.website.WebsiteSubmitDto;
import com.backstage.system.domain.vo.website.OshPracticalWebsiteVo;
import com.backstage.system.domain.vo.website.UserFavoriteWebsiteVo;
import com.backstage.system.domain.website.OshPracticalWebsite;
import com.backstage.system.mapper.website.OshUserFavoriteWebsiteMapper;
import com.backstage.system.service.website.OshPracticalWebsiteService;
import com.backstage.system.service.website.OshUserFavoriteWebsiteService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 实用网站 Controller
 */
@RestController
@RequestMapping("/api/website")
public class OshPracticalWebsiteController extends BaseController {

    @Autowired
    private OshPracticalWebsiteService oshPracticalWebsiteService;

    @Autowired
    private OshUserFavoriteWebsiteService oshUserFavoriteWebsiteService;

    /**
     * 查询实用网站列表（支持按名称和标签筛选）
     */
    @Anonymous
    @ApiOperation("查询实用网站列表")
    @GetMapping("/list")
    public R<TableDataInfo> list(@RequestParam(value = "websiteName", required = false) String websiteName,
                                 @RequestParam(value = "tagNames", required = false) String tagNames,
                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        WebsiteQueryDto queryDTO = new WebsiteQueryDto();
        queryDTO.setWebsiteName(websiteName);
        queryDTO.setTagNames(tagNames);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        return R.ok(getDataTable(oshPracticalWebsiteService.selectWebsitePage(queryDTO)));
    }

    /**
     * 增加网站点击次数
     */
    @Anonymous
    @ApiOperation("增加网站点击次数")
    @PutMapping("/click")
    public R<Void> incrementClickCount(@RequestParam("id") Long id) {
        int result = oshPracticalWebsiteService.incrementClickCount(id);
        return result > 0 ? R.ok() : R.fail("网络开小差");
    }

    /**
     * 用户提交网站
     */
    @Anonymous
    @ApiOperation("用户提交网站")
    @PostMapping("/submit")
    public R submit(@RequestBody WebsiteSubmitDto submitDto) {
        try {
            // 调用 Service 层保存网站信息
            int result = oshPracticalWebsiteService.submitWebsite(submitDto);
            // 判断是否保存成功
            if (result > 0) {
                return R.ok();
            } else {
                return R.fail("提交失败，请稍后重试");
            }
        } catch (IllegalArgumentException e) {
            // 捕获参数校验异常
            return R.fail(e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常
            e.printStackTrace();
            return R.fail("网络开小差");
        }
    }
    /**
     * 用户收藏网站
     */
    @ApiOperation("用户收藏网站")
    @Anonymous
    @PostMapping("/favorite")
    public R<Void> favorite(Long websiteId, @RequestParam (value = "remark", required = false) String remark) {
        try {
            // 调用 Service 层进行收藏
            int result = oshUserFavoriteWebsiteService.favoriteWebsite(websiteId);
            // 判断是否收藏成功
            if (result > 0) {
                return R.ok();
            } else {
                return R.fail("收藏失败，请勿重复收藏");
            }
        } catch (IllegalArgumentException e) {
            // 捕获参数校验异常（如重复收藏）
            return R.fail(e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常
            e.printStackTrace();
            return R.fail("网络开小差");
        }
    }
    /**
     * 用户取消收藏网站
     */
    @ApiOperation("用户取消收藏网站")
    @Anonymous
    @GetMapping("/del")
    public R<Void> cancelFavorite(@RequestParam("websiteId") Long websiteId) {
        try {

            // 调用 Service 层取消收藏
            int result = oshUserFavoriteWebsiteService.cancelFavoriteWebsite(websiteId);

            // 判断是否取消成功
            if (result > 0) {
                return R.ok();
            } else {
                return R.fail("取消收藏失败，请稍后重试");
            }
        } catch (IllegalArgumentException e) {
            return R.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("网络开小差");
        }
    }

    /**
     * 查询用户的收藏网站列表（分页）
     */
    @ApiOperation("查询用户的收藏网站列表")
    @Anonymous
    @GetMapping("/Favorites")
    public R<TableDataInfo> getMyFavoriteList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            // 调用 Service 层查询收藏列表
            TableDataInfo result =
                    oshUserFavoriteWebsiteService.selectUserFavoriteList(pageNum, pageSize);

            return R.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("网络开小差");
        }
    }

    /**
     * 管理员审核网站
     */
    @ApiOperation("管理员审核网站")
    @Anonymous
    @PostMapping("/audit")
    public R<String> audit(@RequestBody WebsiteAuditDto auditDto) {
        try {
            //从拦截器获取当前登录的管理员 ID
             //Long adminId = ThreadLocalUtil.get("admin",Long.class);
            //TODO 验证是否是管理员登录
            // 调用 Service 层审核
            boolean auditResult = oshPracticalWebsiteService.auditWebsite(auditDto);
            if (auditResult) {
                return R.ok("审核成功");
            } else {

                return R.fail( "审核失败: "+ auditDto.getRejectReason());
            }
        } catch (IllegalArgumentException e) {
            return R.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("审核失败");
        }
    }

    /**
     * 查询待审核的网站列表
     */
    @ApiOperation("查询待审核网站列表")
    @Anonymous
    @GetMapping("/audit/list")
    public R<TableDataInfo> getAuditByList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
            ) {
        try {
            TableDataInfo result = oshPracticalWebsiteService.selectAuditList(pageNum, pageSize);
            return R.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("查询失败");
        }
    }
    /**
     * 根据 ID 查询待审核网站的详情
     *
     * @param websiteId 网站ID
     * @return 网站详细信息
     */
    @ApiOperation("查询待审核网站详情")
    @Anonymous
    @GetMapping("/audit/detail/{websiteId}")
    public R<OshPracticalWebsiteVo> getAuditDetail(@PathVariable Long websiteId) {
        try {
            if (websiteId == null) {
                return R.fail("网站ID不能为空");
            }

            OshPracticalWebsiteVo website = oshPracticalWebsiteService.getAuditDetail(websiteId);

            if (website == null) {
                return R.fail("网站不存在或已审核");
            }

            return R.ok(website);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("查询失败");
        }
    }

    /**
     * 管理员批量删除网站
     */
    @ApiOperation("批量删除网站")
    @Anonymous
    @GetMapping("/batch")
    public R<String> batchDelete(@RequestParam List<Integer> websiteIds) {
        try {
            //从拦截器获取当前登录的管理员 ID
            Long adminId = ThreadLocalUtil.get("admin",Long.class);

            // 调用 Service 层批量删除
            int result = oshPracticalWebsiteService.batchDeleteWebsite(websiteIds);

            return R.ok("成功删除 " + result + " 个网站");
        } catch (ServiceException e) {
            return R.fail(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("删除失败");
        }
    }
}
