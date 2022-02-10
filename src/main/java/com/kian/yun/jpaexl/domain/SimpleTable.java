package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.Constants;
import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;
import com.kian.yun.jpaexl.util.ReflectionUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Setter
public class SimpleTable implements Table {
    private final PersistenceManager persistenceManager;
    private final Sheet sheet;
    private Cursor cursor;
    private int cellSize;

    public static SimpleTable getInstance(String name, List<Schema<?>> schemas) {
        return new SimpleTable(name, schemas, Constants.CUR_ROW_INIT_VAL, Constants.CUR_CELL_INIT_VAL);
    }

    private SimpleTable(String name, List<Schema<?>> schemas, int rowCur, int cellCur) {
        this.persistenceManager = PersistenceManager.getInstance();
        this.sheet = (persistenceManager.isExist(name)) ? persistenceManager.getSheet(name) : persistenceManager.createSheet(name);
        this.cursor = Cursor.of(rowCur, cellCur);
        this.cellSize = Constants.CUR_CELL_INIT_VAL + schemas.size() - 1;

        initTable(schemas);
    }

    @Override
    public void insert(Tuple tuple) {
        insertTuple(tuple, cursor.shiftRow());
        persistenceManager.flush();
    }

    @Override
    public Tuple findById(String id) {
        return Tuple.empty();
    }

    @Override
    public Iterable<Tuple> findAll() {
        return null;
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

    private void initTable(List<Schema<?>> schemas) {
        List<String> schemaNames = schemas.stream().map(Schema::getName).collect(Collectors.toList());
        insertRow(schemaNames, Constants.SCHEMA_NAME_CUR);

        List<String> schemaTypes = schemas.stream().map(s -> s.getType().getName()).collect(Collectors.toList());
        insertRow(schemaTypes, Constants.SCHEMA_TYPE_CUR);

        for(int i=0; i<schemas.size(); i++) {
            for(int j=0; j<schemas.get(i).getAnnotations().size(); j++) {
                insertValue(schemas.get(i).getAnnotations().get(j).annotationType().getName(), Constants.SCHEMA_ANN_START_CUR + j, Constants.CUR_CELL_INIT_VAL + i);
            }
        }
    }

    private int findRowCur(String value) {
        int idCellCursor = findCursorIdCell(sheetName, 4);

        return IntStream.range(Constants.CUR_ROW_INIT_VAL, Constants.CURSOR_ROW_MAX_VALUE)
                .filter(v -> id.equals(findValue(sheetName, v, idCellCursor).orElse(null)))
                .findFirst()
                .orElseThrow(() -> new JpaexlException(JpaexlCode.FAIL_TO_FIND_ROW_BY_ID));
    }

    private int findCellCur(String value, int rowCur) {
        for(int i=1; i<=cellSize; i++) {
            if("ID".equalsIgnoreCase(findValue(sheetName, Constants.SCHEMA_NAME_CUR, i).orElse(null))) {
                return i;
            }
        }

        throw new JpaexlException(JpaexlCode.FAIL_TO_FIND_ID_CELL_IN_SCHEMA);
    }

    private Cursor findCursor(String value) {
        return Cursor.base();
    }

    private Optional<Tuple> findTuple(Cursor cursor) {
        List<Data<?>> data = IntStream.range(Constants.CUR_CELL_INIT_VAL, Constants.CUR_CELL_INIT_VAL + cellSize)
                .mapToObj(i -> findData(Cursor.of(cursor.getRow(), i)))
                .map(opt -> opt.orElse(null))
                .collect(Collectors.toList());

        return Optional.of(Tuple.of(data));
    }

    private Optional<Data<?>> findData(Cursor cursor) {
        try {
            String schemaType = findValue(Cursor.of(Constants.SCHEMA_TYPE_CUR, cursor.getCell()))
                    .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_TYPE));

            String schemaName = findValue(Cursor.of(Constants.SCHEMA_NAME_CUR, cursor.getCell()))
                    .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_NAME));

            List<String> anns = new ArrayList<>();
            for(int i=Constants.SCHEMA_ANN_START_CUR; i<=Constants.SCHEMA_ANN_END_CUR; i++) {
                Optional<String> valueOpt = findValue(Cursor.of(i, cursor.getCell()));

                if(valueOpt.isEmpty()) {
                    break;
                }

                anns.add(valueOpt.get());
            }

            String value = findValue(Cursor.of(cursor.getRow(), cursor.getCell()))
                    .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_DATA));

            Class<?> clazz = Class.forName(ReflectionUtils.classType(schemaType));
            Constructor<?> constructor = clazz.getConstructor(String.class);
            Object obj = constructor.newInstance(value);

           return Optional.of(Data.of(schemaName, obj));

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

    private Optional<String> findValue(Cursor cursor) {
        Cell cell = sheet.getRow(cursor.getRow()).getCell(cursor.getCell());

        if(Objects.isNull(cell)) {
            return Optional.empty();
        }

        return Optional.of(cell.getStringCellValue());
    }

    private void insertTuple(Tuple tuple, int rowCur) {
        insertRow(tuple.getValue().stream().map(d -> String.valueOf(d.getValue())).collect(Collectors.toList()), rowCur);
    }

    private void insertRow(List<String> values, int rowCur) {
        for(String value : values) {
            insertValue(value, rowCur, cursor.shiftRow());
        }
    }

    private void insertValue(String value, int rowCur, int cellCur) {
        Row row = sheet.getRow(rowCur);

        if(Objects.isNull(row)) {
            row = sheet.createRow(rowCur);
        }

        row.createCell(cellCur).setCellValue(value);
    }
}
