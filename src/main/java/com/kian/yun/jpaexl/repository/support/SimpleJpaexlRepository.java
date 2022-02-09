package com.kian.yun.jpaexl.repository.support;

import com.kian.yun.jpaexl.domain.*;
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
            SimpleTable.getInstance(ReflectionUtils.className(clazz.getSimpleName()), schemas).insert(tuple);

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
//        Tuple tuple = SimpleTable.getInstance(clazz.getSimpleName()).findById(String.valueOf(id));
        Tuple tuple = Tuple.empty();

        Class<?>[] schemaTypes = tuple.getValue().stream().map(d -> d.getSchema().getType()).collect(Collectors.toList()).toArray(new Class[]{});
        Object[] values = tuple.getValue().stream().map(Data::getValue).collect(Collectors.toList()).toArray(new Object[]{});

        try {
            Constructor<T> constructor = clazz.getConstructor(schemaTypes);
            T instance = constructor.newInstance(values);

            return Optional.of(instance);

        } catch (NoSuchMethodException
                | InvocationTargetException
                | InstantiationException
                | IllegalAccessException e) {

            e.printStackTrace();
            return Optional.empty();
        }
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
