package com.kian.yun.jpaexl.domain;

public interface Schema<T> {
    Class<T> getType();

    String getName();

    Boolean isIdentifier();
}
