package com.kian.yun.jpaexl.util;

import com.kian.yun.jpaexl.domain.SimpleData;
import com.kian.yun.jpaexl.domain.SimpleTuple;
import com.kian.yun.jpaexl.exception.JpaexlException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public static int countFields(Field... fields) {
        return fields.length;
    }

    public static String className(Object entity) {
        return entity.getClass().getSimpleName();
    }

    public static <T> Optional<T> createInstanceByTuple(SimpleTuple<T> simpleTuple) {
        Class<?>[] schemaTypes = simpleTuple.getValue().stream().map(d -> d.getSchema().getType()).collect(Collectors.toList()).toArray(new Class[]{});
        Object[] values = simpleTuple.getValue().stream().map(SimpleData::getValue).collect(Collectors.toList()).toArray(new Object[]{});

        try {
            Constructor<T> constructor = simpleTuple.getClazz().getConstructor(schemaTypes);
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
