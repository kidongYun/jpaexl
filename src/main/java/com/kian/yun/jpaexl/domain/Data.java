package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.List;

@Getter
@Builder
public class Data<T> {
    private final Schema<T> schema;
    private final T value;

    @SuppressWarnings("unchecked")
    public static <T> Data<T> of(String name, List<Annotation> annotations, T value) {
        Schema<T> schema = Schema.<T>builder()
                .name(name)
                .annotations(annotations)
                .type((Class<T>) value.getClass())
                .build();

        return Data.<T>builder()
                .schema(schema)
                .value(value)
                .build();
    }

    public static <T> Data<T> of(Schema<T> schema, T value) {
        return new Data<>(schema, value);
    }
}
