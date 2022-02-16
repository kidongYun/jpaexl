package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

@Getter
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
                .persistenceManager(PersistenceManager.getInstance())
                .schema(schema)
                .value(value)
                .build();
    }

    public static <D> Data<D> of(SimpleSchema<D> simpleSchema, D value) {
        return new SimpleData<>(PersistenceManager.getInstance(), simpleSchema, value);
    }

    @Override
    public <T> Optional<Data<D>> find(Table<T> table, Cursor cursor) {
        Schema<D> simpleSchema = schema.find(table, cursor)
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA));

        String value = persistenceManager.findValue(table.getName(), Cursor.of(cursor.getRow(), cursor.getCell()))
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_VALUE));

        try {
            Constructor<T> constructor = simpleSchema.getType().getConstructor(String.class);
            T instance = constructor.newInstance(value);
            return Optional.of(SimpleData.of(simpleSchema, instance));

        } catch (NoSuchMethodException
                | InvocationTargetException
                | InstantiationException
                | IllegalAccessException e) {
            e.printStackTrace();
            return Optional.empty();

        } catch (JpaexlException e) {
            return Optional.empty();
        }
    }
}
