package com.kian.yun.jpaexl.domain;

import java.util.List;

public class Tuples<T> {
    private final List<Tuple<T>> value;

    private Tuples(List<Tuple<T>> value) {
        this.value = value;
    }
}
