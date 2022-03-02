package com.kian.yun.jpaexl.util;


import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.domain.*;
import com.kian.yun.jpaexl.exception.JpaexlException;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public static Optional<?> createInstanceBySchema(Schema<?> schema, String value) {
        String schemaName = schema.getType().getCanonicalName();

        if(schemaName.equals(String.class.getCanonicalName())) {
            return schema.getType().getConstructor(String.class).newInstance(value);
        } else if(schemaName.equals(Long.class.getCanonicalName())) {

        } else if(schemaName.equals(LocalDate.class.getCanonicalName())) {

        }
    }

    public static <T> Optional<T> createInstanceByTuple(Tuple<T> tuple) {
        Class<?>[] schemaTypes = tuple.getSchemas().stream()
                .map(Schema::getType)
                .collect(Collectors.toList())
                .toArray(new Class[]{});

        tuple.getData().stream().forEach(d -> {
            log.info("DEBUG d.getSchema().getType().getName() : {}", d.getSchema().getType().getName());
            log.info("DEBUG d.getValue() : {}", d.getValue());
        });

        Object[] values = tuple.getData().stream()
                .map(d -> ExceptionUtils.wrap(() -> d.getSchema().getType().getConstructor(String.class).newInstance(d.getValue())))
                .collect(Collectors.toList())
                .toArray(new Object[]{});

        try {
            Constructor<T> constructor = tuple.getClazz().getConstructor(schemaTypes);
            T instance = constructor.newInstance(values);

            return Optional.of(instance);

        } catch (NoSuchMethodException
                | InvocationTargetException
                | InstantiationException
                | IllegalAccessException e) {

            e.printStackTrace();
            return Optional.empty();
        }
    }
}
