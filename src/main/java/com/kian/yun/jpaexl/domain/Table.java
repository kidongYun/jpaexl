package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.stream.Collectors;

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
        createTuple(row, tuple.getTuple().stream()
                .map(data -> String.valueOf(data.getValue()))
                .collect(Collectors.toList()));

        persistenceManager.flush();
    }

    public void findById() {
        log.info(persistenceManager.findValue(name, 1, 2));
        log.info(persistenceManager.findById(name, "1") + "");
    }

    private Sheet createTable(Sheet table, List<Schema<?>> schemas) {
        Row row = table.createRow(Constants.SCHEMA_NAME_CURSOR);
        createTuple(row, schemas.stream().map(Schema::getName).collect(Collectors.toList()));

        Row row2 = table.createRow(Constants.SCHEMA_TYPE_CURSOR);
        createTuple(row2, schemas.stream().map(s -> String.valueOf(s.getType())).collect(Collectors.toList()));

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

    private void createTuple(Row row, List<String> strings) {
        for(String string : strings) {
            row.createCell(this.getCellCursor()).setCellValue(string);
        }

        initCellCursor();
    }
}
