package com.dsr.practice.testingsystem.mapper;

import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.QuestionDto;
import com.dsr.practice.testingsystem.dto.TestDto;
import com.dsr.practice.testingsystem.dto.TestInfoDto;
import com.dsr.practice.testingsystem.entity.Answer;
import com.dsr.practice.testingsystem.entity.Question;
import com.dsr.practice.testingsystem.entity.Test;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TestMapper {
    private final ModelMapper modelMapper;

    public TestDto toDto(Test test) {
        TestInfoDto infoDto = modelMapper.map(test, TestInfoDto.class);
        List<QuestionDto> questionsDtoList = new ArrayList<>();
        for (Question question : test.getQuestions()) {
            List<AnswerDto> answersDtoList = question.getAnswers().stream()
                    .map((ans) -> modelMapper.map(ans, AnswerDto.class))
                    .collect(Collectors.toList());
            QuestionDto questionDto = modelMapper.map(question, QuestionDto.class);
            questionDto.setAnswers(answersDtoList);
            questionsDtoList.add(questionDto);
        }
        return new TestDto(infoDto, questionsDtoList);
    }

    public Test toEntity(TestDto testDto) {
        Test test = modelMapper.map(testDto.getTestInfo(), Test.class);
        test.setAttempts(new ArrayList<>());
        test.setQuestions(new ArrayList<>());
        for (QuestionDto questionDto : testDto.getQuestions()) {
            Question question = modelMapper.map(questionDto, Question.class);
            List<Answer> answers = questionDto.getAnswers().stream()
                    .map((ans) -> modelMapper.map(ans, Answer.class))
                    .collect(Collectors.toList());
            answers.forEach((ans) -> ans.setQuestion(question));
            question.setAnswers(answers);
            question.setTest(test);
            test.getQuestions().add(question);
        }
        return test;
    }
}
