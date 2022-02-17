package com.kian.yun.jpaexl.domain;

import org.apache.poi.ss.usermodel.Sheet;

import java.util.Optional;

public interface PersistenceManager {
    Optional<String> findValue(String tableName, Cursor cursor);

    void insertValue(String tableName, Cursor cursor, String value);

    void flush();

    Sheet getSheet(String tableName);

    Sheet createSheet(String tableName);

    boolean isExist(String tableName);
}