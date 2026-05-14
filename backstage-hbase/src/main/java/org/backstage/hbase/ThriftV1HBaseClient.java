package org.backstage.hbase;

import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.hadoop.hbase.thrift.generated.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.glassfish.jersey.internal.guava.HashBasedTable;
import org.glassfish.jersey.internal.guava.Table;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * thrift (V1)
 */
class ThriftV1HBaseClient implements HBaseClient {
    protected Hbase.Client hbaseClient;
    private final TTransport socket;
    protected static final Charset CHAR_SET = StandardCharsets.UTF_8;

    public ThriftV1HBaseClient(String addr, int port) {
        socket = new TSocket(addr, port);
        TProtocol protocol = new TBinaryProtocol(socket, true, true);
        hbaseClient = new Hbase.Client(protocol);
        this.open();
    }

    public static void testIterateRow(String tableName, String rowKey, ThriftV1HBaseClient client) throws TException {
        Map<String, String> attributes = new HashMap<>();
        List<TRowResult> results = client.getRow(tableName, rowKey, attributes);
        for (TRowResult rslt : results) {
            client.iterateResults(rslt);
        }
    }

    public static void testScanTable(String tableName, String startRow, int rowCnt, ThriftV1HBaseClient client) throws TException {
        List<String> columns = new ArrayList<>(0);
        Map<String, String> attributesTest = new HashMap<>();
        int scannerID = client.scannerOpen(tableName, startRow, columns, attributesTest);
        try {
            List<TRowResult> scanResults = client.scannerGetList(scannerID, rowCnt);
            while (scanResults != null && !scanResults.isEmpty()) {
                for (TRowResult rowResult : scanResults) {
                    client.iterateResults(rowResult);
                }
                scanResults = client.scannerGetList(scannerID, rowCnt);
            }
        } finally {
            client.scannerClose(scannerID);
        }
    }

    public void listTableNames(ThriftV1HBaseClient client) {
        List<String> tblNames = client.getTableNames();
        for (String name : tblNames) {
            System.out.println(">> " + name);
        }
    }

    public void deleteRow(String table, String rowKey) throws TException {
        ByteBuffer tableName = getByteBuffer(table);
        ByteBuffer row = getByteBuffer(rowKey);
        hbaseClient.deleteAllRow(tableName, row, getAttributesMap(new HashMap<>()));
    }

    public void deleteCell(String table, String rowKey, String column) throws TException {
        List<String> columns = new ArrayList<>(1);
        columns.add(column);
        deleteCells(table, rowKey, columns);
    }

    public void deleteCells(String table, String rowKey, List<String> columns) throws TException {
        boolean writeToWal = false;
        List<Mutation> mutations = new ArrayList<>();
        for (String column : columns) {
            mutations.add(new Mutation(false, getByteBuffer(column), null, writeToWal));
        }
        ByteBuffer tableName = getByteBuffer(table);
        ByteBuffer row = getByteBuffer(rowKey);
        hbaseClient.mutateRow(tableName, row, mutations, getAttributesMap(new HashMap<>()));
    }

    public void updateRow(String table, String rowKey, Map<String, String> rowData) throws TException {
        boolean writeToWal = false;
        Map<String, String> attributes = new HashMap<>();
        List<Mutation> mutations = new ArrayList<>();

        for (Map.Entry<String, String> entry : rowData.entrySet()) {
            mutations.add(new Mutation(false, getByteBuffer(entry.getKey()), getByteBuffer(entry.getValue()), writeToWal));
        }
        Map<ByteBuffer, ByteBuffer> wrappedAttributes = getAttributesMap(attributes);
        ByteBuffer tableName = getByteBuffer(table);
        ByteBuffer row = getByteBuffer(rowKey);
        hbaseClient.mutateRow(tableName, row, mutations, wrappedAttributes);
    }

