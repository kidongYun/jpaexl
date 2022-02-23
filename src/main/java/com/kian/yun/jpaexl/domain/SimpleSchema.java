package com.kian.yun.jpaexl.domain;

import lombok.Builder;

import java.lang.reflect.Field;

@Builder
public class SimpleSchema<T> implements Schema<T> {
    private final Class<T> type;
    private final String name;

    public static <T> Schema<T> of(Class<T> type, String name) {
        return SimpleSchema.<T>builder().type(type).name(name).build();
    }

    @SuppressWarnings("unchecked")
    public static <T> Schema<T> of(Field field) {
        return SimpleSchema.<T>builder().type((Class<T>) field.getType()).name(field.getName()).build();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
