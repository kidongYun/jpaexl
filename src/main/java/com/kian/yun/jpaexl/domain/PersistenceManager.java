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

@Slf4j
public class PersistenceManager {
    private static PersistenceManager singleton;
    private final Workbook workbook;

    private PersistenceManager(Workbook workbook) {
        this.workbook = workbook;
    }

    public static PersistenceManager getInstance() {
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
            log.info(e.toString());
        }
    }

    public Sheet getSheet(String tableName) {
        return workbook.getSheet(tableName);
    }

    public Sheet createSheet(String tableName) {
        return workbook.createSheet(tableName);
    }

    public boolean isExist(String tableName) {
        return !Objects.isNull(this.workbook.getSheet(tableName));
    }
}
