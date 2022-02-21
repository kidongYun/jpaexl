package com.kian.yun.jpaexl.domain;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EmptyFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
    public Optional<String> find(String tableName, Cursor cursor) {
        Sheet sheet = getSheet(tableName);

        if(Objects.isNull(sheet)) {
            return Optional.empty();
        }

        Row row = sheet.getRow(cursor.getRow());

        if(Objects.isNull(row)) {
            return Optional.empty();
        }

        Cell cell = row.getCell(cursor.getCell());

        if(Objects.isNull(cell)) {
            return Optional.empty();
        }

        String value = cell.getStringCellValue();
        log.info("found '{}' from row : '{}', cell : '{}' at table named '{}'", value, cursor.getRow(), cursor.getCell(), tableName);


        return Optional.of(value);
    }

    @Override
    public void insert(String tableName, Cursor cursor, String value) {
        Sheet sheet = workbook.getSheet(tableName);

        if(Objects.isNull(sheet)) {
            sheet = workbook.createSheet(tableName);
        }

        Row row = sheet.getRow(cursor.getRow());

        if(Objects.isNull(row)) {
            row = sheet.createRow(cursor.getRow());
        }

        row.createCell(cursor.getCell()).setCellValue(value);

        log.info("Inserted '{}' into row : '{}', cell : '{}' at excel...", value, cursor.getRow(), cursor.getCell());
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
