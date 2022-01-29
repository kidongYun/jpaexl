package com.kian.yun.jpaexl.repository.support;

import com.kian.yun.jpaexl.domain.Data;
import com.kian.yun.jpaexl.domain.PersistenceManager;
import com.kian.yun.jpaexl.domain.Tuple;
import com.kian.yun.jpaexl.repository.JpaexlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.Optional;

@Slf4j
@Repository
public class SimpleJpaexlRepository<T, ID> implements JpaexlRepository<T, ID> {
    private final PersistenceManager persistenceManager;

    public SimpleJpaexlRepository(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
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
                tuple.add(Data.of(field.getName(), field.get(entity)));
            }

            persistenceManager.insert(entity.getClass().getSimpleName(), tuple);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        persistenceManager.flush();

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
}
