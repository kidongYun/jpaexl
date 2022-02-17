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
    private final D value;

    @SuppressWarnings("unchecked")
    public static <D> Data<D> of(String name, D value) {
        Schema<D> schema = SimpleSchema.<D>builder()
                .name(name)
                .type((Class<D>) value.getClass())
                .build();

        return SimpleData.<D>builder()
                .persistenceManager(SimplePersistenceManager.getInstance())
                .schema(schema)
                .value(value)
                .build();
    }

    public static <D> Data<D> of(Schema<D> simpleSchema, D value) {
        return new SimpleData<>(SimplePersistenceManager.getInstance(), simpleSchema, value);
    }

    @Override
    public <T> Optional<Data<D>> findByName(Table<T> table, String schemaName, String id) {
        return Optional.of(SimpleData.of(
                schema.findByName(table, schemaName).orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA)),
                findValue(table, schemaName, id)));
    }

    @Override
    public <T> Schema<D> findSchema(Table<T> table, String schemaName) {
        return schema.findByName(table, schemaName)
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA));
    }

    @Override
    public <T> D findValue(Table<T> table, String schemaName, String id) {
        String value = persistenceManager.findValue(table.getName(), findCursor())
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_VALUE));

        try {
            Constructor<D> constructor = schema1.getType().getConstructor(String.class);
            D instance = constructor.newInstance(value);
            return Optional.of(SimpleData.of(schema1, instance));

        } catch (NoSuchMethodException
                | InvocationTargetException
                | InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
            return Optional.empty();

        } catch (JpaexlException e) {
            return Optional.empty();
        }

        return null;
    }

    private Cursor findCursor() {
        return Cursor.base();
    }
}
