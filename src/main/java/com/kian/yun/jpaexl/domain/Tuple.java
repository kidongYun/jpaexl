package com.kian.yun.jpaexl.domain;

import java.util.Collection;
import java.util.List;

public interface Tuple<T> {
    Class<T> getClazz();

    Collection<Data<?>> getData();

    Collection<Schema<?>> getSchemas();

    List<String> getValues();

    Data<?> getIdentifier();
}
