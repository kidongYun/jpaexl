package com.kian.yun.jpaexl.domain;

import java.util.Collection;

public interface Tuple<T> {
    Collection<Data<?>> getData();

    Collection<Schema<?>> getSchemas();
}
