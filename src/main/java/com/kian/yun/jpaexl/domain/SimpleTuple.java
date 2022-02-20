package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class SimpleTuple<T> implements Tuple<T> {
    private final Class<T> clazz;
    private final List<Data<?>> data;

    private SimpleTuple(Class<T> clazz, List<Data<?>> data) {
        this.clazz = clazz;
        this.data = data;
    }

    public static <T> Tuple<T> empty() {
        return new SimpleTuple<>(null, new ArrayList<>());
    }

    public static <T> Tuple<T> of(Class<T> clazz, List<Data<?>> tuple) {
        return new SimpleTuple<>(clazz, tuple);
    }

    @Override
    public Iterable<Data<?>> getData() {
        return null;
    }
}
