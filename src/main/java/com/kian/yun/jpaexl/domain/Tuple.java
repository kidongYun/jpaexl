package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class Tuple {
    private final List<Data<?>> tuple;

    private Tuple(List<Data<?>> tuple) {
        this.tuple = tuple;
    }

    public static Tuple empty() {
        return new Tuple(new ArrayList<>());
    }

    public static Tuple of(List<Data<?>> tuple) {
        return new Tuple(tuple);
    }

    public void add(Data<?> data) {
        tuple.add(data);
    }
}
