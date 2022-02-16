package com.kian.yun.jpaexl.domain;

import java.util.Optional;

public interface Schema<S> {
    <T> Optional<Schema<S>> find(Table<T> table, Cursor cursor);
}
