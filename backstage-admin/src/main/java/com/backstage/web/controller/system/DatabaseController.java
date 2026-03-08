package com.backstage.web.controller.system;

import com.backstage.common.constant.HttpStatus;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.system.domain.database.QueryRequest;
import com.backstage.system.service.IDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/database")
public class DatabaseController extends BaseController {

    @Autowired
    private IDatabaseService databaseService;

    @GetMapping("/objects")
    public AjaxResult getDatabaseObjects() {
        return success(databaseService.getDatabaseObjects());
    }

    @GetMapping("/table/{tableName}")
    public TableDataInfo getTableData(
            @PathVariable String tableName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        // 安全检查
        if (!isValidTableName(tableName)) {
            throw new RuntimeException("非法的表名");
        }

        // 2. 分页查询
        List<Map<String, Object>> list = databaseService.getTableData(tableName, pageNum, pageSize);
        long total = databaseService.getTableCount(tableName);

        // 3. 封装结果
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(list);
        rspData.setTotal(total);
        return rspData;
    }

    private boolean isValidTableName(String tableName) {
        // 简单校验表名格式（实际项目建议使用白名单）
        return tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }

    @PostMapping("/query")
    public TableDataInfo executeQuery(@RequestBody QueryRequest request) {
        // 2. 分页查询
        List<Map<String, Object>> list = databaseService.getTableDataBySql(request.getSql(), request.getPage(), request.getPageSize());
        long total = databaseService.getQueryCount(request.getSql());

        // 3. 封装结果
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(list);
        rspData.setTotal(total);
        return rspData;
    }

    @PostMapping("/table/{tableName}")
    public AjaxResult saveTableData(@PathVariable String tableName, @RequestBody  Map<String, List<Map<String, Object>>> changes) {
        try {
            return success(databaseService.saveTableData(tableName, changes));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // 增加删除接口
    @DeleteMapping("/table/{tableName}/{id}")
    public AjaxResult deleteTableRow(
            @PathVariable String tableName,
            @PathVariable Long id) {
        int rows = databaseService.deleteById(tableName, id);
        return toAjax(rows);
    }

    // 批量删除接口
    @DeleteMapping("/table/{tableName}/batch")
    public AjaxResult batchDelete(
            @PathVariable String tableName,
            @RequestBody List<Long> ids) {
        int rows = databaseService.batchDelete(tableName, ids);
        return success("删除成功");
    }
}
