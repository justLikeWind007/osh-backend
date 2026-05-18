package org.backstage.hbase;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 直连HBase Master，使用Apache HBase提供的API操作HBase
 *
 * @see org.apache.hadoop.hbase.client.HTable
 */
public class DefaultHBaseClient implements HBaseClient {

    @Override
    public void open() {

    }

    @Override
    public boolean createTable(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean createTable(String tableName, String... families) {
        return false;
    }

    @Override
    public boolean dropTable(String tableName) {
        return false;
    }

    @Override
    public boolean existsTable(String tableName) {
        return false;
    }

    @Override
    public <T> void save(T row) {

    }

    @Override
    public <T> void saveBatch(Collection<T> rows) {

    }

    @Override
    public <T> T get(Class<T> clazz, String rowKey) {
        return null;
    }

    @Override
    public void delete(Class<?> clazz, String rowKey) {

    }

    @Override
    public <T> List<T> scan(Class<T> clazz, String startRow, int rowCount) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getTableNames() {
        return Collections.emptyList();
    }

    @Override
    public void close() {

    }

    @Override
    public <T> List<T> scanWithFilter(Class<T> type, String filter, int rowCount) {
        return Collections.emptyList();
    }
}
