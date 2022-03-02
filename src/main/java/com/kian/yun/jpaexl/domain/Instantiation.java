package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.code.JpaexlCode;
import com.kian.yun.jpaexl.exception.JpaexlException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum Instantiation {
    STRING(String.class, new Class<?>[]{ String.class }, s -> new Object[]{s}),
    LONG(Long.class, new Class<?>[]{ String.class }, s -> new Object[]{s}),
    LOCALDATE(LocalDate.class, new Class<?>[]{ int.class, int.class, int.class }, s -> Arrays.stream(s.split("-")).mapToInt(Integer::parseInt).mapToObj(i -> (Object)i).toArray()),
    ;

    private final Class<?> instanceType;
    private final Class<?>[] parameterTypes;
    private final Function<String, Object[]> proc;

    public static Instantiation of(Class<?> instanceType) {
        return Arrays.stream(Instantiation.values())
                .filter(instantiation -> instantiation.getInstanceType().equals(instanceType))
                .findFirst()
                .orElseThrow(() -> JpaexlException.of(JpaexlCode.FAIL_TO_FIND_INSTANCE_TYPE));
    }
}
