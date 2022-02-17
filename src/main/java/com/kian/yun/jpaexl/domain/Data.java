package com.kian.yun.jpaexl.domain;

import java.util.Optional;

public interface Data<D> {
    <T> Optional<Data<D>> findByName(Table<T> table, String schemaName, String id);

    <T> Schema<D> findSchema(Table<T> table, String schemaName);

    <T> D findValue(Table<T> table, String schemaName, String id);
}
