package com.kian.yun.jpaexl.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JpaexlCode {
    /* REFLECTION */
    FAIL_TO_FIND_FIELD_MATCHED_ANNOTATION_TYPE("RF0000", "Fields don't have matched annotation type.")
    ;

    private final String name;
    private final String desc;
}
