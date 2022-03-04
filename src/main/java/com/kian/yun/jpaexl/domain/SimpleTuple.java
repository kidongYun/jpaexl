package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleTuple<T> implements Tuple<T> {
    private final Class<T> clazz;
    private final List<Data<?>> data;

    private SimpleTuple(Class<T> clazz, List<Data<?>> data) {
        this.clazz = clazz;
        this.data = data;
    }

    public static <T> Tuple<T> of(Class<T> clazz, List<Data<?>> data) {
        return new SimpleTuple<>(clazz, data);
    }

    @Override
    public Class<T> getClazz() {
        return this.clazz;
    }

    @Override
    public Collection<Data<?>> getData() {
        return this.data;
    }

    @Override
    public Collection<Schema<?>> getSchemas() {
        return Arrays.stream(clazz.getDeclaredFields()).map(SimpleSchema::of).collect(Collectors.toList());
    }

    @Override
    public List<String> getValues() {
        return this.getData().stream().map(Data::getValue).collect(Collectors.toList());
    }

    @Override
    public Data<?> getIdentifier() {
        return getData().stream().filter(Data::isIdentifier).findFirst()
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_ID_SCHEMA));
    }
}
