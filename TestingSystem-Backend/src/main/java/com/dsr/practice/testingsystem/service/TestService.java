package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.entity.Test;
import com.dsr.practice.testingsystem.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class TestService {
    private final TestRepository testRepository;

    public Test createTest(@Valid Test test) {
        if (test.getQuestionsCount() > test.getQuestionsBank().size()) {
            throw new IllegalArgumentException("Invalid questions count");
        }
        return testRepository.save(test);
    }

    public Page<Test> fetchTestPage(String filter, @Min(value = 0, message = "Index must be >=0") int index,
                                    @Min(value = 1, message = "Page size must be >=1") int size) {
        Pageable pageable = PageRequest.of(index, size, Sort.by("id").ascending());
        if (filter == null || filter.trim().isEmpty()) {
            return testRepository.findAll(pageable);
        } else {
            return testRepository.findAllByProgrammingLang(filter, pageable);
        }
    }

    public void updateTest(int id, @Valid Test test) {
        if (test.getQuestionsCount() > test.getQuestionsBank().size()) {
            throw new IllegalArgumentException("Invalid questions count");
        }
        Optional<Test> oldTest = testRepository.findById(id);
        oldTest.ifPresent(testRepository::delete);
        test.setId(id);
        testRepository.save(test);
    }

    public void deleteTest(int id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invalid test Id:" + id));
        testRepository.delete(test);
    }

    public Test getTest(Integer id) {
        return testRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Invalid test Id:" + id));
    }
}
