package com.kian.yun.jpaexl.domain;

import lombok.Builder;

@Builder
public class SimpleSchema<T> implements Schema<T> {
    private final Class<T> type;
    private final String name;

    public static <T> Schema<T> of(Class<T> type, String name) {
        return SimpleSchema.<T>builder().type(type).name(name).build();
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
