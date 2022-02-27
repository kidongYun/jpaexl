package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;

@Slf4j
@Builder
public class SimpleSchema<T> implements Schema<T> {
    private final Class<T> type;
    private final String name;
    private final Boolean isIdentifier;

    @SuppressWarnings("unchecked")
    public static <T> Schema<T> of(Field field) {
        return SimpleSchema.<T>builder()
                .type((Class<T>) field.getType())
                .name(field.getName())
                .isIdentifier(Arrays.stream(field.getAnnotations()).anyMatch(f -> f.annotationType().getCanonicalName().equals(Id.class.getCanonicalName())))
                .build();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Boolean isIdentifier() {
        return this.isIdentifier;
    }
}
