package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SimpleData<T> implements Data<T> {
    private final Schema<T> schema;
    private final String value;

    @SuppressWarnings("unchecked")
    public static <T> Data<T> of(String name, T value) {
        SimpleSchema<T> schema = SimpleSchema.<T>builder()
                .name(name)
                .type((Class<T>) value.getClass())
                .build();

        return SimpleData.<T>builder()
                .schema(schema)
                .value(String.valueOf(value))
                .build();
    }

    public static <T> Data<T> of(Schema<T> schema, String value) {
        return new SimpleData<>(schema, value);
    }
}
