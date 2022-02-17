package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.Constants;
import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Builder
public class SimpleTuple<T> implements Tuple<T> {
    private final PersistenceManager persistenceManager;
    private final Class<T> clazz;
    private final List<Data<?>> data;

    private SimpleTuple(Class<T> clazz, List<Data<?>> data) {
        this.persistenceManager = SimplePersistenceManager.getInstance();
        this.clazz = clazz;
        this.data = data;
    }

    public static <T> Tuple<T> empty() {
        return new SimpleTuple<>(null, new ArrayList<>());
    }

    public static <T> Tuple<T> of(Class<T> clazz, List<Data<?>> tuple) {
        return new SimpleTuple<>(clazz, tuple);
    }

    @Override
    public void insert(Table<T> table) {
        data.forEach(d -> d.insert(table));
    }

    @Override
    public void add(Data<?> data) {
        this.data.add(data);
    }

    @Override
    public Optional<Tuple<T>> findById(Table<T> table, String id) {
//        List<SimpleData<?>> data = IntStream.range(Constants.CUR_CELL_INIT_VAL, Constants.CUR_CELL_INIT_VAL + cellSize)
//                .mapToObj(i -> findData(Cursor.of(cursor.getRow(), i)).orElseThrow(() -> new JpaexlException(JpaexlCode.FAIL_TO_FIND_DATA)))
//                .collect(Collectors.toList());
//
//        return Optional.of(SimpleTuple.of(clazz, data));

        return Optional.of(SimpleTuple.empty());
    }

    @Override
    public Collection<Tuple<T>> findAll(Table<T> table) {
//        return findTuples(Cursor.row(Constants.CUR_ROW_INIT_VAL), Cursor.row(rowSize));
        return new ArrayList<>();
    }
}
