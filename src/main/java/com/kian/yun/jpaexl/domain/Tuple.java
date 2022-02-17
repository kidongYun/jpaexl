package com.kian.yun.jpaexl.domain;

import java.util.Optional;

public interface Tuple<T> {
    Optional<Tuple<T>> findById(Table<T> table, String id);

    Iterable<Tuple<T>> findAll(Table<T> table);

    void insert(Table<T> table);

    void add(Data<?> data);
}
