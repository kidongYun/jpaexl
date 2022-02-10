package com.kian.yun.jpaexl.domain;

import java.util.Collection;

public interface Table {
    void insert(Tuple tuple);
    Tuple findById(String id);
    Iterable<Tuple> findAll();
}
