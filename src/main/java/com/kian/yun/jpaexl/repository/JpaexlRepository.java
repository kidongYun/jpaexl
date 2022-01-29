package com.kian.yun.jpaexl.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface JpaexlRepository<T, ID> extends PagingAndSortingRepository<T, ID> {
}
