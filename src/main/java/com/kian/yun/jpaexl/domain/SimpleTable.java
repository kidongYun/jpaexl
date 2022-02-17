package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.Constants;
import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Getter
@Setter
public class SimpleTable<T> implements Table<T> {
    private final SimplePersistenceManager simplePersistenceManager;
    private final Class<T> clazz;

    private Integer cellSize;
    private Integer rowSize;
    private Cursor cursor;

    public static <T> SimpleTable<T> getInstance(Class<T> clazz) {
        SimpleTable<T> table = new SimpleTable<>(clazz);

        boolean isExist = table.isExist();
        log.info("'{}' table is exist ?... : {}", clazz.getName(), isExist);

        table.setRowSize(isExist ? table.findRowSize() : 0);
        table.setCellSize(isExist ? table.findCellSize() : clazz.getFields().length);
        table.setCursor(Cursor.of(table.getRowSize(), Constants.CUR_CELL_INIT_VAL));

        return table;
    }

    private SimpleTable(Class<T> clazz) {
        this.simplePersistenceManager = SimplePersistenceManager.getInstance();
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return getClazz().getName();
    }

    private Sheet getSheet() {
        return getSimplePersistenceManager().getSheet(clazz.getName());
    }

    private Sheet createSheet() {
        return getSimplePersistenceManager().createSheet(clazz.getName());
    }

    private boolean isExist() {
        return getSimplePersistenceManager().isExist(clazz.getName());
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

    private void initTable(List<SimpleSchema<?>> simpleSchemas) {
        List<String> schemaNames = simpleSchemas.stream().map(SimpleSchema::getName).collect(Collectors.toList());
        insertRow(schemaNames, Constants.CUR_ROW_SCHEMA_NAME);

        List<String> schemaTypes = simpleSchemas.stream().map(s -> s.getType().getName()).collect(Collectors.toList());
        insertRow(schemaTypes, Constants.CUR_ROW_SCHEMA_TYPE);
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

//    @SuppressWarnings("unchecked")
//    private <T> Optional<SimpleSchema<T>> findSchema(Cursor cursor) {
//        String schemaType = findValue(Cursor.of(Constants.CUR_ROW_SCHEMA_TYPE, cursor.getCell()))
//                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_TYPE));
//
//        String schemaName = findValue(Cursor.of(Constants.CUR_ROW_SCHEMA_NAME, cursor.getCell()))
//                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_NAME));
//
//        try {
//            SimpleSchema<T> simpleSchema = SimpleSchema.of((Class<T>) Class.forName(schemaType), schemaName);
//            return Optional.of(simpleSchema);
//
//        } catch (ClassNotFoundException | JpaexlException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
//    }

    private int findCellSize() {
        int size = 0;
        for(int i=Constants.CUR_CELL_INIT_VAL; i<Constants.CUR_CELL_MAX_VAL; i++) {
            Optional<String> valueOpt = getSimplePersistenceManager().findValue(clazz.getName(), Cursor.of(Constants.CUR_ROW_SCHEMA_NAME, i));

            if(valueOpt.isEmpty()) {
                break;
            }

            size++;
        }

        return size;
    }

    private int findRowSize() {
        for(int i=Constants.CUR_ROW_INIT_VAL; i<Constants.CUR_ROW_MAX_VAL; i++) {
            Optional<String> valueOpt = getSimplePersistenceManager().findValue(clazz.getName(), Cursor.of(i, Constants.CUR_CELL_INIT_VAL));

            if(valueOpt.isPresent()) {
                continue;
            }

            return i;
        }

        return 0;
    }

    private List<SimpleTuple<T>> findTuples(Cursor from, Cursor to) {
        return IntStream.range(from.getRow(), to.getRow())
                .mapToObj(i -> findTuple(Cursor.row(i)).orElseThrow(() -> new JpaexlException(JpaexlCode.FAIL_TO_FIND_TUPLE)))
                .collect(Collectors.toList());
    }

    private void insertTuple(SimpleTuple simpleTuple, int rowCur) {
        insertRow(simpleTuple.getValue().stream().map(d -> String.valueOf(d.getValue())).collect(Collectors.toList()), rowCur);
    }

    private void insertRow(List<String> values, int rowCur) {
        for(String value : values) {
            insertValue(value, rowCur, cursor.shiftCell(cellSize));
        }
    }
}
