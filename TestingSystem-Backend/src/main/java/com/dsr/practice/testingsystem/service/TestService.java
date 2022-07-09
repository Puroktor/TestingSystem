package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.dto.QuestionDto;
import com.dsr.practice.testingsystem.dto.TestDto;
import com.dsr.practice.testingsystem.dto.TestInfoDto;
import com.dsr.practice.testingsystem.entity.Question;
import com.dsr.practice.testingsystem.entity.Test;
import com.dsr.practice.testingsystem.entity.TestType;
import com.dsr.practice.testingsystem.mapper.TestMapper;
import com.dsr.practice.testingsystem.repository.QuestionRepository;
import com.dsr.practice.testingsystem.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;
    private final TestMapper testMapper;

    public TestDto createTest(TestDto testDto) {
        validateTestDto(testDto).ifPresent(violations -> {
            throw new IllegalArgumentException(violations);
        });
        if (testDto.getId() != null) {
            throw new IllegalArgumentException("Test must not have id");
        }
        Test test = testMapper.toEntity(testDto);
        test = testRepository.save(test);
        return testMapper.toDto(test);
    }

    public Page<TestInfoDto> fetchTestPage(String filter, int index, int size) {
        Pageable pageable = PageRequest.of(index, size);
        Page<Test> page;
        if (filter == null || filter.trim().isEmpty()) {
            page = testRepository.findAll(pageable);
        } else {
            page = testRepository.findAllByProgrammingLang(filter, pageable);
        }
        return page.map(test -> modelMapper.map(test, TestInfoDto.class));
    }

    @Transactional
    public TestDto getTest(int id) {
        Test test = testRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Invalid test Id:" + id));
        return testMapper.toDto(test);
    }

    @Transactional
    public TestDto getShuffledTest(int id) {
        TestDto testDto = getTest(id);
        if (testDto.getTestType() == TestType.WITH_BANK) {
            Collections.shuffle(testDto.getQuestions());
            List<QuestionDto> questions = testDto.getQuestions().subList(0, testDto.getQuestionsCount());
            for (QuestionDto questionDto : questions) {
                questionDto.getAnswers().forEach((answerDto -> answerDto.setIsRight(false)));
            }
            testDto.setQuestions(questions);
        } else if (testDto.getTestType() == TestType.WITH_QUESTION_OPTIONS) {
            List<List<QuestionDto>> questionTemplates = new ArrayList<>();
            for (int i = 0; i < testDto.getQuestionsCount(); i++) {
                questionTemplates.add(new ArrayList<>());
            }
            for (QuestionDto questionDto : testDto.getQuestions()) {
                int index = questionDto.getQuestionTemplateIndex();
                List<QuestionDto> questionOptions = questionTemplates.get(index);
                questionOptions.add(questionDto);
            }
            Random rand = new Random();
            List<QuestionDto> questions = new ArrayList<>();
            for (List<QuestionDto> questionOptions : questionTemplates) {
                QuestionDto questionDto = questionOptions.get(rand.nextInt(questionOptions.size()));
                questionDto.getAnswers().forEach((answerDto -> answerDto.setIsRight(false)));
                questions.add(questionDto);
            }
            testDto.setQuestions(questions);
        }
        return testDto;
    }

    @Transactional
    public void updateTest(int id, TestDto newTestDto) {
        validateTestDto(newTestDto).ifPresent(violations -> {
            throw new IllegalArgumentException(violations);
        });
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

    private Optional<String> validateTestDto(TestDto testDto) {
        Set<String> violations = new HashSet<>();
        if (testDto.getTestType() == TestType.WITH_BANK) {
            if (testDto.getQuestionsCount() > testDto.getQuestions().size()) {
                violations.add("Question count for student must be <= question bank size");
            }
            for (QuestionDto questionDto : testDto.getQuestions()) {
                if (questionDto.getQuestionTemplateIndex() != null) {
                    violations.add("Question must not have template index");
                    break;
                }
            }
        } else if (testDto.getTestType() == TestType.WITH_QUESTION_OPTIONS) {
            boolean[] questionTemplatesPresence = new boolean[testDto.getQuestionsCount()];
            for (QuestionDto questionDto : testDto.getQuestions()) {
                Integer index = questionDto.getQuestionTemplateIndex();
                if (index == null) {
                    violations.add("Enter question template index");
                } else if (questionDto.getQuestionTemplateIndex() >= testDto.getQuestionsCount()) {
                    violations.add("Question template index must be < question count for students");
                } else {
                    questionTemplatesPresence[index] = true;
                }
            }
            for (boolean isPresent : questionTemplatesPresence) {
                if (!isPresent) {
                    violations.add("All question templates must have at least 1 question option");
                    break;
                }
            }
        }
        StringBuilder violationsBuilder = new StringBuilder();
        violations.forEach(violation -> violationsBuilder.append(violation).append('\n'));
        String violationsMessage = violationsBuilder.toString();
        return Optional.ofNullable(violationsMessage.equals("") ? null : violationsMessage);
    }

}
