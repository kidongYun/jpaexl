package com.kian.yun.jpaexl.repository.support;

import com.kian.yun.jpaexl.domain.*;
import com.kian.yun.jpaexl.repository.JpaexlRepository;
import com.kian.yun.jpaexl.util.ExceptionUtils;
import com.kian.yun.jpaexl.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class SimpleJpaexlRepository<T, ID> implements JpaexlRepository<T, ID> {
    private final Class<T> clazz;

    public SimpleJpaexlRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public <S extends T> S save(S entity) {

        Arrays.stream(entity.getClass().getDeclaredFields()).forEach(f -> f.setAccessible(true));

        List<Data<?>> data = Arrays.stream(entity.getClass().getDeclaredFields())
                .map(f -> ExceptionUtils.wrap(() -> SimpleData.of(f.getName(), f.get(entity))))
                .collect(Collectors.toList());

        Tuple<T> tuple = SimpleTuple.of(clazz, data);

        try {

            List<Schema<?>> schemas = tuple.getValue().stream().map(Data::getSchema).collect(Collectors.toList());
            SimpleTable.createOrGetInstance(ReflectionUtils.className(clazz.getSimpleName()), schemas).insert(tuple);

            SimpleTable.getInstance(clazz)

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(ID id) {
        return false;
    }

    @Override
    public Iterable<T> findAll() {
        return null;
    }

    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(ID id) {

    }

    @Override
    public void delete(T entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {

    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return null;
    }
}
