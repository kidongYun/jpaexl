package com.kian.yun.jpaexl.domain;

public interface Data<T> {
    Schema<T> getSchema();

    String getValue();

    Boolean isIdentifier();
}
