package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Getter
@Builder
public class SimpleSchema<S> implements Schema<S> {
    public static final int CUR_ROW_SCHEMA_NAME = 1;
    public static final int CUR_ROW_SCHEMA_TYPE = 2;

    private final SimplePersistenceManager simplePersistenceManager;
    private final Class<S> type;
    private final String name;

    public static <S> SimpleSchema<S> of(Class<S> type, String name) {
        return SimpleSchema.<S>builder()
                .simplePersistenceManager(SimplePersistenceManager.getInstance())
                .type(type)
                .name(name)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Collection<Schema<S>> find(Table<T> table) {
        return new ArrayList<>();
//        String schemaType = persistenceManager.findValue(table.getName(), Cursor.of(CUR_ROW_SCHEMA_TYPE, cursor.getCell()))
//                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_TYPE));
//
//        String schemaName = persistenceManager.findValue(table.getName(), Cursor.of(CUR_ROW_SCHEMA_NAME, cursor.getCell()))
//                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_NAME));
//
//        try {
//            Schema<S> schema = SimpleSchema.of((Class<S>) Class.forName(schemaType), schemaName);
//            return Optional.of(schema);
//
//        } catch (ClassNotFoundException | JpaexlException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
    }

    @Override
    public <T> Optional<Schema<S>> findByName(Table<T> table, String name) {
        return Optional.empty();
    }
}
