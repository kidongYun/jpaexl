package com.kian.yun.jpaexl.domain;

import java.util.List;
import java.util.Optional;

public interface Table<T> {
    Optional<Tuple<T>> findById(String id);

    Iterable<Tuple<T>> findAll();

    void save(Tuple<T> tuple);

    boolean existsById(String id);

    List<Schema<?>> getSchemas();
}
