package com.kian.yun.jpaexl.domain;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.Optional;

public interface PersistenceManager {
    Sheet getSheet(String tableName);

    Sheet createSheet(String tableName);

    boolean isExist(String tableName);

    Optional<String> findValue(String tableName, Cursor cursor);

    void insertValue(String tableName, Cursor cursor, String value);

    void flush();
}
