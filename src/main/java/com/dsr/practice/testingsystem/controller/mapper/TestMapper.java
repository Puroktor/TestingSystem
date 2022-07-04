package com.dsr.practice.testingsystem.controller.mapper;

import com.dsr.practice.testingsystem.controller.dto.AnswerDto;
import com.dsr.practice.testingsystem.controller.dto.QuestionDto;
import com.dsr.practice.testingsystem.controller.dto.FullTestDto;
import com.dsr.practice.testingsystem.controller.dto.TestThemeDto;
import com.dsr.practice.testingsystem.entity.Answer;
import com.dsr.practice.testingsystem.entity.Question;
import com.dsr.practice.testingsystem.entity.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class TestMapper {
    public static TestThemeDto tooThemeDto(Test test) {
        return new TestThemeDto(test.getId(), test.getName(), test.getProgrammingLang(), test.getQuestionsCount());
    }

    public static FullTestDto toFullDto(Test test) {
        TestThemeDto themeDto = tooThemeDto(test);
        List<QuestionDto> shuffledDtoList = new ArrayList<>();
        List<Question> questionList = new ArrayList<>(test.getQuestionsBank());
        Collections.shuffle(questionList);
        for (Question question : questionList.subList(0, test.getQuestionsCount())) {
            List<AnswerDto> answerDtos = new ArrayList<>();
            for (Answer answer : question.getAnswers()) {
                answerDtos.add(new AnswerDto(answer.getId(), answer.getText(), false));
            }
            QuestionDto questionDto = new QuestionDto(question.getId(), question.getText(), question.getMaxScore(), answerDtos);
            shuffledDtoList.add(questionDto);
        }
        return new FullTestDto(themeDto, shuffledDtoList);
    }

    public static Test toEntity(FullTestDto testDto) {
        Test test = new Test(testDto.getTestThemeDto().getId(), testDto.getTestThemeDto().getProgrammingLang(),
                testDto.getTestThemeDto().getName(), testDto.getTestThemeDto().getQuestionsCount(),
                null, new HashSet<>());
        for (QuestionDto questionDto : testDto.getQuestionList()) {
            Question question = new Question(questionDto.getId(), test, questionDto.getText(),
                    questionDto.getMaxScore(), new HashSet<>());
            for (AnswerDto answerDto : questionDto.getAnswers()) {
                Answer answer = new Answer(answerDto.getId(), answerDto.getText(), answerDto.getIsSelected(), question);
                question.getAnswers().add(answer);
            }
            test.getQuestionsBank().add(question);
        }
        return test;
    }
}
