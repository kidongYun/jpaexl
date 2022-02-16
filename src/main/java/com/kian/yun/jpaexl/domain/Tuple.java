package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class Tuple<T> {
    private final Class<T> clazz;
    private final List<SimpleData<?>> value;

    private Tuple(Class<T> clazz, List<SimpleData<?>> tuple) {
        this.clazz = clazz;
        this.value = tuple;
    }

    public static <T> Tuple<T> empty() {
        return new Tuple<>(null, new ArrayList<>());
    }

    public static <T> Tuple<T> of(Class<T> clazz, List<SimpleData<?>> tuple) {
        return new Tuple<>(clazz, tuple);
    }

    public void add(SimpleData<?> simpleData) {
        value.add(simpleData);
    }
}
