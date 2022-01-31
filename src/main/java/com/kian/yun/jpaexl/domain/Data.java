package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Data<T> {
    private final Schema<T> schema;
    private final T value;

    @SuppressWarnings("unchecked")
    public static <T> Data<T> of(String name, T value) {
        Schema<T> schema = Schema.<T>builder()
                .name(name)
                .type((Class<T>) value.getClass())
                .build();

        return Data.<T>builder()
                .schema(schema)
                .value(value)
                .build();
    }
}
