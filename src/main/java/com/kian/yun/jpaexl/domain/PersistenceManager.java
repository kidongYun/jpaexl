package com.kian.yun.jpaexl.domain;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Slf4j
public class PersistenceManager {
    private static PersistenceManager singleton;
    private final Workbook workbook;

    private PersistenceManager(Workbook workbook) {
        this.workbook = workbook;
    }

    public static PersistenceManager getInstance() {
        return getInstance(new SXSSFWorkbook());
    }

    private synchronized static PersistenceManager getInstance(Workbook workbook) {
        if(singleton == null) {
            singleton = new PersistenceManager(workbook);
        }

        return singleton;
    }

    public void flush() {
        try {
            FileOutputStream fos = new FileOutputStream("./jpaexl.xlsx");
            this.workbook.write(fos);
        } catch (IOException e) {
            log.debug(e.toString());
        }
    }

    public Sheet getTable(String tableName) {
        return (isExist(tableName)) ? this.getSheet(tableName) : this.createSheet(tableName);
    }

    public Sheet getSheet(String tableName) {
        return workbook.getSheet(tableName);
    }

    public Sheet createSheet(String tableName) {
        return workbook.createSheet(tableName);
    }

//    private Sheet createTable(String tableName, List<Schema<?>> schemas) {
//        Sheet table = workbook.createSheet(tableName);
//
//        Row row = table.createRow(Constants.SCHEMA_NAME_CURSOR);
//
//        for(Schema<?> schema : schemas) {
//            row.createCell(this.getCellCursor()).setCellValue(schema.getName());
//        }
//
//        return table;
//    }

    public boolean isExist(String tableName) {
        return !Objects.isNull(this.workbook.getSheet(tableName));
    }
}
