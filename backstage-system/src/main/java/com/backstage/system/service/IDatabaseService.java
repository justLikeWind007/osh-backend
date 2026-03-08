package com.backstage.system.service;

import com.backstage.system.domain.database.DatabaseObject;
import com.backstage.system.domain.database.QueryResult;
import java.util.List;
import java.util.Map;

public interface IDatabaseService {
    Map<String, List<DatabaseObject>> getDatabaseObjects();
    List<Map<String, Object>> getTableData(String tableName, int pageIndex, int pageSize);
    List<Map<String, Object>> getTableDataBySql(String sqlStr, int pageIndex, int pageSize);
    long getTableCount(String tableName);
    long getQueryCount(String sql);
    List<QueryResult> executeQuery(String sql) throws Exception;
    int saveTableData(String tableName,  Map<String, List<Map<String, Object>>> changes) throws Exception;
    int deleteById(String tableName, Long id);
    int batchDelete(String tableName, List<Long> ids);
}
