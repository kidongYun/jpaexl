package com.kian.yun.jpaexl.exception;

public interface ExceptionFunction<T, R> {
    R apply(T t) throws Exception;
}
