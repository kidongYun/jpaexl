package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.Constants;
import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
@Builder
public class SimpleSchema<S> implements Schema<S> {
    public static final int CUR_ROW_SCHEMA_NAME = 1;
    public static final int CUR_ROW_SCHEMA_TYPE = 2;

    private final PersistenceManager persistenceManager;
    private final Class<S> type;
    private final String name;

    public static <S> SimpleSchema<S> of(Class<S> type, String name) {
        return SimpleSchema.<S>builder()
                .persistenceManager(PersistenceManager.getInstance())
                .type(type)
                .name(name)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Schema<S>> find(Table<T> table, Cursor cursor) {
        String schemaType = persistenceManager.findValue(table.getName(), Cursor.of(CUR_ROW_SCHEMA_TYPE, cursor.getCell()))
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_TYPE));

        String schemaName = persistenceManager.findValue(table.getName(), Cursor.of(CUR_ROW_SCHEMA_NAME, cursor.getCell()))
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_NAME));

        try {
            Schema<S> schema = SimpleSchema.of((Class<S>) Class.forName(schemaType), schemaName);
            return Optional.of(schema);

        } catch (ClassNotFoundException | JpaexlException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
