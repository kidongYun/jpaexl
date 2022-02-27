package com.kian.yun.jpaexl.domain;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.Optional;

public interface PersistenceManager {
    Sheet getSheet(String tableName);

    Sheet createSheet(String tableName);

    boolean isExist(String tableName);

    Optional<String> findValue(String tableName, Cursor cursor);

    Optional<Cursor> searchValue(String tableName, String target, Cursor from, Cursor to);

    void insert(String tableName, Cursor cursor, String value);

    Integer cellSize(String tableName);

    Integer rowSize(String tableName);

    void flush();
}
