package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.Constants;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Cursor {
    private Integer row;
    private Integer cell;

    public static Cursor base() {
        return Cursor.builder().row(0).cell(0).build();
    }

    public static Cursor row(int row) {
        return Cursor.builder().row(row).build();
    }

    public static Cursor of(int row, int cell) {
        return Cursor.builder().row(row).cell(cell).build();
    }

    public int shiftRow() {
        return row++;
    }

    public int shiftCell(int cellSize) {
        if(cell >= cellSize) {
            int temp = cell;
            setCell(Constants.CUR_CELL_INIT_VAL);
            return temp;
        }

        return cell++;
    }
}
