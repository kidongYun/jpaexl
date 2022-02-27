package com.kian.yun.jpaexl.util;


import com.kian.yun.jpaexl.domain.Schema;
import com.kian.yun.jpaexl.domain.SimpleSchema;
import com.kian.yun.jpaexl.exception.JpaexlException;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;

import static com.kian.yun.jpaexl.code.JpaexlCode.FAIL_TO_FIND_FIELD_MATCHED_ANNOTATION_TYPE;

@Slf4j
public class ReflectionUtils {
    public static Field getFieldByAnnotation(Class<?> annotationType, Field... fields) {
        Field target = Arrays.stream(fields)
                .filter(field -> Arrays.stream(field.getAnnotations()).anyMatch(a -> a.annotationType().getCanonicalName().equals(annotationType.getCanonicalName())))
                .findAny().orElseThrow(() -> new JpaexlException(FAIL_TO_FIND_FIELD_MATCHED_ANNOTATION_TYPE));

        target.setAccessible(true);

        return target;
    }

    public static Schema<?> getSchemaByField(Field field) {
        return SimpleSchema.of(field);
    }
}
