package com.kian.yun.jpaexl.util;


import com.kian.yun.jpaexl.exception.JpaexlException;
import com.kian.yun.jpaexl.repository.helper.Dummy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReflectionUtilsTest {
    @Test
    @DisplayName("when getFieldByAnnotation() is called with id annotation, then it has to return the field is defined with this annotation. ")
    public void getFieldByAnnotation_1() {
        // given
        Dummy dummy = Dummy.of(1L);

        // when
        Field result = ReflectionUtils.getFieldByAnnotation(Id.class, dummy.getClass().getDeclaredFields());

        // then
        assertThat(result.getName()).isEqualTo("id");
        assertThat(result.getType()).isEqualTo(Long.class);
    }

    @Test
    @DisplayName("when getFieldByAnnotation() is called with the annotation don't have, then it should throw JpaexlException. ")
    public void getFieldByAnnotation_2() {
        // given
        Dummy dummy = Dummy.of(1L);

        // when, then
        assertThatThrownBy(() -> ReflectionUtils.getFieldByAnnotation(ManyToOne.class, dummy.getClass().getDeclaredFields()))
                .isInstanceOf(JpaexlException.class);
    }
}