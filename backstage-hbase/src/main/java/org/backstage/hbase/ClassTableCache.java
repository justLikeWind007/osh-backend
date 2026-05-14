package org.backstage.hbase;

import org.apache.hbase.thirdparty.com.google.common.collect.HashBasedTable;
import org.apache.hbase.thirdparty.com.google.common.collect.Table;

import java.lang.reflect.Field;
import java.util.concurrent.locks.ReentrantLock;

class ClassTableCache {

    Table<String, String, Field> fieldCache = HashBasedTable.create();

    ReentrantLock lock = new ReentrantLock();

    public Field getField(String className, String fieldName) {
        return null;
    }
}
