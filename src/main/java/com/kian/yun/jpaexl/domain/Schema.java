package com.kian.yun.jpaexl.domain;

import java.util.Collection;
import java.util.Optional;

public interface Schema<S> {
    <T> Collection<Schema<S>> find(Table<T> table);

    <T> Optional<Schema<S>> findByName(Table<T> table, String name);

    Class<S> getType();

    String getName();
}
