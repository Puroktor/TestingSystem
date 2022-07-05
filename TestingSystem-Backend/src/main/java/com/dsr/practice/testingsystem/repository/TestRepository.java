package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TestRepository extends PagingAndSortingRepository<Test, Integer> {
    @Query(value = "SELECT t FROM Test t WHERE lower(t.programmingLang) LIKE lower(concat('%', ?1,'%'))")
    Page<Test> findAllByProgrammingLang(String programmingLanguage, Pageable pageable);
}
