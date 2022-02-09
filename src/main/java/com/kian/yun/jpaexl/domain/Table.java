package com.kian.yun.jpaexl.domain;

public interface Table {
    void insert(Tuple tuple);
    Tuple findById(String id);
}
