package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SimpleData<T> implements Data<T> {
    private final SimpleSchema<T> schema;
    private final T value;

    @SuppressWarnings("unchecked")
    public static <T> Data<T> of(String name, T value) {
        SimpleSchema<T> schema = SimpleSchema.<T>builder()
                .name(name)
                .type((Class<T>) value.getClass())
                .build();

        return SimpleData.<T>builder()
                .schema(schema)
                .value(value)
                .build();
    }

    public static <T> Data<T> of(SimpleSchema<T> schema, T value) {
        return new SimpleData<>(schema, value);
    }

    @Override
    public Iterable<SimpleSchema<T>> getSchemas() {
        return null;
    }

    @Override
    public SimpleSchema<T> getSchemaByName(String name) {
        return null;
    }

    @Override
    public Iterable<String> getValues() {
        return null;
    }

    @Override
    public String getValueBySchemaName(String schemaName) {
        return null;
    }
}
