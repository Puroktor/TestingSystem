package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.entity.Question;
import com.dsr.practice.testingsystem.entity.Test;
import com.dsr.practice.testingsystem.repository.QuestionRepository;
import com.dsr.practice.testingsystem.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class TestService {
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

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

    @Transactional
    public void updateTest(int id, @Valid Test newTest) {
        if (newTest.getQuestionsCount() > newTest.getQuestionsBank().size()) {
            throw new IllegalArgumentException("Invalid questions count");
        }
        Optional<Test> optionalTest = testRepository.findById(id);
        if (optionalTest.isPresent()) {
            Test oldTest = optionalTest.get();
            BeanUtils.copyProperties(newTest, oldTest, "questionsBank", "attempts");
            oldTest.getAttempts().clear();
            oldTest.getAttempts().addAll(newTest.getAttempts());
            oldTest.getQuestionsBank().clear();
            for (Question newQuestion : newTest.getQuestionsBank()) {
                Optional<Question> optionalQuestion = questionRepository.findById(newQuestion.getId());
                if (optionalQuestion.isPresent()) {
                    Question oldQuestion = optionalQuestion.get();
                    BeanUtils.copyProperties(newQuestion, oldQuestion, "answers");
                    oldQuestion.getAnswers().clear();
                    oldQuestion.getAnswers().addAll(newQuestion.getAnswers());
                    oldTest.getQuestionsBank().add(oldQuestion);
                } else {
                    oldTest.getQuestionsBank().add(newQuestion);
                }
            }
            testRepository.save(oldTest);
        } else {
            newTest.setId(id);
            testRepository.save(newTest);
        }
    }

    public void deleteTest(int id) {
        Test test = testRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Invalid test Id:" + id));
        testRepository.delete(test);
    }

    public Test getTest(Integer id) {
        return testRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Invalid test Id:" + id));
    }
}
