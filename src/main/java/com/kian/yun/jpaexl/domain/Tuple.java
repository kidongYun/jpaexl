package com.kian.yun.jpaexl.domain;

public interface Tuple<T> {
    Iterable<Data<?>> getData();
}
