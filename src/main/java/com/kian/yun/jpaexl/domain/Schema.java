package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.List;

@Getter
@Builder
public class Schema<T> {
    private final Class<T> type;
    private final List<Annotation> annotations;
    private final String name;
}
