package com.kian.yun.jpaexl.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JpaexlCode {
    /* REFLECTION */
    FAIL_TO_FIND_FIELD_MATCHED_ANNOTATION_TYPE("RF0000", "Fields don't have matched annotation type."),
    FAIL_TO_CREATE_INSTANCE_BY_TUPLE("RF0001", "Tuple can't be converted as the new instance."),

    /* EXCEL */
    FAIL_TO_FIND_ID_CELL_IN_SCHEMA("XL0000", "Id cell can't be found at Schema row."),
    FAIL_TO_FIND_ROW_BY_ID("XL0001", "Row is not found matched with id"),
    FAIL_TO_FIND_DATA("XL0002", "Data is not found in Excel"),
    FAIL_TO_FIND_SCHEMA_TYPE("XL0003", "Schema type is not found in Excel"),
    FAIL_TO_FIND_SCHEMA_NAME("XL0004", "Schema name is not found in Excel"),
    SCHEMA_IS_NULL("XL0005", "Schema is null"),
    TUPLE_IS_NULL("XL0006", "Tuple is not found from Excel"),
    ;

    private final String name;
    private final String desc;
}
