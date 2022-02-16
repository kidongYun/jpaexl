package com.kian.yun.jpaexl.domain;

import java.util.Optional;

public interface Data<D> {
    <T> Optional<Data<D>> find(Table<T> table, Cursor cursor)
}
