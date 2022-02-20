package com.kian.yun.jpaexl.exception;

public interface ExceptionSupplier<T> {
    T get() throws Exception;
}
