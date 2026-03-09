package com.backstage.system.controller.column;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.system.domain.vo.ColumnDetailVo;
import com.backstage.system.domain.vo.ColumnListItemVo;
import com.backstage.system.service.IColumnService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Hope
 * @createTime: 2026年03月03日 23:35:40
 * @version:
 * @Description:
 */
@RestController
@RequestMapping("/pc/column")
public class ColumnController {

    @Autowired
    IColumnService columnService;

    @GetMapping("/read")
    @Anonymous
    public AjaxResult read(@RequestParam Long id) {
        ColumnDetailVo columnDetail = columnService.getColumnDetail(id);
        if (columnDetail == null) {
            return AjaxResult.error("fail", "专栏不存在");
        }
        return AjaxResult.success(columnDetail);
    }

    @GetMapping("/list")
    @Anonymous
    public AjaxResult list(@RequestParam("page") Integer page,
                           @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        PageHelper.startPage(page, limit);
        List<ColumnListItemVo> rows = columnService.listColumnPage();
        PageInfo<ColumnListItemVo> pageInfo = new PageInfo<>(rows);

        Map<String, Object> data = new HashMap<>(2);
        data.put("count", pageInfo.getTotal());
        data.put("rows", rows);
        return new AjaxResult(20000, "ok", data);
    }
}