    public void updateRows(String table, Map<String, Map<String, String>> rowBatchData) throws TException {
        boolean writeToWal = false;
        Map<String, String> attributes = new HashMap<>();
        Map<ByteBuffer, ByteBuffer> wrappedAttributes = getAttributesMap(attributes);
        ByteBuffer tableNameByte = getByteBuffer(table);
        List<BatchMutation> rowBatches = new ArrayList<>();

        for (Entry<String, Map<String, String>> batchEntry : rowBatchData.entrySet()) {
            List<Mutation> mutations = new ArrayList<>();
            for (Map.Entry<String, String> rowData : batchEntry.getValue().entrySet()) {
                mutations.add(new Mutation(false, getByteBuffer(rowData.getKey()), getByteBuffer(rowData.getValue()), writeToWal));
            }
            BatchMutation batchMutation = new BatchMutation(getByteBuffer(batchEntry.getKey()), mutations);
            rowBatches.add(batchMutation);
        }
        hbaseClient.mutateRows(tableNameByte, rowBatches, wrappedAttributes);
    }

    public void iterateResults(TRowResult result) {
        Iterator<Entry<ByteBuffer, TCell>> iter = result.columns.entrySet().iterator();
        System.out.println("RowKey:" + new String(result.getRow()));
        while (iter.hasNext()) {
            Entry<ByteBuffer, TCell> entry = iter.next();
            System.out.println("\tCol=" + new String(toBytes(entry.getKey())) + ", Value=" + new String(entry.getValue().getValue()));
        }
    }

    public List<TRowResult> scannerGetList(int id, int nbRows) throws TException {
        return hbaseClient.scannerGetList(id, nbRows);
    }

    public List<TRowResult> scannerGet(int id) throws TException {
        return hbaseClient.scannerGetList(id, 1);
    }

    public int scannerOpen(String table, String startRow, String stopRow, List<String> columns, Map<String, String> attributes) throws TException {
        ByteBuffer tableName = getByteBuffer(table);
        List<ByteBuffer> blist = getColumnsByte(columns);
        Map<ByteBuffer, ByteBuffer> wrappedAttributes = getAttributesMap(attributes);
        return hbaseClient.scannerOpenWithStop(tableName, getByteBuffer(startRow), getByteBuffer(stopRow), blist, wrappedAttributes);
    }

    public int scannerOpen(String table, String startRow, List<String> columns, Map<String, String> attributes) throws TException {
        ByteBuffer tableName = getByteBuffer(table);
        List<ByteBuffer> blist = getColumnsByte(columns);
        Map<ByteBuffer, ByteBuffer> wrappedAttributes = getAttributesMap(attributes);
        return hbaseClient.scannerOpen(tableName, getByteBuffer(startRow), blist, wrappedAttributes);
    }

    public void scannerClose(int id) throws TException {
        hbaseClient.scannerClose(id);
    }

    public List<ByteBuffer> getColumnsByte(List<String> columns) {
        List<ByteBuffer> blist = new ArrayList<>();
        for (String column : columns) {
            blist.add(getByteBuffer(column));
        }
        return blist;
    }

