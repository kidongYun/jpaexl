package com.kian.yun.jpaexl.domain;

import java.util.Optional;

public interface Table<T> {
    Optional<Tuple<T>> findById(String id);

    Iterable<Tuple<T>> findAll();

    void save(Tuple<T> tuple);
}
