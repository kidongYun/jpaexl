package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.Constants;
import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Getter
@Setter
public class SimpleTable implements Table {
    private final PersistenceManager persistenceManager;
    private Sheet sheet;
    private Cursor cursor;
    private int cellSize;
    private int rowSize;

    public static SimpleTable getInstance(String name) {
        SimpleTable table = new SimpleTable();
        table.setSheet(table.getPersistenceManager().getSheet(name));
        table.setCellSize(table.findCellSize());
        table.setRowSize(table.findRowSize());
        table.setCursor(Cursor.of(table.getRowSize(), Constants.CUR_CELL_INIT_VAL));

        return table;
    }

    public static SimpleTable createOrGetInstance(String name, List<Schema<?>> schemas) {
        SimpleTable table = new SimpleTable();

        boolean isNonExist = !table.getPersistenceManager().isExist(name);
        table.setSheet((isNonExist) ? table.getPersistenceManager().createSheet(name) : table.getPersistenceManager().getSheet(name));
        log.info("'{}' sheet is empty ?... : {}", name, isNonExist);

        table.setCellSize(Constants.CUR_CELL_INIT_VAL + schemas.size() - 1);
        table.setRowSize(table.findRowSize());
        table.setCursor(Cursor.of(table.getRowSize(), Constants.CUR_CELL_INIT_VAL));

        if(isNonExist) {
            table.initTable(schemas);
        }

        return table;
    }

    private SimpleTable() {
        this.persistenceManager = PersistenceManager.getInstance();
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
        return List.of(findTuple(Cursor.row(Constants.CUR_ROW_INIT_VAL))
                .orElseThrow(() -> new JpaexlException(JpaexlCode.FAIL_TO_FIND_SCHEMA_TYPE)));
    }

//    public Tuple findById(String sheetName, String id) {
//        int cursorIdRow = findCursorIdRow(sheetName, id);
//
//        Tuple tuple = Tuple.empty();
//
//        for(int i=Constants.CUR_CELL_INIT_VAL; i<=Constants.CUR_CELL_MAX_VAL; i++) {
//            Data<?> data = findData(sheetName, cursorIdRow, i).orElse(null);
//
//            if(Objects.isNull(data)) {
//                break;
//            }
//
//            tuple.add(data);
//        }
//
//        return tuple;
//    }

    private void initTable(List<Schema<?>> schemas) {
        List<String> schemaNames = schemas.stream().map(Schema::getName).collect(Collectors.toList());
        insertRow(schemaNames, Constants.CUR_ROW_SCHEMA_NAME);

        List<String> schemaTypes = schemas.stream().map(s -> s.getType().getName()).collect(Collectors.toList());
        insertRow(schemaTypes, Constants.CUR_ROW_SCHEMA_TYPE);

        for(int i=0; i<schemas.size(); i++) {
            for(int j=0; j<schemas.get(i).getAnnotations().size(); j++) {
                insertValue(schemas.get(i).getAnnotations().get(j).annotationType().getName(), Constants.SCHEMA_ANN_START_CUR + j, Constants.CUR_CELL_INIT_VAL + i);
            }
        }
    }

//    private int findRowCur(String value) {
//        int idCellCursor = findCursorIdCell(sheetName, 4);
//
//        return IntStream.range(Constants.CUR_ROW_INIT_VAL, Constants.CURSOR_ROW_MAX_VALUE)
//                .filter(v -> id.equals(findValue(sheetName, v, idCellCursor).orElse(null)))
//                .findFirst()
//                .orElseThrow(() -> new JpaexlException(JpaexlCode.FAIL_TO_FIND_ROW_BY_ID));
//    }
//
//    private int findCellCur(String value, int rowCur) {
//        for(int i=1; i<=cellSize; i++) {
//            if("ID".equalsIgnoreCase(findValue(sheetName, Constants.SCHEMA_NAME_CUR, i).orElse(null))) {
//                return i;
//            }
//        }
//
//        throw new JpaexlException(JpaexlCode.FAIL_TO_FIND_ID_CELL_IN_SCHEMA);
//    }
//
//    private Cursor findCursor(String value) {
//        return Cursor.base();
//    }

    @SuppressWarnings("unchecked")
    private <T> Optional<Schema<T>> findSchema(Cursor cursor) {
        String schemaType = findValue(Cursor.of(Constants.CUR_ROW_SCHEMA_TYPE, cursor.getCell()))
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_TYPE));

        String schemaName = findValue(Cursor.of(Constants.CUR_ROW_SCHEMA_NAME, cursor.getCell()))
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_NAME));

        try {
            Schema<T> schema = Schema.of((Class<T>) Class.forName(schemaType), schemaName);
            return Optional.of(schema);

        } catch (ClassNotFoundException | JpaexlException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private int findCellSize() {
        int size = 0;
        for(int i=Constants.CUR_CELL_INIT_VAL; i<Constants.CUR_CELL_MAX_VAL; i++) {
            Optional<String> valueOpt = findValue(Cursor.of(Constants.CUR_ROW_SCHEMA_NAME, i));

            if(valueOpt.isEmpty()) {
                break;
            }

            size++;
        }

        return size;
    }

    private int findRowSize() {
        for(int i=Constants.CUR_ROW_INIT_VAL; i<Constants.CUR_ROW_MAX_VAL; i++) {
            Optional<String> valueOpt = findValue(Cursor.of(i, Constants.CUR_CELL_INIT_VAL));

            if(valueOpt.isPresent()) {
                continue;
            }

            return i;
        }

        return 0;
    }

    private List<Tuple> findTuples(Cursor from, Cursor to) {
        return IntStream.range(from.getRow(), to.getRow())
                .mapToObj(i -> findTuple(Cursor.row(i)).orElseThrow(() -> new JpaexlException(JpaexlCode.FAIL_TO_FIND_TUPLE)))
                .collect(Collectors.toList());
    }

    private Optional<Tuple> findTuple(Cursor cursor) {
        List<Data<?>> data = IntStream.range(Constants.CUR_CELL_INIT_VAL, Constants.CUR_CELL_INIT_VAL + cellSize)
                .mapToObj(i -> findData(Cursor.of(cursor.getRow(), i)).orElseThrow(() -> new JpaexlException(JpaexlCode.FAIL_TO_FIND_DATA)))
                .collect(Collectors.toList());

        return Optional.of(Tuple.of(data));
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<Data<T>> findData(Cursor cursor) {
        Schema<T> schema = (Schema<T>) findSchema(cursor).orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA));

        String value = findValue(Cursor.of(cursor.getRow(), cursor.getCell()))
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_VALUE));

        try {
            Constructor<T> constructor = schema.getType().getConstructor(String.class);
            T instance = constructor.newInstance(value);
           return Optional.of(Data.of(schema, instance));

        } catch (NoSuchMethodException
                | InvocationTargetException
                | InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
            return Optional.empty();

        } catch (JpaexlException e) {
            return Optional.empty();
        }
    }

    private Optional<String> findValue(Cursor cursor) {
        Row row = sheet.getRow(cursor.getRow());

        if(Objects.isNull(row)) {
            return Optional.empty();
        }

        Cell cell = row.getCell(cursor.getCell());

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
            insertValue(value, rowCur, cursor.shiftCell(cellSize));
        }
    }

    private void insertValue(String value, int rowCur, int cellCur) {
        Row row = sheet.getRow(rowCur);

        if(Objects.isNull(row)) {
            row = sheet.createRow(rowCur);
        }

        row.createCell(cellCur).setCellValue(value);

        log.info("Inserted '{}' into row : '{}', cell : '{}' at excel...", value, rowCur, cellCur);
    }
}
