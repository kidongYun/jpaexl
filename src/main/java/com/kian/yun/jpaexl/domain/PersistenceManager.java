package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.Constants;
import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

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
            log.debug(e.toString());
        }
    }

    public String findValue(String sheetName, int rowCursor, int cellCursor) {
        return Optional.ofNullable(workbook.getSheet(sheetName).getRow(rowCursor).getCell(cellCursor))
                .orElseThrow(() -> new JpaexlException(JpaexlCode.FAIL_TO_FIND_DATA)).getStringCellValue();
    }

    public Tuple findById(String sheetName, String id) {
        int idRowCursor = findRowCursorById(sheetName, id);
        log.info(idRowCursor + "");

        for(int i=Constants.CURSOR_CELL_INITIAL_VALUE; i<=Constants.CURSOR_CELL_MAX_VALUE; i++) {
            String data = findValue(sheetName, idRowCursor, i);

            if(Objects.isNull(data)) {
                break;
            }

            log.info(findValue(sheetName, idRowCursor, i));
        }

        return Tuple.empty();
    }

    private int findCursorIdCell(String sheetName, int size) {
        for(int i=1; i<=size; i++) {
            if("ID".equalsIgnoreCase(findValue(sheetName, Constants.SCHEMA_NAME_CURSOR, i))) {
                return i;
            }
        }

        throw new JpaexlException(JpaexlCode.FAIL_TO_FIND_ID_CELL_IN_SCHEMA);
    }

    private int findRowCursorById(String sheetName, String id) {
        int idCellCursor = findCursorIdCell(sheetName, 4);

        return IntStream.range(Constants.CURSOR_ROW_INITIAL_VALUE, Constants.CURSOR_ROW_MAX_VALUE)
                .filter(v -> id.equals(findValue(sheetName, v, idCellCursor)))
                .findFirst()
                .orElseThrow(() -> new JpaexlException(JpaexlCode.FAIL_TO_FIND_ROW_BY_ID));
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
