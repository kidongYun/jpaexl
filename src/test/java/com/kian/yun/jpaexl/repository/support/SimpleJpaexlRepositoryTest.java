package com.kian.yun.jpaexl.repository.support;

import com.kian.yun.jpaexl.domain.PersistenceManager;
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
        this.jpaexlRepository = new SimpleJpaexlRepository<>(PersistenceManager.getInstance());
    }

    @Test
    @DisplayName("save() 함수를 호출했을 때 정상의 경우 엑셀 형식의 파일을 추출해야 한다")
    public void save_it_has_to_export_xlsx_file() {
        // given
        Dummy dummy = Dummy.of(1L);

        // when
        jpaexlRepository.save(dummy);
    }
}