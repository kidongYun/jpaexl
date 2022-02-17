package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;
import lombok.Builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Builder
public class SimpleData<D> implements Data<D> {
    private final PersistenceManager persistenceManager;
    private final Schema<D> schema;
    private final String value;

    @SuppressWarnings("unchecked")
    public static <D> Data<D> of(String name, D value) {
        Schema<D> schema = SimpleSchema.<D>builder()
                .name(name)
                .type((Class<D>) value.getClass())
                .build();

        return SimpleData.<D>builder()
                .persistenceManager(SimplePersistenceManager.getInstance())
                .schema(schema)
                .value(String.valueOf(value))
                .build();
    }

    public static <D> Data<D> of(Schema<D> simpleSchema, String value) {
        return new SimpleData<>(SimplePersistenceManager.getInstance(), simpleSchema, value);
    }

    @Override
    public <T> Optional<Data<D>> findByNameAndId(Table<T> table, String schemaName, String id) {
        Schema<D> foundScheme = schema.findByName(table, schemaName)
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA));

        String foundValue = findValue(table, schemaName, id);

        return Optional.of(SimpleData.of(foundScheme, foundValue));
    }

    @Override
    public <T> Schema<D> findSchema(Table<T> table, String schemaName) {
        return schema.findByName(table, schemaName)
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA));
    }

    @Override
    public <T> String findValue(Table<T> table, String schemaName, String id) {
        return persistenceManager.findValue(table.getName(), findCursor(table, schemaName, id))
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_VALUE));
    }

    @Override
    public <T> void insert(Table<T> table) {

    }

    private <T> Cursor findCursor(Table<T> table, String schemaName, String id) {
        return Cursor.base();
    }
}
