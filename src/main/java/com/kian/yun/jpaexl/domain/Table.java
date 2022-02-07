package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Table {
    private final PersistenceManager persistenceManager;
    private final String name;
    private int rowCursor;
    private int cellCursor;

    public static Table getInstance(String name) {
        return new Table(name, Constants.CURSOR_ROW_INITIAL_VALUE, Constants.CURSOR_CELL_INITIAL_VALUE);
    }

    private Table(String name, int rowCursor, int cellCursor) {
        this.persistenceManager = PersistenceManager.getInstance();
        this.name = name;
        this.rowCursor = rowCursor;
        this.cellCursor = cellCursor;
    }

    public void insert(Tuple tuple) {
        Sheet table = (persistenceManager.isExist(name)) ?
                persistenceManager.getSheet(name) :
                createTable(persistenceManager.createSheet(name), Schema.of(tuple));

        Row row = table.createRow(this.getRowCursor());

        insertTuple(row, tuple);

        persistenceManager.flush();
    }

    public Tuple findById(String id) {
        return persistenceManager.findById(name, id);
    }

    private Sheet createTable(Sheet table, List<Schema<?>> schemas) {
        Row schemaName = table.createRow(Constants.SCHEMA_NAME_CURSOR);
        insertRow(schemaName, schemas.stream().map(Schema::getName).collect(Collectors.toList()));

        Row schemaType = table.createRow(Constants.SCHEMA_TYPE_CURSOR);

        for(Schema<?> schema : schemas) {

            IntStream.range(Constants.SCHEMA_ANNOTATION_START_CURSOR, Constants.SCHEMA_ANNOTATION_START_CURSOR + schema.getAnnotations().size())
                            .forEach(rowCursor -> {
                                persistenceManager.
                            });
            schema.getAnnotations()
            Row schemaAnnotation = table.createRow(i);

            int finalI = i;
            insertRow(schemaAnnotation, schemas.stream().map(s -> String.valueOf(s.getAnnotations().get(finalI))).collect(Collectors.toList()));
        }

        return table;
    }

    private int getRowCursor() {
        return rowCursor++;
    }

    private int getCellCursor() {
        return cellCursor++;
    }

    private void initCellCursor() {
        cellCursor = Constants.CURSOR_CELL_INITIAL_VALUE;
    }

    private void insertTuple(Row row, Tuple tuple) {
        insertRow(row, tuple.getTuple().stream().map(d -> String.valueOf(d.getValue())).collect(Collectors.toList()));
    }

    private void insertRow(Row row, List<String> strings) {
        for(String string : strings) {
            row.createCell(this.getCellCursor()).setCellValue(string);
        }

        initCellCursor();
    }
}
