package com.kian.yun.jpaexl.repository.support;

import com.kian.yun.jpaexl.repository.JpaexlRepository;
import com.kian.yun.jpaexl.repository.helper.Dummy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
class SimpleJpaexlRepositoryTest {
    private JpaexlRepository<Dummy, Long> jpaexlRepository;

    @BeforeEach
    public void setUp() {
        this.jpaexlRepository = new SimpleJpaexlRepository<>(Dummy.class);
    }

    @Test
    @DisplayName("save() 함수를 한번 호출했을 때 정상의 경우 엑셀 형식의 파일로 추출되어야 한다")
    public void save_called_one_time_then_it_has_to_export_xlsx_file() {
        // given
        Dummy dummy = Dummy.of(1L);

        // when
        jpaexlRepository.save(dummy);
    }

    @Test
    @DisplayName("save() 함수를 여러번 호출했을 때 정상의 경우 엑셀 형식의 파일로 추출되어야 한다")
    public void save_called_multiple_time_then_they_have_to_export_xlsx_file() {
        // given
        Dummy dummy1 = Dummy.of(1L);
        Dummy dummy2 = Dummy.of(2L);

        // when
        jpaexlRepository.save(dummy1);
        jpaexlRepository.save(dummy2);
    }

    @Test
    @DisplayName("")
    public void findById_test() {
        // given
        Dummy dummy = jpaexlRepository.findById(2L).orElse(null);

        // when
        log.info(dummy.toString());

        // then
    }
}