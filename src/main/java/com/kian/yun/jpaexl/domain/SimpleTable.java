package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.Constants;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class SimpleTable<T> implements Table<T> {
    private final PersistenceManager persistenceManager;
    private final Class<T> clazz;

    private Integer cellSize;
    private Integer rowSize;
    private Cursor cursor;

    private SimpleTable(Class<T> clazz) {
        this.persistenceManager = SimplePersistenceManager.getInstance();
        this.clazz = clazz;

        boolean isExist = persistenceManager.isExist(clazz.getName());
        log.info("'{}' table is exist ?... : {}", clazz.getName(), isExist);

        setRowSize(isExist ? findRowSize() : 0);
        setCellSize(isExist ? findCellSize() : clazz.getFields().length);
        table.setCursor(Cursor.of(getRowSize(), Constants.CUR_CELL_INIT_VAL));ss
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

    }

    private int findCellSize() {
        int size = 0;
        for(int i=Constants.CUR_CELL_INIT_VAL; i<Constants.CUR_CELL_MAX_VAL; i++) {
            Optional<String> valueOpt = getPersistenceManager().findValue(clazz.getName(), Cursor.of(Constants.CUR_ROW_SCHEMA_NAME, i));

            if(valueOpt.isEmpty()) {
                break;
            }

            size++;
        }

        return size;
    }

    private int findRowSize() {
        for(int i=Constants.CUR_ROW_INIT_VAL; i<Constants.CUR_ROW_MAX_VAL; i++) {
            Optional<String> valueOpt = getPersistenceManager().findValue(clazz.getName(), Cursor.of(i, Constants.CUR_CELL_INIT_VAL));

            if(valueOpt.isPresent()) {
                continue;
            }

            return i;
        }

        return 0;
    }
}
