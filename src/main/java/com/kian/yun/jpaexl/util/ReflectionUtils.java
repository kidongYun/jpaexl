package com.kian.yun.jpaexl.util;

import com.kian.yun.jpaexl.exception.JpaexlException;

import java.lang.reflect.Field;
import java.util.Arrays;

import static com.kian.yun.jpaexl.code.JpaexlCode.FAIL_TO_FIND_FIELD_MATCHED_ANNOTATION_TYPE;

public class ReflectionUtils {
    public static Field getFieldByAnnotation(Class<?> annotationType, Field... fields) {
        Field target = Arrays.stream(fields)
                .filter(field -> Arrays.stream(field.getAnnotations()).anyMatch(a -> a.annotationType().getCanonicalName().equals(annotationType.getCanonicalName())))
                .findAny().orElseThrow(() -> new JpaexlException(FAIL_TO_FIND_FIELD_MATCHED_ANNOTATION_TYPE));

        target.setAccessible(true);

        return target;
    }
}
