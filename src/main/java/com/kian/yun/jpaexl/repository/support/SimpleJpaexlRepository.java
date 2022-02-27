package com.kian.yun.jpaexl.repository.support;

import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.domain.*;
import com.kian.yun.jpaexl.exception.JpaexlException;
import com.kian.yun.jpaexl.repository.JpaexlRepository;
import com.kian.yun.jpaexl.util.ExceptionUtils;
import com.kian.yun.jpaexl.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class SimpleJpaexlRepository<T, ID> implements JpaexlRepository<T, ID> {
    private final Class<T> clazz;
    private final Table<T> table;

    public SimpleJpaexlRepository(Class<T> clazz) {
        this.clazz = clazz;
        this.table = SimpleTable.getInstance(clazz);
    }

    @Override
    public <S extends T> S save(S entity) {
        List<Data<?>> data = Arrays.stream(entity.getClass().getDeclaredFields())
                .peek(f -> f.setAccessible(true))
                .map(f -> ExceptionUtils.wrap(() -> SimpleData.of(f.getName(), f.get(entity))))
                .collect(Collectors.toList());

        Tuple<T> tuple = SimpleTuple.of(clazz, data);

        table.save(tuple);

        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<T> findById(ID id) {
        Tuple<T> tuple = table.findById(String.valueOf(id))
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_TUPLE_BY_ID));

        return ReflectionUtils.createInstanceByTuple(tuple);
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
