package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.entity.*;
import com.dsr.practice.testingsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final AttemptRepository attemptRepository;

    public User createUser(@Valid User user) {
        if (userRepository.findByNickname(user.getNickname()).isPresent()) {
            throw new IllegalStateException("User with such nickname already exists");
        } else if (!user.getEmail().matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
            throw new ValidationException("Not valid email");
        }
        return userRepository.save(user);
    }

    public Integer loginUser(User user) {
        Optional<User> dbUser = userRepository.findByNickname(user.getNickname());
        if (!dbUser.isPresent() || !dbUser.get().getPasswordHash().equals(user.getPasswordHash())) {
            throw new IllegalStateException("Invalid name or password!");
        }
        return dbUser.get().getId();
    }

    public void submitAttempt(Test submittedTest, int studentId) {
        Test dbTest = testRepository.findById(submittedTest.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid test Id:" + submittedTest.getId()));
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + studentId));
        double score = 0, maxScore = 0;
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
            maxScore += dbQuestion.getMaxScore();
        }
        attemptRepository.save(new Attempt(0, user, dbTest, score / maxScore * 100));
    }
}
