package com.kian.yun.jpaexl.domain;

public interface Data<T> {
    Iterable<SimpleSchema<T>> getSchemas();

    SimpleSchema<T> getSchemaByName(String name);

    Iterable<String> getValues();

    String getValueBySchemaName(String schemaName);
}
