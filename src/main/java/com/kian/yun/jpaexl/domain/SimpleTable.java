package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;
import com.kian.yun.jpaexl.util.ReflectionUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class SimpleTable<T> implements Table<T> {
    private final PersistenceManager persistenceManager;
    private final Class<T> clazz;

    private SimpleTable(Class<T> clazz) {
        this.persistenceManager = SimplePersistenceManager.getInstance();
        this.clazz = clazz;

        boolean isExist = persistenceManager.isExist(this.getTableName());

        if(!isExist) {
            initTable(this.getSchemas());
        }
    }

    public static <T> Table<T> getInstance(Class<T> clazz) {
        return new SimpleTable<>(clazz);
    }

    @Override
    public Optional<Tuple<T>> findById(String id) {
        log.info("DEBUG findById : {}", id);

        String idSchemaName = getSchemas().stream().filter(Schema::isIdentifier).findFirst()
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_ID_SCHEMA)).getName();

        log.info("DEBUG idSchemaName : {}", idSchemaName);

        Cursor idCursor = persistenceManager.searchValue(this.getTableName(), idSchemaName, Cursor.of(Cursor.ROW_SCHEMA_NAME, Cursor.CELL_INIT_VAL), Cursor.of(Cursor.ROW_SCHEMA_NAME, cellSize()))
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_SCHEMA_NAME));

        log.info("DEBUG idCursor : {}", idCursor.toString());

        Cursor cursor = persistenceManager.searchValue(this.getTableName(), id, Cursor.of(Cursor.ROW_INIT_VAL, idCursor.getCell()), Cursor.of(Cursor.ROW_MAX_VAL, idCursor.getCell()))
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_SEARCH_CURSOR));

        log.info("DEBUG cursor : {}", cursor.toString());

        return Optional.empty();
    }

    @Override
    public Iterable<Tuple<T>> findAll() {
        return null;
    }

    @Override
    public void save(Tuple<T> tuple) {
        List<String> values = tuple.getValues();
        insertRow(values, Cursor.of(persistenceManager.rowSize(this.getTableName()), Cursor.CELL_INIT_VAL));

        persistenceManager.flush();
    }

    private void initTable(List<Schema<?>> schemas) {
        List<String> schemaNames = schemas.stream().map(Schema::getName).collect(Collectors.toList());
        insertRow(schemaNames, Cursor.of(Cursor.ROW_SCHEMA_NAME, Cursor.CELL_INIT_VAL));

        List<String> schemaTypes = schemas.stream().map(s -> s.getType().getName()).collect(Collectors.toList());
        insertRow(schemaTypes, Cursor.of(Cursor.ROW_SCHEMA_TYPE, Cursor.CELL_INIT_VAL));
    }

    private void insertRow(List<String> values, Cursor cursor) {
        for(String value : values) {
            persistenceManager.insert(this.getTableName(), cursor.shift(cellSize()), value);
        }
    }

    private List<String> findRow(Cursor cursor) {
        persistenceManager.findValue(this.getTableName(), cursor.shift(cellSize()));
    }

    private String getTableName() {
        return clazz.getSimpleName();
    }

    private List<Schema<?>> getSchemas() {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(ReflectionUtils::getSchemaByField)
                .collect(Collectors.toList());
    }

    private Integer cellSize() {
        return clazz.getDeclaredFields().length;
    }
}
