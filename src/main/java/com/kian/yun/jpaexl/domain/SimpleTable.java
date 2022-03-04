package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;
import com.kian.yun.jpaexl.util.ReflectionUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        Cursor idCursor = idCursor();

        Cursor cursor = persistenceManager.searchValue(getTableName(), id, Cursor.of(Cursor.ROW_INIT_VAL, idCursor.getCell()), Cursor.of(persistenceManager.rowSize(getTableName()), idCursor.getCell()))
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_SEARCH_CURSOR));

        return Optional.of(SimpleTuple.of(this.getClazz(), findData(cursor)));
    }

    @Override
    public Iterable<Tuple<T>> findAll() {
        List<Tuple<T>> tuples = new ArrayList<>();
        for(int i=Cursor.ROW_INIT_VAL; i<persistenceManager.rowSize(getTableName()); i++) {
            tuples.add(SimpleTuple.of(this.getClazz(), findData(Cursor.row(i))));
        }

        return tuples;
    }

    @Override
    public void save(Tuple<T> tuple) {
        if(existsById(tuple.getIdentifier().getValue())) {
            update(tuple);
        } else {
            insert(tuple);
        }

        persistenceManager.flush();
    }

    @Override
    public boolean existsById(String id) {
        return !Objects.isNull(this.findById(id).orElse(null));
    }

    @Override
    public List<Schema<?>> getSchemas() {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(ReflectionUtils::getSchemaByField)
                .collect(Collectors.toList());
    }

    private void initTable(List<Schema<?>> schemas) {
        List<String> schemaNames = schemas.stream().map(Schema::getName).collect(Collectors.toList());
        insertRow(schemaNames, Cursor.of(Cursor.ROW_SCHEMA_NAME, Cursor.CELL_INIT_VAL));

        List<String> schemaTypes = schemas.stream().map(s -> s.getType().getName()).collect(Collectors.toList());
        insertRow(schemaTypes, Cursor.of(Cursor.ROW_SCHEMA_TYPE, Cursor.CELL_INIT_VAL));

        persistenceManager.flush();
    }

    private void insert(Tuple<T> tuple) {
        insertRow(tuple.getValues(), Cursor.of(persistenceManager.rowSize(this.getTableName()), Cursor.CELL_INIT_VAL));
    }

    private void update(Tuple<T> tuple) {
        log.info("DEBUG update()...");
    }

    private Cursor idCursor() {
        Schema<?> idSchema = getIdSchema().orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_ID_SCHEMA));

        return persistenceManager.searchValue(this.getTableName(), idSchema.getName(), Cursor.of(Cursor.ROW_SCHEMA_NAME, Cursor.CELL_INIT_VAL), Cursor.of(Cursor.ROW_SCHEMA_NAME, cellSize()))
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_ID_SCHEMA));
    }

    private Optional<Schema<?>> getIdSchema() {
        return this.getSchemas().stream()
                .filter(Schema::isIdentifier)
                .findFirst();
    }

    private void insertRow(List<String> values, Cursor cursor) {
        for(String value : values) {
            persistenceManager.insert(this.getTableName(), cursor.shift(cellSize()), value);
        }
    }

    private List<String> findRow(Cursor cursor) {
        return IntStream.rangeClosed(Cursor.CELL_INIT_VAL, cellSize())
                .mapToObj(i -> persistenceManager.findValue(this.getTableName(), Cursor.of(cursor.getRow(), i)))
                .map(opt -> opt.orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_VALUE)))
                .collect(Collectors.toList());
    }

    private List<Data<?>> findData(Cursor cursor) {
        List<String> values = findRow(cursor);

        List<Schema<?>> schemas = getSchemas();

        return IntStream.range(0, schemas.size())
                .mapToObj(i -> SimpleData.of(schemas.get(i), values.get(i)))
                .collect(Collectors.toList());
    }

    private String getTableName() {
        return clazz.getSimpleName();
    }

    private Integer cellSize() {
        return clazz.getDeclaredFields().length;
    }
}
