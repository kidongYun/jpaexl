package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class Tuple<T> {
    private final Class<T> clazz;
    private final List<Data<?>> value;

    private Tuple(Class<T> clazz, List<Data<?>> tuple) {
        this.clazz = clazz;
        this.value = tuple;
    }

    public static <T> Tuple<T> empty() {
        return new Tuple<>(null, new ArrayList<>());
    }

    public static <T> Tuple<T> of(Class<T> clazz, List<Data<?>> tuple) {
        return new Tuple<>(clazz, tuple);
    }

    public void add(Data<?> data) {
        value.add(data);
    }
}
