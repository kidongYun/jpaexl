package com.kian.yun.jpaexl.repository.helper;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Builder
@Entity
public class Dummy {
    @Id
    private Long id;

    private String column1;

    private String column2;

    private String column3;

    public static Dummy of(Long id) {
        return Dummy.builder()
                .id(id)
                .column1("col1 of " + id)
                .column2("col2 of " + id)
                .column3("col3 of " + id)
                .build();
    }
}
