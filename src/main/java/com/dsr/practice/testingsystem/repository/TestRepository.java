package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TestRepository extends PagingAndSortingRepository<Test, Integer> {
    Page<Test> findAllByProgrammingLanguage(String theme, Pageable pageable);
}