    protected byte[] toBytes(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.limit()];
        for (int i = 0; i < buffer.limit(); i++) {
            bytes[i] = buffer.get();
        }
        return bytes;
    }

    public List<TRowResult> getRow(String table, String row, Map<String, String> attributes) throws TException {
        ByteBuffer tableName = getByteBuffer(table);
        Map<ByteBuffer, ByteBuffer> wrappedAttributes = getAttributesMap(attributes);
        return hbaseClient.getRow(tableName, getByteBuffer(row), wrappedAttributes);
    }

    @Override
    public List<String> getTableNames() {
        ArrayList<String> tableNames = new ArrayList<>();
        try {
            for (ByteBuffer name : hbaseClient.getTableNames()) {
                tableNames.add(byteBufferToString(name));
            }
            return tableNames;
        } catch (Exception e) {
            throw new HBaseException("", e);
        }
    }

    private Map<ByteBuffer, ByteBuffer> getAttributesMap(Map<String, String> attributes) {
        Map<ByteBuffer, ByteBuffer> attributesMap = null;
        if (attributes != null && !attributes.isEmpty()) {
            attributesMap = new HashMap<>();
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                attributesMap.put(getByteBuffer(entry.getKey()), getByteBuffer(entry.getValue()));
            }
        }
        return attributesMap;
    }

    public static String byteBufferToString(ByteBuffer buffer) {
        CharBuffer charBuffer;
        try {
            charBuffer = CHAR_SET.decode(buffer);
            buffer.flip();
            return charBuffer.toString();
        } catch (Exception ex) {
            throw new HBaseException("", ex);
        }
    }

    public ByteBuffer getByteBuffer(String str) {
        return ByteBuffer.wrap(str.getBytes());
    }

    private void openTransport() throws TTransportException {
        if (socket != null) {
            socket.open();
        }
    }

    private void closeTransport() {
        if (socket != null) {
            socket.close();
        }
    }

    @Override
    public void open() {
        try {
            openTransport();
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean createTable(Class<?> clazz) {
        try {
            HBaseTable table = clazz.getAnnotation(HBaseTable.class);
            String tableName = table.tableName();

            Set<String> families = new HashSet<>();
            families.add(table.defaultFamily());

            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(HBaseColumn.class)) {
                    HBaseColumn c = f.getAnnotation(HBaseColumn.class);
                    if (!c.family().isEmpty()) {
                        families.add(c.family());
                    }
                }
            }

            List<ColumnDescriptor> columns = new ArrayList<>();
            for (String f : families) {
                columns.add(buildColumnDesc(f));
            }

            hbaseClient.createTable(ByteBuffer.wrap(tableName.getBytes()), columns);
        } catch (Exception e) {
            throw new RuntimeException("建表失败", e);
        }
        return false;
    }

    @Override
    public boolean createTable(String tableName, String... families) {
        List<ColumnDescriptor> columnDescriptors = Arrays.stream(families)
                .map(this::buildColumnDesc)
                .collect(Collectors.toList());
        try {
            hbaseClient.createTable(getByteBuffer(tableName), columnDescriptors);
            return true;
        } catch (TException e) {
            throw new RuntimeException(e);
        }
    }

    private ColumnDescriptor buildColumnDesc(String family) {
        return new ColumnDescriptor(
                ByteBuffer.wrap(family.getBytes()),  // 列族 family
                3,                                   // 版本数 HBase默认3
                "none",                              // 压缩算法
                false,                               // 是否缓存
                BloomType.NONE.name(),               // 布隆过滤器
                0,                                   // TTL 0=永不过期
                0,                                   // BLOCKSIZE 默认0
                false,                               // IN_MEMORY
                0                                    // REPLICATION_SCOPE
        );
    }

    @Override
    public boolean dropTable(String tableName) {
        try {
            if (!existsTable(tableName)) {
                return false;
            }
            ByteBuffer tableBuf = ByteBuffer.wrap(tableName.getBytes());
            hbaseClient.disableTable(tableBuf);
            hbaseClient.deleteTable(tableBuf);
            return true;
        } catch (Exception e) {
            throw new HBaseException("删表失败", e);
        }
    }

    @Override
    public boolean existsTable(String tableName) {
        return getTableNames().contains(tableName);
    }

    @Override
    public <T> void save(T obj) {
        try {
            Class<?> clazz = obj.getClass();
            HBaseTable table = clazz.getAnnotation(HBaseTable.class);
            String tableName = table.tableName();
            String defaultFamily = table.defaultFamily();

            String rowKey = getRowKey(obj);
            List<Mutation> mutations = new ArrayList<>();
            for (Field f : clazz.getDeclaredFields()) {
                if (!f.isAnnotationPresent(HBaseColumn.class)) {
                    continue;
                }
                f.setAccessible(true);
                Object value = f.get(obj);
                if (value == null) {
                    continue;
                }

                HBaseColumn c = f.getAnnotation(HBaseColumn.class);
                String family = c.family().isEmpty() ? defaultFamily : c.family();
                String qualifier = c.qualifier();

                ByteBuffer column = ByteBuffer.wrap((family + ":" + qualifier).getBytes(StandardCharsets.UTF_8));
                ByteBuffer colValue = ByteBuffer.wrap(String.valueOf(value).getBytes(StandardCharsets.UTF_8));

                // 添加一个Put操作 (列族:列, 值)
                mutations.add(new Mutation(false, column, colValue, true));
            }

            hbaseClient.mutateRow(
                    ByteBuffer.wrap(tableName.getBytes()),
                    ByteBuffer.wrap(rowKey.getBytes()),
                    mutations,
                    new HashMap<>()
            );
        } catch (Exception e) {
            throw new RuntimeException("保存失败", e);
        }
    }

    @Override
    public <T> void saveBatch(Collection<T> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        Iterator<T> iterator = rows.iterator();
        T firstRow = iterator.next();
        Class<?> clazz = firstRow.getClass();

        List<Field> fields = new ArrayList<>();
        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAnnotationPresent(HBaseColumn.class)) {
                continue;
            }
            f.setAccessible(true);
            fields.add(f);
        }

        try {
            HBaseTable table = clazz.getAnnotation(HBaseTable.class);
            String tableName = table.tableName();
            String defaultFamily = table.defaultFamily();

            List<BatchMutation> mutations = new ArrayList<>();
            for (T row : rows) {
                String rowKey = getRowKey(row);
                BatchMutation batchMutation = new BatchMutation();
                batchMutation.setRow(getByteBuffer(rowKey));
                for (Field f : fields) {
                    Object value = f.get(row);
                    if (value == null) {
                        continue;
                    }
                    HBaseColumn c = f.getAnnotation(HBaseColumn.class);
                    String family = c.family().isEmpty() ? defaultFamily : c.family();
                    String qualifier = c.qualifier();

                    ByteBuffer column = ByteBuffer.wrap((family + ":" + qualifier).getBytes(StandardCharsets.UTF_8));
                    ByteBuffer colValue = ByteBuffer.wrap(String.valueOf(value).getBytes(StandardCharsets.UTF_8));

                    // 添加一个Put操作 (列族:列, 值)
                    batchMutation.addToMutations(new Mutation(false, column, colValue, true));
                }
                mutations.add(batchMutation);
            }

            hbaseClient.mutateRows(getByteBuffer(tableName), mutations, new HashMap<>());
        } catch (Exception e) {
            throw new RuntimeException("保存失败", e);
        }
    }

    @Override
    public <T> T get(Class<T> clazz, String rowKey) {
        try {
            HBaseTable table = clazz.getAnnotation(HBaseTable.class);
            String tableName = table.tableName();

            List<TRowResult> results = hbaseClient.getRow(
                    ByteBuffer.wrap(tableName.getBytes()),
                    ByteBuffer.wrap(rowKey.getBytes()),
                    new HashMap<>()
            );

            if (results.isEmpty()) {
                return null;
            }
            TRowResult row = results.get(0);

            String defaultFamily = table.defaultFamily();
            HashBasedTable<String, String, Field> columnTable = HashBasedTable.create();
            Field rowKeyField = null;
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(HBaseRowKey.class)) {
                    rowKeyField = f;
                }
                if (!f.isAnnotationPresent(HBaseColumn.class)) {
                    continue;
                }
                f.setAccessible(true);
                HBaseColumn c = f.getAnnotation(HBaseColumn.class);
                String family = c.family().isEmpty() ? defaultFamily : c.family();
                String qualifier = c.qualifier();
                columnTable.put(family, qualifier, f);
            }

            T instance = clazz.newInstance();
            if (rowKeyField != null) {
                rowKeyField.setAccessible(true);
                rowKeyField.set(instance, rowKey);
            }

            Map<ByteBuffer, TCell> columns = row.getColumns();
            for (Entry<ByteBuffer, TCell> entry : columns.entrySet()) {
                TCell cell = entry.getValue();
                String value = byteBufferToString(cell.value);
                String column = byteBufferToString(entry.getKey());
                String[] split = column.split(":");
                Field field = columnTable.get(split[0], split[1]);
                if (field != null) {
                    field.set(instance, value);
                }
            }
            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void delete(Class<?> clazz, String rowKey) {
        try {
            String tableName = clazz.getAnnotation(HBaseTable.class).tableName();
            hbaseClient.deleteAllRow(
                    ByteBuffer.wrap(tableName.getBytes()),
                    ByteBuffer.wrap(rowKey.getBytes()),
                    new HashMap<>()
            );
        } catch (Exception e) {
            throw new RuntimeException("删除失败", e);
        }
    }

    @Override
    public <T> List<T> scan(Class<T> clazz, String startRow, int rowCount) {
        try {
            HBaseTable table = clazz.getAnnotation(HBaseTable.class);
            String tableName = HBaseUtils.getTableName(clazz);

            List<T> list = new ArrayList<>();

            List<ByteBuffer> columnsByte = new ArrayList<>();
            for (Field f : clazz.getDeclaredFields()) {
                if (!f.isAnnotationPresent(HBaseColumn.class)) {
                    continue;
                }
                f.setAccessible(true);
                HBaseColumn c = f.getAnnotation(HBaseColumn.class);
                String family = c.family().isEmpty() ? table.defaultFamily() : c.family();
                String qualifier = c.qualifier();
                columnsByte.add(getByteBuffer(family + ":" + qualifier));
            }

            int scannerId = hbaseClient.scannerOpen(
                    ByteBuffer.wrap(tableName.getBytes()),
                    ByteBuffer.wrap(startRow.getBytes(StandardCharsets.UTF_8)),
                    columnsByte,
                    new HashMap<>()
            );

            List<TRowResult> rows = hbaseClient.scannerGetList(scannerId, rowCount);

            HashBasedTable<String, String, Field> columnTable = HashBasedTable.create();
            Field rowKeyField = null;
            for (Field f : clazz.getDeclaredFields()) {
                if (f.isAnnotationPresent(HBaseRowKey.class)) {
                    rowKeyField = f;
                }
                if (!f.isAnnotationPresent(HBaseColumn.class)) {
                    continue;
                }
                f.setAccessible(true);
                HBaseColumn c = f.getAnnotation(HBaseColumn.class);
                String family = c.family();
                String qualifier = c.qualifier();
                columnTable.put(family, qualifier, f);
            }

            for (TRowResult row : rows) {
                String rowKey = byteBufferToString(row.row);
                T instance = clazz.newInstance();
                if (rowKeyField != null) {
                    rowKeyField.setAccessible(true);
                    rowKeyField.set(instance, rowKey);
                }

                Map<ByteBuffer, TCell> columns = row.getColumns();
                for (Entry<ByteBuffer, TCell> entry : columns.entrySet()) {
                    TCell cell = entry.getValue();
                    String value = byteBufferToString(cell.value);
                    String column = byteBufferToString(entry.getKey());
                    String[] split = column.split(":");
                    Field field = columnTable.get(split[0], split[1]);
                    if (field != null) {
                        field.set(instance, value);
                    }
                }
                list.add(instance);
            }
            hbaseClient.scannerClose(scannerId);
            return list;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ===================== 工具方法 =====================
    private String getRowKey(Object obj) throws Exception {
        Field f = findRowKeyField(obj.getClass());
        if (f == null) {
            throw new Exception("未找到@RowKey");
        }
        f.setAccessible(true);
        return f.get(obj).toString();
    }

    private Field findRowKeyField(Class<?> clazz) {
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(HBaseRowKey.class)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public void close() {
        closeTransport();
    }

    @Override
    public <T> List<T> scanWithFilter(Class<T> type, String filter, int rowCount) {
        TScan scan = new TScan();
        scan.setFilterString(getByteBuffer(filter));
        String tableName = HBaseUtils.getTableName(type);
        try {
            Table<String, String, Field> columnTable = HashBasedTable.create();

            Field rowKeyField = null;
            for (Field f : type.getDeclaredFields()) {
                if (f.isAnnotationPresent(HBaseRowKey.class)) {
                    rowKeyField = f;
                }
                if (!f.isAnnotationPresent(HBaseColumn.class)) {
                    continue;
                }
                f.setAccessible(true);
                HBaseColumn c = f.getAnnotation(HBaseColumn.class);
                String family = c.family();
                String qualifier = c.qualifier();
                columnTable.put(family, qualifier, f);
            }

            int scannerId = hbaseClient.scannerOpenWithScan(getByteBuffer(tableName), scan, new HashMap<>());
            List<TRowResult> rows = hbaseClient.scannerGetList(scannerId, rowCount);

            List<T> list = new ArrayList<>();
            for (TRowResult row : rows) {
                String rowKey = byteBufferToString(row.row);
                T instance = type.newInstance();
                if (rowKeyField != null) {
                    rowKeyField.setAccessible(true);
                    rowKeyField.set(instance, rowKey);
                }

                Map<ByteBuffer, TCell> columns = row.getColumns();
                for (Entry<ByteBuffer, TCell> entry : columns.entrySet()) {
                    TCell cell = entry.getValue();
                    String value = byteBufferToString(cell.value);
                    String column = byteBufferToString(entry.getKey());
                    String[] split = column.split(":");
                    Field field = columnTable.get(split[0], split[1]);
                    if (field != null) {
                        field.set(instance, value);
                    }
                }
                list.add(instance);
            }
            return list;
        } catch (TException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}