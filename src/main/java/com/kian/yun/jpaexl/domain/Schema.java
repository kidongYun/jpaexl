package com.kian.yun.jpaexl.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Schema<T> {
    private final Class<T> type;
    private final String name;
}
