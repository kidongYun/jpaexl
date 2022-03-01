package com.kian.yun.jpaexl.repository.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@ToString
@Entity
public class Dummy {
    @Id
    private Long id;

    private String column1;

    private String column2;

    private String column3;

    private LocalDate column4;

    public static Dummy of(Long id) {
        return Dummy.builder()
                .id(id)
                .column1("col1 of " + id)
                .column2("col2 of " + id)
                .column3("col3 of " + id)
                .column4(LocalDate.now())
                .build();
    }
}
