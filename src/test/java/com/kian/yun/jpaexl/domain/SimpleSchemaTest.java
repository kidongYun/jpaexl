package com.kian.yun.jpaexl.domain;

import com.kian.yun.jpaexl.repository.helper.Dummy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleSchemaTest {

    @Test
    @DisplayName("")
    public void of_shouldBeMappedFieldsProperly() {
        // given

        // when
        Schema<Long> schema = SimpleSchema.of(Dummy.class.getDeclaredFields()[0]);

        // then
        assertThat(schema.getName()).isEqualTo(Dummy.class.getDeclaredFields()[0].getName());
        assertThat(schema.getType()).isEqualTo(Dummy.class.getDeclaredFields()[0].getType());
        assertThat(schema.isIdentifier()).isEqualTo(true);
    }

}