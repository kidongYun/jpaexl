package com.kian.yun.jpaexl.domain;

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

    private Integer cellSize;

    private SimpleTable(Class<T> clazz) {
        this.persistenceManager = SimplePersistenceManager.getInstance();
        this.clazz = clazz;

        boolean isExist = persistenceManager.isExist(clazz.getSimpleName());

        setCellSize(isExist ? findCellSize() : clazz.getDeclaredFields().length);

        if(!isExist) {
            List<Schema<?>> schemas = Arrays.stream(clazz.getDeclaredFields())
                    .map(ReflectionUtils::getSchemaByField)
                    .collect(Collectors.toList());

            initTable(schemas);
        }
    }

    public static <T> Table<T> getInstance(Class<T> clazz) {
        return new SimpleTable<>(clazz);
    }

    @Override
    public Optional<Tuple<T>> findById(String id) {
        return Optional.empty();
    }

    @Override
    public Iterable<Tuple<T>> findAll() {
        return null;
    }

    @Override
    public void save(Tuple<T> tuple) {
        List<String> values = tuple.getData().stream().map(Data::getValue).collect(Collectors.toList());
        insertRow(values, Cursor.of(findRowSize(), Cursor.CELL_INIT_VAL));

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
            persistenceManager.insert(clazz.getSimpleName(), cursor.shift(cellSize), value);
        }
    }

    private Integer findCellSize() {
        int size = 0;

        for(int i=Cursor.of(Cursor.ROW_SCHEMA_NAME).shift(cellSize).getCell(); i<Cursor.CELL_MAX_VAL; i++) {
            Optional<String> valueOpt = getPersistenceManager().find(clazz.getSimpleName(), Cursor.of(Cursor.ROW_SCHEMA_NAME, i));

            if(valueOpt.isEmpty()) {
                break;
            }

            size++;
        }

        return size;
    }

    private Integer findRowSize() {
        for(int i=Cursor.ROW_INIT_VAL; i<Cursor.ROW_MAX_VAL; i++) {
            Optional<String> valueOpt = getPersistenceManager().find(clazz.getSimpleName(), Cursor.of(i).shift(cellSize));

            if(valueOpt.isPresent()) {
                continue;
            }

            return i;
        }

        return 0;
    }
}
