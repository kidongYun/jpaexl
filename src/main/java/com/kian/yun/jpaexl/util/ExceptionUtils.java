package com.kian.yun.jpaexl.util;

import com.kian.yun.jpaexl.exception.ExceptionFunction;
import com.kian.yun.jpaexl.exception.ExceptionSupplier;

import java.util.function.Function;

public class ExceptionUtils {
    public static <T, R> Function<T, R> wrap(ExceptionFunction<T, R> f) {
        return (T t) -> {
            try {
                return f.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T> T wrap(ExceptionSupplier<T> s) {
        try {
            return s.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
