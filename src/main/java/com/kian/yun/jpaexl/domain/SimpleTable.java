package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.Constants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Setter
public class SimpleTable implements Table {
    private final PersistenceManager persistenceManager;
    private final Sheet sheet;
    private int rowCur;
    private int cellCur;
    private int cellSize;

    public static SimpleTable getInstance(String name, List<Schema<?>> schemas) {
        return new SimpleTable(name, schemas, Constants.CUR_ROW_INIT_VAL, Constants.CUR_CELL_INIT_VAL);
    }

    private SimpleTable(String name, List<Schema<?>> schemas, int rowCur, int cellCur) {
        this.persistenceManager = PersistenceManager.getInstance();
        this.sheet = (persistenceManager.isExist(name)) ? persistenceManager.getSheet(name) : persistenceManager.createSheet(name);
        this.rowCur = rowCur;
        this.cellCur = cellCur;
        this.cellSize = Constants.CUR_CELL_INIT_VAL + schemas.size() - 1;

        initTable(schemas);
    }

    @Override
    public void insert(Tuple tuple) {
        insertTuple(tuple, getRowCur());
        persistenceManager.flush();
    }

    @Override
    public Tuple findById(String id) {
//        return persistenceManager.findById(name, id);
        return Tuple.empty();
    }

    private void initTable(List<Schema<?>> schemas) {
        List<String> schemaNames = schemas.stream().map(Schema::getName).collect(Collectors.toList());
        insertRow(schemaNames, Constants.SCHEMA_NAME_CUR);

        List<String> schemaTypes = schemas.stream().map(s -> s.getType().getName()).collect(Collectors.toList());
        insertRow(schemaTypes, Constants.SCHEMA_TYPE_CUR);

//        for(int i=0; i<schemas.size(); i++) {
//            log.info("schemas.size : {}, i : {}", schemas.size(), i);
//            log.info("ann size: {}", schemas.get(i).getAnnotations().size());
//
//            for(int j=0; j<schemas.get(i).getAnnotations().size(); j++) {
//                log.info("i: {}, j: {}", i, j);
//                insertValue(schemas.get(i).getAnnotations().get(j).annotationType().getName(), Constants.SCHEMA_ANN_START_CUR + j, Constants.CUR_CELL_INIT_VAL + i);
//            }
//        }
    }

    private void insertTuple(Tuple tuple, int rowCur) {
        insertRow(tuple.getValue().stream().map(d -> String.valueOf(d.getValue())).collect(Collectors.toList()), rowCur);
    }

    private void insertRow(List<String> values, int rowCur) {
        for(String value : values) {
            insertValue(value, rowCur, getCellCur());
        }
    }

    private void insertValue(String value, int rowCur, int cellCur) {
        Optional.ofNullable(sheet.getRow(rowCur)).orElse(sheet.createRow(rowCur)).createCell(cellCur).setCellValue(value);
    }

    private int getRowCur() {
        return rowCur++;
    }

    private int getCellCur() {
        if(cellCur >= cellSize) {
            int temp = cellCur;
            setCellCur(Constants.CUR_CELL_INIT_VAL);
            return temp;
        }

        return cellCur++;
    }
}
