package com.backstage.system.controller.flashsale;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.Log;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.BusinessType;
import com.backstage.common.utils.poi.ExcelUtil;
import com.backstage.system.domain.SysFlashSale;
import com.backstage.system.domain.vo.FlashColumnVo;
import com.backstage.system.service.ISysFlashsaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 秒杀活动Controller
 *
 * @author 星号
 * @date 2026-03-04
 */
@RestController
@RequestMapping("/pc/flashsale")
public class SysFlashsaleController extends BaseController
{
    @Autowired
    private ISysFlashsaleService flashSaleService;


    /**
     * 秒杀课程
     */
    @Anonymous
    @GetMapping("/course/read")
    public R read(
            @RequestParam("id") Long id,
            @RequestParam("flashsale_id") Long flashsaleId) {
        Object data = flashSaleService.selectFlashsaleReadDetail(flashsaleId);
        if (data == null) {
            return R.fail("未找到相关秒杀活动");
        }
        return R.ok(data);
    }

    /**
     * 秒杀专栏详情接口
     */
    @Anonymous
    @GetMapping("/column/read")
    public R readColumn(
            @RequestParam("id") Long id,
            @RequestParam("flashsale_id") Long flashsaleId) {
        FlashColumnVo data = flashSaleService.selectFlashsaleColumnDetail(flashsaleId);
        if (data == null) {
            return R.fail("未找到该秒杀专栏信息");
        }
        return R.ok(data);
    }

    /**
     * 查询秒杀活动列表
     */
    @PreAuthorize("@ss.hasPermi('pc:flashsale:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysFlashSale oshFlashsale)
    {
        startPage();
        List<SysFlashSale> list = flashSaleService.selectOshFlashsaleList(oshFlashsale);
        return getDataTable(list);
    }

    /**
     * 导出秒杀活动列表
     */
    @PreAuthorize("@ss.hasPermi('pc:flashsale:export')")
    @Log(title = "秒杀活动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysFlashSale oshFlashsale)
    {
        List<SysFlashSale> list = flashSaleService.selectOshFlashsaleList(oshFlashsale);
        ExcelUtil<SysFlashSale> util = new ExcelUtil<SysFlashSale>(SysFlashSale.class);
        util.exportExcel(response, list, "秒杀活动数据");
    }

    /**
     * 获取秒杀活动详细信息
     */
    @PreAuthorize("@ss.hasPermi('pc:flashsale:query')")
    @GetMapping(value = "/{id}")
    public R<SysFlashSale> getInfo(@PathVariable("id") Long id)
    {
        SysFlashSale data = flashSaleService.selectOshFlashsaleById(id);
        return data != null ? R.ok(data) : R.fail("查询不到该活动");
    }

    /**
     * 新增秒杀活动
     */
    @PreAuthorize("@ss.hasPermi('pc:flashsale:add')")
    @Log(title = "秒杀活动", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody SysFlashSale oshFlashsale)
    {
        return R.ok(flashSaleService.insertOshFlashsale(oshFlashsale));
    }

    /**
     * 修改秒杀活动
     */
    @PreAuthorize("@ss.hasPermi('pc:flashsale:edit')")
    @Log(title = "秒杀活动", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody SysFlashSale oshFlashsale)
    {
        return R.ok(flashSaleService.updateOshFlashsale(oshFlashsale));
    }

    /**
     * 删除秒杀活动
     */
    @PreAuthorize("@ss.hasPermi('pc:flashsale:remove')")
    @Log(title = "秒杀活动", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids)
    {
        return R.ok(flashSaleService.deleteOshFlashsaleByIds(ids));
    }
}
