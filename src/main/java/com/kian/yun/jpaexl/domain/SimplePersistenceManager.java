package com.kian.yun.jpaexl.domain;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class SimplePersistenceManager implements PersistenceManager {
    private static SimplePersistenceManager singleton;
    private final Workbook workbook;

    private SimplePersistenceManager(Workbook workbook) {
        this.workbook = workbook;
    }

    public static SimplePersistenceManager getInstance() {
        try {
            FileInputStream file = new FileInputStream("./jpaexl.xlsx");
            return getInstance(new XSSFWorkbook(file));
        } catch (FileNotFoundException
                | EmptyFileException e) {
            return getInstance(new XSSFWorkbook());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private synchronized static SimplePersistenceManager getInstance(Workbook workbook) {
        if(singleton == null) {
            singleton = new SimplePersistenceManager(workbook);
        }

        return singleton;
    }

    @Override
    public Sheet getSheet(String tableName) {
        return workbook.getSheet(tableName);
    }

    @Override
    public Sheet createSheet(String tableName) {
        return workbook.createSheet(tableName);
    }

    @Override
    public boolean isExist(String tableName) {
        return !Objects.isNull(this.workbook.getSheet(tableName));
    }

    @Override
    public Optional<String> findValue(String tableName, Cursor cursor) {
        return Optional.empty();
    }

    @Override
    public void insertValue(String tableName, Cursor cursor, String value) {

    }

    @Override
    public void flush() {
        try {
            FileOutputStream fos = new FileOutputStream("./jpaexl.xlsx");
            this.workbook.write(fos);
        } catch (IOException e) {
            log.info(e.toString());
        }
    }
}
