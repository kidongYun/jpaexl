package com.kian.yun.jpaexl.domain;

public interface Table<T> {
    void insert(Tuple<T> tuple);

    Tuple<T> findById(String id);

    Iterable<Tuple<T>> findAll();

    String getName();
}
