package com.kian.yun.jpaexl.domain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PRIVATE)
@Builder
@ToString
public class Cursor {
    public static final int ROW_SCHEMA_NAME = 1;
    public static final int ROW_SCHEMA_TYPE = 2;
    public static final int ROW_INIT_VAL = 3;
    public static final int CELL_INIT_VAL = 0;

    public static final int ROW_MAX_VAL = 9999;
    public static final int CELL_MAX_VAL = 9999;

    private final int row;
    private int cell;

    public static Cursor of(int row, int cell) {
        return Cursor.builder().row(row).cell(cell).build();
    }

    public static Cursor of(int row) {
        return Cursor.builder().row(row).cell(CELL_INIT_VAL).build();
    }

    public Cursor shift(int cellSize) {
        log.info("cell : {}", cell);
        log.info("cellSize : {}", cellSize);

        if(cell >= cellSize) {
            setCell(Cursor.CELL_INIT_VAL);
        } else {
            cell++;
        }

        return this;
    }
}
