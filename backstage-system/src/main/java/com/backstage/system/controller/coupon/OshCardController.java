package com.backstage.system.controller.coupon;

import java.util.List;
import javax.naming.ldap.PagedResultsControl;
import javax.servlet.http.HttpServletResponse;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.vo.OshCardVo;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.enums.BusinessType;
import com.backstage.system.domain.coupon.OshCard;
import com.backstage.system.service.IOshCardService;
import com.backstage.common.utils.poi.ExcelUtil;
import com.backstage.common.core.page.TableDataInfo;

/**
 * 卡券信息Controller
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/pc/user_coupon")
public class OshCardController extends BaseController
{
    @Autowired
    private IOshCardService oshCardService;
    @Anonymous
    @ApiOperation("查询优惠劵列表")
    @GetMapping("get")
    public R getCardList(@ApiParam("网校 appid") @RequestHeader(value = "appid", required = false) String appid)
    {
        // 校验 appid 是否 为空
     // if (appid == null || appid.trim().isEmpty())
      //  {
      //      return R.fail("appid 不能为空");
      //  }

        //TODO: 校验请求中的 appid 的有效性
        startPage();
        List<OshCardVo> list = oshCardService.getOshCardList();
        //Page<OshCardVo> count = (Page<OshCardVo>) list;
        PageInfo<OshCardVo> pageInfo = new PageInfo<>(list);
        return R.ok(new TableDataInfo(list, pageInfo.getTotal()));
    }

    @Anonymous
    @PostMapping("/user_coupon/receive")
    public R receiveCoupon(@RequestBody OshCard oshCard){
         if(oshCardService.receiveCoupon(oshCard) == true) {
            return R.ok();
        } else {
            return R.fail("领取失败");
        }
    }

    /**
     * 查询卡券信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:card:list')")
    @GetMapping("/list")
    public TableDataInfo list(OshCard oshCard)
    {

        startPage();
        List<OshCard> list = oshCardService.selectOshCardList(oshCard);
        return getDataTable(list);
    }

    /**
     * 导出卡券信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:card:export')")
    @Log(title = "卡券信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OshCard oshCard)
    {
        List<OshCard> list = oshCardService.selectOshCardList(oshCard);
        ExcelUtil<OshCard> util = new ExcelUtil<OshCard>(OshCard.class);
        util.exportExcel(response, list, "卡券信息数据");
    }

    /**
     * 获取卡券信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:card:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(oshCardService.selectOshCardById(id));
    }

    /**
     * 新增卡券信息
     */
    @PreAuthorize("@ss.hasPermi('system:card:add')")
    @Log(title = "卡券信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OshCard oshCard)
    {
        return toAjax(oshCardService.insertOshCard(oshCard));
    }

    /**
     * 修改卡券信息
     */
    @PreAuthorize("@ss.hasPermi('system:card:edit')")
    @Log(title = "卡券信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OshCard oshCard)
    {
        return toAjax(oshCardService.updateOshCard(oshCard));
    }

    /**
     * 删除卡券信息
     */
    @PreAuthorize("@ss.hasPermi('system:card:remove')")
    @Log(title = "卡券信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(oshCardService.deleteOshCardByIds(ids));
    }
}
