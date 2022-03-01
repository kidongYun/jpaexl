package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SimpleData<T> implements Data<T> {
    private final Schema<T> schema;
    private final String value;

    public static <T> Data<T> of(Schema<T> schema, String value) {
        return new SimpleData<>(schema, value);
    }
}
