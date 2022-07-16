package com.dsr.practice.testingsystem;

import com.dsr.practice.testingsystem.dto.*;
import com.dsr.practice.testingsystem.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class SampleDataProvider {
    public TestDto getValidTestDtoWithBank() {
        List<AnswerDto> answers = Stream.of(
                        new AnswerDto(1, "First Answer", false),
                        new AnswerDto(2, "Second Answer", true),
                        new AnswerDto(3, "Third Answer", true),
                        new AnswerDto(4, "Fourth Answer", false),
                        new AnswerDto(5, "Fifth Answer", true))
                .collect(Collectors.toList());
        List<QuestionDto> questions = Stream.of(
                        new QuestionDto(1, "First Question", 2, null, answers.subList(0, 2)),
                        new QuestionDto(2, "Second question", 4, null, answers.subList(2, 5)))
                .collect(Collectors.toList());
        return new TestDto(1, "Java", "First test", 1, TestType.WITH_BANK, questions);
    }

    public TestDto getValidTestDtoWithOptions() {
        List<AnswerDto> answers = Stream.of(
                        new AnswerDto(1, "Answer 1", true),
                        new AnswerDto(2, "Answer 2", false),
                        new AnswerDto(3, "Answer 3", true),
                        new AnswerDto(4, "Answer 4", true),
                        new AnswerDto(5, "Answer 5", true),
                        new AnswerDto(6, "Answer 6", true))
                .collect(Collectors.toList());
        List<QuestionDto> questions = Stream.of(
                        new QuestionDto(1, "Question 1.1", 2, 0, answers.subList(0, 2)),
                        new QuestionDto(2, "Question 1.2", 2, 0, answers.subList(2, 4)),
                        new QuestionDto(3, "Question 2.1", 4, 1, answers.subList(4, 5)),
                        new QuestionDto(4, "Question 2.2", 4, 1, answers.subList(5, 6)))
                .collect(Collectors.toList());
        return new TestDto(1, "Java", "First test", 2, TestType.WITH_QUESTION_OPTIONS, questions);
    }

    public Test getValidTestWithBank() {
        List<Answer> answers = Stream.of(
                        new Answer(1, "First Answer", false, null),
                        new Answer(2, "Second Answer", true, null),
                        new Answer(3, "Third Answer", true, null),
                        new Answer(4, "Fourth Answer", false, null),
                        new Answer(5, "Fifth Answer", true, null))
                .collect(Collectors.toList());
        List<Question> questions = Stream.of(
                        new Question(1, null, "First Question", 2, null, answers.subList(0, 2)),
                        new Question(2, null, "Second question", 4, null, answers.subList(2, 5)))
                .collect(Collectors.toList());
        Test test = new Test(1, "Java", "First test", 1, TestType.WITH_BANK,
                new ArrayList<>(), questions);
        answers.subList(0, 2).forEach(answer -> answer.setQuestion(questions.get(0)));
        answers.subList(2, 5).forEach(answer -> answer.setQuestion(questions.get(1)));
        questions.forEach(question -> question.setTest(test));
        return test;
    }

    public UserRegistrationDto getValidRegistrationDto() {
        return new UserRegistrationDto("User User User", "User", "qwerty", Role.STUDENT,
                "The university", 2, 4, "user@user.com");
    }

    public UserLoginDto getValidLoginDto() {
        return new UserLoginDto("User", "qwerty");
    }

    public JwtTokensDto getJwtTokensDto() {
        return new JwtTokensDto("token1", "token2");
    }

    public User getUser() {
        return new User(1, "User User User", "User", "qwerty", Role.STUDENT,
                "The university", 2, 4, "user@user.com", new ArrayList<>());
    }
}
