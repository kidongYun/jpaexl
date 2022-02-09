package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.Constants;
import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;
import com.kian.yun.jpaexl.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    public void insertValue(String sheetName, int rowCursor, int cellCursor) {

    }

    public Optional<Data<?>> findData(String sheetName, int rowCursor, int cellCursor) {
       try {
           String schemaType = findValue(sheetName, Constants.SCHEMA_TYPE_CUR, cellCursor)
                   .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_TYPE));

           String schemaName = findValue(sheetName, Constants.SCHEMA_NAME_CUR, cellCursor)
                   .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_NAME));

           String value = findValue(sheetName, rowCursor, cellCursor)
                   .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_DATA));

           Class<?> clazz = Class.forName(ReflectionUtils.classType(schemaType));
           Constructor<?> constructor = clazz.getConstructor(String.class);
           Object obj = constructor.newInstance(value);

//           return Optional.of(Data.of(schemaName, obj));

           return Optional.empty();

       } catch (ClassNotFoundException
               | NoSuchMethodException
               | InvocationTargetException
               | InstantiationException
               | IllegalAccessException
               e) {
           e.printStackTrace();
           return Optional.empty();
       } catch (JpaexlException e) {
           return Optional.empty();
       }
    }

    public Tuple findById(String sheetName, String id) {
        int cursorIdRow = findCursorIdRow(sheetName, id);

        Tuple tuple = Tuple.empty();

        for(int i=Constants.CUR_CELL_INIT_VAL; i<=Constants.CURSOR_CELL_MAX_VALUE; i++) {
            Data<?> data = findData(sheetName, cursorIdRow, i).orElse(null);

            if(Objects.isNull(data)) {
                break;
            }

            tuple.add(data);
        }

        return tuple;
    }

    private Optional<String> findValue(String sheetName, int rowCursor, int cellCursor) {
        Cell cell = workbook.getSheet(sheetName).getRow(rowCursor).getCell(cellCursor);

        if(Objects.isNull(cell)) {
            return Optional.empty();
        }

        return Optional.of(cell.getStringCellValue());
    }

    private int findCursorIdCell(String sheetName, int size) {
        for(int i=1; i<=size; i++) {
            if("ID".equalsIgnoreCase(findValue(sheetName, Constants.SCHEMA_NAME_CUR, i).orElse(null))) {
                return i;
            }
        }

        throw new JpaexlException(JpaexlCode.FAIL_TO_FIND_ID_CELL_IN_SCHEMA);
    }

    private int findCursorIdRow(String sheetName, String id) {
        int idCellCursor = findCursorIdCell(sheetName, 4);

        return IntStream.range(Constants.CUR_ROW_INIT_VAL, Constants.CURSOR_ROW_MAX_VALUE)
                .filter(v -> id.equals(findValue(sheetName, v, idCellCursor).orElse(null)))
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
