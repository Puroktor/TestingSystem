package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.dto.QuestionDto;
import com.dsr.practice.testingsystem.dto.TestDto;
import com.dsr.practice.testingsystem.dto.TestInfoDto;
import com.dsr.practice.testingsystem.entity.Question;
import com.dsr.practice.testingsystem.entity.Test;
import com.dsr.practice.testingsystem.mapper.TestMapper;
import com.dsr.practice.testingsystem.repository.QuestionRepository;
import com.dsr.practice.testingsystem.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;
    private final TestMapper testMapper;

    public TestDto createTest(TestDto testDto) {
        if (testDto.getTestInfo().getQuestionsCount() > testDto.getQuestions().size()) {
            throw new IllegalArgumentException("Invalid questions count");
        } else if (testDto.getTestInfo().getId() != null) {
            throw new IllegalArgumentException("Test must not have id");
        }
        Test test = testMapper.toEntity(testDto);
        test = testRepository.save(test);
        return testMapper.toDto(test);
    }

    public Page<TestInfoDto> fetchTestPage(String filter, int index, int size) {
        Pageable pageable = PageRequest.of(index, size, Sort.by("id").ascending());
        Page<Test> page;
        if (filter == null || filter.trim().isEmpty()) {
            page = testRepository.findAll(pageable);
        } else {
            page = testRepository.findAllByProgrammingLang(filter, pageable);
        }
        return page.map(test -> modelMapper.map(test, TestInfoDto.class));
    }

    @Transactional
    public void updateTest(int id, TestDto newTestDto) {
        if (newTestDto.getTestInfo().getQuestionsCount() > newTestDto.getQuestions().size()) {
            throw new IllegalArgumentException("Invalid questions count");
        }
        Test newTest = testMapper.toEntity(newTestDto);
        Optional<Test> optionalTest = testRepository.findById(id);
        if (optionalTest.isPresent()) {
            Test oldTest = optionalTest.get();
            BeanUtils.copyProperties(newTest, oldTest, "questions", "attempts");
            oldTest.getAttempts().clear();
            oldTest.getAttempts().addAll(newTest.getAttempts());
            oldTest.getQuestions().clear();
            for (Question newQuestion : newTest.getQuestions()) {
                Optional<Question> optionalQuestion;
                Integer qId = newQuestion.getId();
                if (qId != null && (optionalQuestion = questionRepository.findById(qId)).isPresent()) {
                    Question oldQuestion = copyQuestion(newQuestion, optionalQuestion.get());
                    oldTest.getQuestions().add(oldQuestion);
                } else {
                    Question savedQuestion = questionRepository.save(newQuestion);
                    oldTest.getQuestions().add(savedQuestion);
                }
            }
            testRepository.save(oldTest);
        } else {
            newTest.setId(id);
            testRepository.save(newTest);
        }
    }

    private Question copyQuestion(Question from, Question to) {
        BeanUtils.copyProperties(from, to, "answers");
        to.getAnswers().clear();
        to.getAnswers().addAll(from.getAnswers());
        return to;
    }

    public void deleteTest(int id) {
        Test test = testRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Invalid test Id:" + id));
        testRepository.delete(test);
    }

    public TestDto getTest(int id) {
        Test test = testRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Invalid test Id:" + id));
        return testMapper.toDto(test);
    }

    public TestDto getShuffledTest(int id) {
        Test test = testRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Invalid test Id:" + id));
        TestDto testDto = testMapper.toDto(test);
        Collections.shuffle(testDto.getQuestions());
        List<QuestionDto> questions = testDto.getQuestions().subList(0, testDto.getTestInfo().getQuestionsCount());
        for (QuestionDto questionDto : questions) {
            questionDto.getAnswers().forEach((answerDto -> answerDto.setIsRight(false)));
        }
        testDto.setQuestions(questions);
        return testDto;
    }
}
