package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.Question;
import com.dsr.practice.testingsystem.entity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestRepositoryTests extends PostgresRepository {
    @Autowired
    private TestRepository testRepository;

    @org.junit.jupiter.api.Test
    public void findAllByExistingProgrammingLang() {
        Test test = getTestForDb();
        entityManager.persist(test);

        Page<Test> page = testRepository.findAllByProgrammingLang("jav", PageRequest.of(0, 1));
        List<Test> testList = page.getContent();

        assertEquals(1, testList.size());
        assertEquals(test, testList.get(0));
    }

    @org.junit.jupiter.api.Test
    public void findAllByMissingProgrammingLang() {
        Test test = getTestForDb();
        entityManager.persist(test);

        Page<Test> page = testRepository.findAllByProgrammingLang("javAA", PageRequest.of(0, 1));
        List<Test> testList = page.getContent();

        assertTrue(page.isEmpty());
        assertEquals(0, page.getTotalElements());
        assertEquals(0, testList.size());
    }

    private Test getTestForDb() {
        Test test = dataProvider.getValidTestWithBank();
        test.setId(null);
        test.setProgrammingLang("Java");
        for (Question question : test.getQuestions()) {
            question.setId(null);
            question.getAnswers().forEach(ans -> ans.setId(null));
        }
        return test;
    }
}
