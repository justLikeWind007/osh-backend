package org.backstage.hbase;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.thrift2.generated.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 基于 Thrift2 协议操作 HBase 的客户端实现
 * <p>
 * Thrift2 API 更贴近原生 HBase Java API，提供高效的数据读写能力；
 * 表管理类操作（建表、删表等）因 Thrift2 未暴露相关接口，
 * 故通过原生 HBase Java API ({@link Admin}) 实现。
 * 使用前需调用 {@link #open()} 建立连接，使用完毕后调用 {@link #close()} 释放资源。
 * </p>
 *
 * @author backstage
 * @see org.apache.hadoop.hbase.thrift2.generated.THBaseService
 */
public class ThriftV2HBaseClient implements HBaseClient {

    /**
     * HBase Thrift2 服务地址
     */
    private final String host;
    /**
     * HBase Thrift2 服务端口
     */
    private final int port;
    /**
     * Thrift 传输层
     */
    private TTransport transport;
    /**
     * Thrift2 HBase 服务客户端（负责数据读写）
     */
    private THBaseService.Client client;
    /**
     * 原生 HBase 连接（负责表管理）
     */
    private Connection connection;
    /**
     * 原生 HBase Admin 接口
     */
    private Admin admin;

    /**
     * 构造 ThriftV2 HBase 客户端
     *
     * @param host HBase Thrift2 服务地址（同时作为 ZooKeeper 地址用于表管理）
     * @param port HBase Thrift2 服务端口
     */
    public ThriftV2HBaseClient(String host, int port) {
        this.host = host;
        this.port = port;

        this.open();
    }

    /**
     * 打开与 HBase 的连接
     * <p>
     * 同时初始化 Thrift2 传输层和原生 HBase 连接。
     * </p>
     */
    @Override
    public void open() {
        openThriftConnection();
    }

    /**
     * 建立 Thrift2 连接
     */
    private void openThriftConnection() {
        try {
            transport = new TSocket(host, port);
            TProtocol protocol = new TBinaryProtocol(transport);
            client = new THBaseService.Client(protocol);
            transport.open();
        } catch (TTransportException e) {
            throw new HBaseException("连接 HBase Thrift2 服务失败: " + host + ":" + port, e);
        }
    }

    /**
     * 根据实体类创建 HBase 表
     * <p>
     * 从 {@link HBaseTable} 注解中读取表名和默认列族，并扫描类中所有 {@link HBaseColumn}
     * 注解收集全部列族，最终通过原生 Admin 接口创建表。
     * </p>
     *
     * @param type 实体类类型
     * @return 创建成功返回 true
     */
    @Override
    public boolean createTable(Class<?> type) {
        HBaseTable tableAnno = type.getAnnotation(HBaseTable.class);
        if (tableAnno == null) {
            throw new HBaseException("类缺少 @HBaseTable 注解: " + type.getName());
        }

        String tableName = tableAnno.tableName();
        Set<String> families = new HashSet<>();
        families.add(tableAnno.defaultFamily());

        for (Field field : type.getDeclaredFields()) {
            HBaseColumn colAnno = field.getAnnotation(HBaseColumn.class);
            if (colAnno != null && colAnno.family() != null && !colAnno.family().isEmpty()) {
                families.add(colAnno.family());
            }
        }

        return createTable(tableName, families.toArray(new String[0]));
    }

    /**
     * 根据表名和列族创建 HBase 表
     *
     * @param tableName 表名
     * @param families  列族名称数组
     * @return 创建成功返回 true
     */
    @Override
    public boolean createTable(String tableName, String... families) {
        if (families == null || families.length == 0) {
            throw new HBaseException("创建表需要至少一个列族");
        }
        try {
            TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
            for (String family : families) {
                builder.setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(family)).build());
            }
            admin.createTable(builder.build());
            return true;
        } catch (Exception e) {
            throw new HBaseException("创建表失败: " + tableName, e);
        }
    }

    /**
     * 删除指定的 HBase 表
     * <p>
     * 先禁用表，再执行删除操作。
     * </p>
     *
     * @param tableName 表名
     * @return 删除成功返回 true；表不存在返回 false
     */
    @Override
    public boolean dropTable(String tableName) {
        try {
            if (!existsTable(tableName)) {
                return false;
            }
            TableName tn = TableName.valueOf(tableName);
            admin.disableTable(tn);
            admin.deleteTable(tn);
            return true;
        } catch (Exception e) {
            throw new HBaseException("删除表失败: " + tableName, e);
        }
    }

    /**
     * 判断表是否存在 Thrift2（HBase 1.x 早期）
     *
     * @param tableName 表名
     * @return 表存在返回 true，不存在返回 false
     */
    @Override
    public boolean existsTable(String tableName) {
        ByteBuffer table = ByteBuffer.wrap(tableName.getBytes());
        TGet tget = new TGet();
        tget.setRow(ByteBuffer.wrap("any_row_key".getBytes())); // 随便一个行键
        try {
            client.exists(table, tget);
            return true;
        } catch (TException e) {
            // 常见：TableNotFoundException 会被包装成 TIOError
            return false;
        }
    }

    /**
     * 保存单条数据到 HBase
     * <p>
     * 通过反射读取实体中的 {@link HBaseRowKey} 和 {@link HBaseColumn} 字段，
     * 组装为 {@link TPut} 后通过 Thrift2 写入。
     * </p>
     *
     * @param row 实体对象
     * @param <T> 实体类型
     */
    @Override
    public <T> void save(T row) {
        if (row == null) {
            return;
        }
        try {
            Class<?> clazz = row.getClass();
            HBaseTable tableAnno = clazz.getAnnotation(HBaseTable.class);
            if (tableAnno == null) {
                throw new HBaseException("实体类缺少 @HBaseTable 注解: " + clazz.getName());
            }

            String tableName = tableAnno.tableName();
            String defaultFamily = tableAnno.defaultFamily();
            String rowKey = HBaseUtils.getRowKey(row);

            TPut tPut = new TPut();
            tPut.setRow(rowKey.getBytes(StandardCharsets.UTF_8));

            List<TColumnValue> columnValues = buildColumnValues(row, defaultFamily);
            if (!columnValues.isEmpty()) {
                tPut.setColumnValues(columnValues);
            }
            client.put(ByteBuffer.wrap(tableName.getBytes(StandardCharsets.UTF_8)), tPut);
        } catch (HBaseException e) {
            throw e;
        } catch (Exception e) {
            throw new HBaseException("保存数据失败", e);
        }
    }

    /**
     * 批量保存数据到 HBase
     * <p>
     * 将多条记录组装为 {@link TPut} 列表，通过 {@code putMultiple} 一次性写入，提升吞吐量。
     * </p>
     *
     * @param rows 实体对象集合
     * @param <T>  实体类型
     */
    @Override
    public <T> void saveBatch(Collection<T> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }

        Iterator<T> iterator = rows.iterator();
        T first = iterator.next();
        Class<?> clazz = first.getClass();
        HBaseTable tableAnno = clazz.getAnnotation(HBaseTable.class);
        if (tableAnno == null) {
            throw new HBaseException("实体类缺少 @HBaseTable 注解: " + clazz.getName());
        }

        String tableName = tableAnno.tableName();
        String defaultFamily = tableAnno.defaultFamily();

        try {
            List<TPut> tPuts = new ArrayList<>();
            for (T row : rows) {
                String rowKey = HBaseUtils.getRowKey(row);
                TPut tPut = new TPut();
                tPut.setRow(rowKey.getBytes(StandardCharsets.UTF_8));

                List<TColumnValue> columnValues = buildColumnValues(row, defaultFamily);
                if (!columnValues.isEmpty()) {
                    tPut.setColumnValues(columnValues);
                }
                tPuts.add(tPut);
            }

            client.putMultiple(ByteBuffer.wrap(tableName.getBytes(StandardCharsets.UTF_8)), tPuts);
        } catch (HBaseException e) {
            throw e;
        } catch (Exception e) {
            throw new HBaseException("批量保存数据失败", e);
        }
    }

    /**
     * 根据行键获取单条数据
     *
     * @param type   实体类类型
     * @param rowKey 行键
     * @param <T>    实体类型
     * @return 查询到的实体对象，不存在则返回 null
     */
    @Override
    public <T> T get(Class<T> type, String rowKey) {
        if (rowKey == null) {
            return null;
        }
        try {
            HBaseTable tableAnno = type.getAnnotation(HBaseTable.class);
            if (tableAnno == null) {
                throw new HBaseException("实体类缺少 @HBaseTable 注解: " + type.getName());
            }

            String tableName = tableAnno.tableName();
            TGet tGet = new TGet();
            tGet.setRow(rowKey.getBytes(StandardCharsets.UTF_8));

            TResult result = client.get(ByteBuffer.wrap(tableName.getBytes(StandardCharsets.UTF_8)), tGet);
            if (result == null || result.getColumnValues() == null || result.getColumnValues().isEmpty()) {
                return null;
            }

            return convertResultToEntity(result, type, rowKey);
        } catch (HBaseException e) {
            throw e;
        } catch (Exception e) {
            throw new HBaseException("查询数据失败, rowKey=" + rowKey, e);
        }
    }

    /**
     * 根据行键删除单条数据
     *
     * @param type   实体类类型
     * @param rowKey 行键
     */
    @Override
    public void delete(Class<?> type, String rowKey) {
        if (rowKey == null) {
            return;
        }
        try {
            HBaseTable tableAnno = type.getAnnotation(HBaseTable.class);
            if (tableAnno == null) {
                throw new HBaseException("实体类缺少 @HBaseTable 注解: " + type.getName());
            }

            String tableName = tableAnno.tableName();
            TDelete tDelete = new TDelete();
            tDelete.setRow(rowKey.getBytes(StandardCharsets.UTF_8));

            client.deleteSingle(ByteBuffer.wrap(tableName.getBytes(StandardCharsets.UTF_8)), tDelete);
        } catch (HBaseException e) {
            throw e;
        } catch (Exception e) {
            throw new HBaseException("删除数据失败, rowKey=" + rowKey, e);
        }
    }

    /**
     * 扫描查询数据
     * <p>
     * 从起始行键开始扫描指定条数的数据，并将结果映射为实体对象列表。
     * Scanner 在使用完毕后会被自动关闭。
     * </p>
     *
     * @param type     实体类类型
     * @param startRow 起始行键（包含）
     * @param rowCount 查询条数
     * @param <T>      实体类型
     * @return 查询结果列表
     */
    @Override
    public <T> List<T> scan(Class<T> type, String startRow, int rowCount) {
        if (rowCount <= 0) {
            return Collections.emptyList();
        }
        try {
            HBaseTable tableAnno = type.getAnnotation(HBaseTable.class);
            if (tableAnno == null) {
                throw new HBaseException("实体类缺少 @HBaseTable 注解: " + type.getName());
            }

            String tableName = tableAnno.tableName();
            TScan tScan = new TScan();
            if (startRow != null) {
                tScan.setStartRow(startRow.getBytes(StandardCharsets.UTF_8));
            }
            tScan.setCaching(rowCount);
            List<TColumn> columns = buildScanColumns(type, tableAnno.defaultFamily());
            if (!columns.isEmpty()) {
                tScan.setColumns(columns);
            }
            int scannerId = client.openScanner(ByteBuffer.wrap(tableName.getBytes(StandardCharsets.UTF_8)), tScan);
            try {
                List<TResult> results = client.getScannerRows(scannerId, rowCount);
                if (results == null || results.isEmpty()) {
                    return Collections.emptyList();
                }
                List<T> list = new ArrayList<>();
                for (TResult result : results) {
                    String resultRowKey = result.getRow() == null
                            ? null : new String(result.getRow(), StandardCharsets.UTF_8);
                    T entity = convertResultToEntity(result, type, resultRowKey);
                    list.add(entity);
                }
                return list;
            } finally {
                client.closeScanner(scannerId);
            }
        } catch (HBaseException e) {
            throw e;
        } catch (Exception e) {
            throw new HBaseException("扫描数据失败", e);
        }
    }

    @Override
    public List<String> getTableNames() {
        return Collections.emptyList();
    }

    /**
     * 关闭 HBase 客户端连接，释放资源
     */
    @Override
    public void close() {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
        if (admin != null) {
            try {
                admin.close();
            } catch (Exception ignored) {
                // ignore
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception ignored) {
                // ignore
            }
        }
    }

    @Override
    public <T> List<T> scanWithFilter(Class<T> type, String filter, int rowCount) {
        return Collections.emptyList();
    }

    // ===================== 私有工具方法 =====================

    /**
     * 构建实体对象的列值列表
     *
     * @param row           实体对象
     * @param defaultFamily 默认列族
     * @return TColumnValue 列表
     * @throws Exception 反射读取字段值时可能抛出的异常
     */
    private List<TColumnValue> buildColumnValues(Object row, String defaultFamily) throws Exception {
        List<TColumnValue> columnValues = new ArrayList<>();
        Class<?> clazz = row.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            HBaseColumn colAnno = field.getAnnotation(HBaseColumn.class);
            if (colAnno == null) {
                continue;
            }
            field.setAccessible(true);
            Object value = field.get(row);
            if (value == null) {
                continue;
            }

            String family = (colAnno.family() == null || colAnno.family().isEmpty())
                    ? defaultFamily : colAnno.family();
            String qualifier = colAnno.qualifier();

            TColumnValue columnValue = new TColumnValue();
            columnValue.setFamily(family.getBytes(StandardCharsets.UTF_8));
            columnValue.setQualifier(qualifier.getBytes(StandardCharsets.UTF_8));
            columnValue.setValue(String.valueOf(value).getBytes(StandardCharsets.UTF_8));
            columnValues.add(columnValue);
        }
        return columnValues;
    }

    /**
     * 构建扫描查询所需的列集合
     *
     * @param type          实体类
     * @param defaultFamily 默认列族
     * @return TColumn 列表
     */
    private List<TColumn> buildScanColumns(Class<?> type, String defaultFamily) {
        List<TColumn> columns = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            HBaseColumn colAnno = field.getAnnotation(HBaseColumn.class);
            if (colAnno == null) {
                continue;
            }
            String family = (colAnno.family() == null || colAnno.family().isEmpty())
                    ? defaultFamily : colAnno.family();
            String qualifier = colAnno.qualifier();

            TColumn column = new TColumn();
            column.setFamily(family.getBytes(StandardCharsets.UTF_8));
            column.setQualifier(qualifier.getBytes(StandardCharsets.UTF_8));
            columns.add(column);
        }
        return columns;
    }

    /**
     * 将 Thrift2 查询结果转换为实体对象
     *
     * @param result HBase Thrift2 查询结果
     * @param type   实体类型
     * @param rowKey 行键
     * @param <T>    实体类型
     * @return 实体对象
     * @throws Exception 反射实例化或字段赋值异常
     */
    private <T> T convertResultToEntity(TResult result, Class<T> type, String rowKey) throws Exception {
        T entity = type.newInstance();

        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(HBaseRowKey.class)) {
                field.setAccessible(true);
                field.set(entity, rowKey);
                break;
            }
        }

        List<TColumnValue> columnValues = result.getColumnValues();
        if (columnValues == null) {
            return entity;
        }

        for (TColumnValue cv : columnValues) {
            String family = new String(cv.getFamily(), StandardCharsets.UTF_8);
            String qualifier = new String(cv.getQualifier(), StandardCharsets.UTF_8);
            String value = new String(cv.getValue(), StandardCharsets.UTF_8);

            Field field = findFieldByColumn(type, family, qualifier);
            if (field != null) {
                field.setAccessible(true);
                field.set(entity, value);
            }
        }
        return entity;
    }

    /**
     * 根据列族和列限定符查找实体类中对应的字段
     *
     * @param type      实体类
     * @param family    列族
     * @param qualifier 列限定符
     * @return 匹配的字段，未找到返回 null
     */
    private Field findFieldByColumn(Class<?> type, String family, String qualifier) {
        HBaseTable tableAnno = type.getAnnotation(HBaseTable.class);
        String defaultFamily = tableAnno == null ? "info" : tableAnno.defaultFamily();

        for (Field field : type.getDeclaredFields()) {
            HBaseColumn colAnno = field.getAnnotation(HBaseColumn.class);
            if (colAnno == null) {
                continue;
            }
            String f = (colAnno.family() == null || colAnno.family().isEmpty())
                    ? defaultFamily : colAnno.family();
            String q = colAnno.qualifier();
            if (f.equals(family) && q.equals(qualifier)) {
                return field;
            }
        }
        return null;
    }
}
