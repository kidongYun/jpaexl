package com.kian.yun.jpaexl.repository.support;

import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.domain.*;
import com.kian.yun.jpaexl.exception.JpaexlException;
import com.kian.yun.jpaexl.repository.JpaexlRepository;
import com.kian.yun.jpaexl.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Repository
public class SimpleJpaexlRepository<T, ID> implements JpaexlRepository<T, ID> {
    private final Class<T> clazz;

    public SimpleJpaexlRepository(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends T> S save(S entity) {
        try {
            Tuple tuple = Tuple.empty();

            for(Field field : entity.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                tuple.add(Data.of(field.getName(), Arrays.stream(field.getAnnotations()).collect(Collectors.toList()), field.get(entity)));
            }

            List<Schema<?>> schemas = tuple.getValue().stream().map(Data::getSchema).collect(Collectors.toList());
            SimpleTable.createInstance(ReflectionUtils.className(clazz.getSimpleName()), schemas).insert(tuple);

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
        Tuple tuple = SimpleTable.getInstance(clazz.getSimpleName()).findById(String.valueOf(id));
        return ReflectionUtils.createInstanceByTuple(clazz, tuple);
    }

    @Override
    public boolean existsById(ID id) {
        return false;
    }

    @Override
    public Iterable<T> findAll() {
        Iterable<Tuple> tuples = SimpleTable.getInstance(ReflectionUtils.className(clazz.getSimpleName())).findAll();

        return StreamSupport.stream(tuples.spliterator(), false)
                .map(t -> ReflectionUtils.createInstanceByTuple(clazz, t)
                        .orElseThrow(() -> new JpaexlException(JpaexlCode.FAIL_TO_CREATE_INSTANCE_BY_TUPLE)))
                .collect(Collectors.toList());
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
}
