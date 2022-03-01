package com.kian.yun.jpaexl.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JpaexlCode {
    /* COMMON */
    FAIL_UNKNOWN("CM0000", "Failure is occured by unknown issue."),
    DATA_IS_NULL("CM0001", "Data is null."),

    /* REFLECTION */
    FAIL_TO_FIND_FIELD_MATCHED_ANNOTATION_TYPE("RF0000", "Fields don't have matched annotation type."),
    FAIL_TO_CREATE_INSTANCE_BY_TUPLE("RF0001", "Tuple can't be converted as the new instance."),
    FAIL_TO_FIND_ID_SCHEMA("RF0002", "Id Schema is not found"),

    /* EXCEL */
    FAIL_TO_FIND_ID_CELL_IN_SCHEMA("XL0000", "Id cell can't be found at Schema row."),
    FAIL_TO_FIND_ROW_BY_ID("XL0001", "Row is not found matched with id"),
    FAIL_TO_FIND_DATA("XL0002", "Data is not found in Excel"),
    FAIL_TO_FIND_SCHEMA_TYPE("XL0003", "Schema type is not found in Excel"),
    FAIL_TO_FIND_SCHEMA_NAME("XL0004", "Schema name is not found in Excel"),
    FAIL_TO_FIND_SCHEMA("XL0005", "Schema is not found in Excel"),
    FAIL_TO_FIND_TUPLE("XL0006", "Tuple is not found in Excel"),
    FAIL_TO_FIND_VALUE("XL0007", "String value is not found in Excel"),
    FAIL_TO_FIND_SCHEMA_ANNOTATION("XL0008", "Schema annotations are not found in Excel"),
    FAIL_TO_SEARCH_CURSOR("XL0009", "Cursor is not searched in Excel"),

    /* TABLE */
    FAIL_TO_FIND_TUPLE_BY_ID("TB0001", "Tuple is not found from Table"),
    ;

    private final String name;
    private final String desc;
}
