package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.entity.*;
import com.dsr.practice.testingsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class TestService {
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final StudentRepository studentRepository;
    private final AttemptRepository attemptRepository;

    public Test createTest(@Valid Test test) {
        return testRepository.save(test);
    }

    public Page<Test> fetchTestPage(String filter, @Min(0) int index, @Min(1) int size) {
        Pageable pageable = PageRequest.of(index, size, Sort.by("id").ascending());
        if (filter == null || filter.trim().isEmpty()) {
            return testRepository.findAll(pageable);
        } else {
            return testRepository.findAllByProgrammingLang(filter, pageable);
        }
    }

    public void updateTest(int id, @Valid Test test) {
        Optional<Test> oldTest = testRepository.findById(id);
        if (!oldTest.isPresent()) {
            throw new IllegalArgumentException("Invalid test Id:" + id);
        }
        test.setId(id);
        // Should attempts be dumped?
        // test.setAttempts(oldTest.get().getAttempts());
        testRepository.save(test);
    }

    public void deleteTest(int id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid test Id:" + id));
        testRepository.delete(test);
    }


    public Test getTest(Integer id) {
        return testRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid test Id:" + id));
    }

    public void submitAttempt(Test submittedTest, int studentId) {
        Test dbTest = testRepository.findById(submittedTest.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid test Id:" + submittedTest.getId()));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + studentId));
        double score = 0;
        for (Question submittedQuestion : submittedTest.getQuestionsBank()) {
            Question dbQuestion = questionRepository.findById(submittedQuestion.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid question Id:" + submittedQuestion.getId()));

            int correct = 0, all = 0;
            for (Answer submittedAnswer : submittedQuestion.getAnswers()) {
                Answer dbAnswer = answerRepository.findById(submittedAnswer.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid answer Id:" + submittedAnswer.getId()));
                if (dbAnswer.getIsRight() == submittedAnswer.getIsRight()) {
                    correct++;
                } else {
                    correct--;
                }
                all++;
            }
            if (correct > 0) {
                score += dbQuestion.getMaxScore() * (double) correct / all;
            }
        }
        Attempt attempt = new Attempt();
        attempt.setTest(dbTest);
        attempt.setStudent(student);
        attempt.setScore(score);
        attemptRepository.save(new Attempt());

    }
}
