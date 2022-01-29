package com.kian.yun.jpaexl.domain;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
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
    private int rowCursor;
    private int cellCursor;

    private PersistenceManager(Workbook workbook, int rowCursor, int cellCursor) {
        this.workbook = workbook;
        this.rowCursor = rowCursor;
        this.cellCursor = cellCursor;
    }

    public static PersistenceManager getInstance() {
        return getInstance(new SXSSFWorkbook(), 4, 1);
    }

    private synchronized static PersistenceManager getInstance(Workbook workbook, int rowCursor, int cellCursor) {
        if(singleton == null) {
            singleton = new PersistenceManager(workbook, rowCursor, cellCursor);
        }

        return singleton;
    }

    public void insert(String tableName, Tuple tuple) {
        Sheet table = (isExist(tableName)) ? this.getTable(tableName) : this.createTable(tableName);
        Row row = table.createRow(this.getRowCursor());

        for(Data<?> data : tuple.getTuple()) {
            row.createCell(this.getCellCursor()).setCellValue(String.valueOf(data.getValue()));
        }
    }

    public void flush() {
        try {
            FileOutputStream fos = new FileOutputStream("./jpaexl.xlsx");
            this.workbook.write(fos);
        } catch (IOException e) {
            log.debug(e.toString());
        }
    }

    private Sheet getTable(String tableName) {
        return workbook.getSheet(tableName);
    }

    private <T> Sheet createTable(String tableName, Schema<T>... schemas) {
        Sheet table = workbook.createSheet(tableName);

        Row row = table.createRow(this.getSchemaNameCursor());

        for(Schema<T> schema : schemas) {
        }

        return workbook.createSheet(tableName);
    }

    private int getRowCursor() {
        return rowCursor++;
    }

    private int getCellCursor() {
        return cellCursor++;
    }

    private int getSchemaNameCursor() {
        return 2;
    }

    private int getSchemaTypeCursor() {
        return 3;
    }

    private boolean isExist(String tableName) {
        return !Objects.isNull(this.workbook.getSheet(tableName));
    }
}
