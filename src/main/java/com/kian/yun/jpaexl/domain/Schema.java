package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.List;

@Getter
@Builder
public class Schema<T> {
    private final Class<T> type;
    private final String name;

    public static <T> Schema<T> of(Class<T> type, String name) {
        return Schema.<T>builder().type(type).name(name).build();
    }
}
