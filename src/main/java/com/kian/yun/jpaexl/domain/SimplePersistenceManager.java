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
    public Optional<String> findValue(String tableName, Cursor cursor) {
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
        log.info("found '{}' from Cursor : [{}. {}] at table named '{}'", value, cursor.getRow(), cursor.getCell(), tableName);


        return Optional.of(value);
    }

    @Override
    public Optional<Cursor> searchValue(String tableName, String target, Cursor from, Cursor to) {
        Sheet sheet = getSheet(tableName);

        if(Objects.isNull(sheet)) {
            return Optional.empty();
        }

        for(int i=from.getRow(); i<=to.getRow(); i++) {
            for(int j=from.getCell(); j<=to.getCell(); j++) {
                Cursor cursor = Cursor.of(i, j);
                Optional<String> valueOpt = this.findValue(tableName, cursor);

                if(valueOpt.isEmpty()) {
                    continue;
                }

                if(target.equals(valueOpt.get())) {
                    return Optional.of(cursor);
                }

                log.info("DEBUG [{}, {}]", i, j);
            }
        }

        return Optional.empty();
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

        log.info("Inserted '{}' into Cursor : [{}. {}] at excel...", value, cursor.getRow(), cursor.getCell());
    }

    @Override
    public Integer cellSize(String tableName) {
        int size = 0;

        for(int i=Cursor.CELL_INIT_VAL; i<Cursor.CELL_MAX_VAL; i++) {
            Optional<String> valueOpt = this.findValue(tableName, Cursor.of(Cursor.ROW_SCHEMA_NAME, i));

            if(valueOpt.isEmpty()) {
                break;
            }

            size++;
        }

        return size;
    }

    @Override
    public Integer rowSize(String tableName) {
        for(int i=Cursor.ROW_INIT_VAL; i<Cursor.ROW_MAX_VAL; i++) {
            Optional<String> valueOpt = this.findValue(tableName, Cursor.of(i).shift(cellSize(tableName)));

            if(valueOpt.isPresent()) {
                continue;
            }

            return i;
        }

        return 0;
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
