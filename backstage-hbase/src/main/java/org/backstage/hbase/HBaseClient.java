package org.backstage.hbase;

import java.util.Collection;
import java.util.List;

/**
 * HBase 客户端接口
 * <p>
 * 提供 HBase 表管理、数据读写等核心操作能力
 * </p>
 *
 * @author backstage
 */
public interface HBaseClient {

    /**
     * 打开 HBase 客户端连接
     */
    void open();

    /**
     * 根据实体类创建 HBase 表
     *
     * @param type 实体类类型，用于解析表名和列族信息
     * @return 创建成功返回 true，失败返回 false
     */
    boolean createTable(Class<?> type);

    /**
     * 根据表名和列族创建 HBase 表
     *
     * @param tableName 表名
     * @param families  列族名称数组
     * @return 创建成功返回 true，失败返回 false
     */
    boolean createTable(String tableName, String... families);

    /**
     * 删除指定的 HBase 表
     *
     * @param tableName 表名
     * @return 删除成功返回 true，失败返回 false
     */
    boolean dropTable(String tableName);

    /**
     * 判断表是否存在
     *
     * @param tableName 表名
     * @return 表存在返回 true，不存在返回 false
     */
    boolean existsTable(String tableName);

    /**
     * 保存单条数据到 HBase
     *
     * @param row 实体对象
     * @param <T> 实体类型
     */
    <T> void save(T row);

    /**
     * 批量保存数据到 HBase
     *
     * @param rows 实体对象集合
     * @param <T>  实体类型
     */
    <T> void saveBatch(Collection<T> rows);

    /**
     * 根据行键获取单条数据
     *
     * @param type  实体类类型
     * @param rowKey 行键
     * @param <T>   实体类型
     * @return 查询到的实体对象，不存在则返回 null
     */
    <T> T get(Class<T> type, String rowKey);

    /**
     * 根据行键删除单条数据
     *
     * @param type   实体类类型
     * @param rowKey 行键
     */
    void delete(Class<?> type, String rowKey);

    /**
     * 扫描查询数据
     *
     * @param type     实体类类型
     * @param startRow 起始行键（包含）
     * @param rowCount 查询条数
     * @param <T>      实体类型
     * @return 查询结果列表
     */
    <T> List<T> scan(Class<T> type, String startRow, int rowCount);

    List<String> getTableNames();

    /**
     * 关闭 HBase 客户端连接，释放资源
     */
    void close();

    <T> List<T> scanWithFilter(Class<T> type, String filter, int rowCount);
}
