package com.dsr.practice.testingsystem.mapper;

import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.FullTestDto;
import com.dsr.practice.testingsystem.dto.QuestionDto;
import com.dsr.practice.testingsystem.dto.TestInfoDto;
import com.dsr.practice.testingsystem.entity.Answer;
import com.dsr.practice.testingsystem.entity.Question;
import com.dsr.practice.testingsystem.entity.Test;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestMapper {

    public static TestInfoDto tooInfoDto(Test test) {
        return new TestInfoDto(test.getId(), test.getProgrammingLang(), test.getName(), test.getQuestionsCount());
    }

    public static FullTestDto toFullDtoWithoutRightAnswers(Test test) {
        TestInfoDto infoDto = tooInfoDto(test);
        Collections.shuffle(test.getQuestionsBank());
        List<QuestionDto> questionDtos = getQuestionListDto(test.getQuestionsBank().subList(0, test.getQuestionsCount()),
                false);
        return new FullTestDto(infoDto, questionDtos);
    }

    public static FullTestDto toFullDtoWithRightAnswers(Test test) {
        TestInfoDto infoDto = tooInfoDto(test);
        List<QuestionDto> questionDtos = getQuestionListDto(test.getQuestionsBank(), true);
        return new FullTestDto(infoDto, questionDtos);
    }

    private static List<QuestionDto> getQuestionListDto(List<Question> questions, boolean needAnswers) {
        List<QuestionDto> dtoList = new ArrayList<>();
        for (Question question : questions) {
            Function<Answer, AnswerDto> mapperFunc = needAnswers ? AnswerMapper::toDtoWithRight :
                    AnswerMapper::toDtoWithoutRight;
            List<AnswerDto> answerDtos = question.getAnswers().stream().map(mapperFunc).collect(Collectors.toList());
            if (!needAnswers) {
                Collections.shuffle(answerDtos);
            }
            QuestionDto questionDto = new QuestionDto(question.getId(), question.getText(), question.getMaxScore(),
                    answerDtos);
            dtoList.add(questionDto);
        }
        return dtoList;
    }

    public static Test toEntity(FullTestDto testDto) {
        Test test = new Test(testDto.getTestInfoDto().getId(), testDto.getTestInfoDto().getProgrammingLang(),
                testDto.getTestInfoDto().getName(), testDto.getTestInfoDto().getQuestionsCount(),
                new ArrayList<>(), new ArrayList<>());
        for (QuestionDto questionDto : testDto.getQuestionList()) {
            Question question = new Question(questionDto.getId(), test, questionDto.getText(),
                    questionDto.getMaxScore(), new ArrayList<>());
            for (AnswerDto answerDto : questionDto.getAnswers()) {
                Answer answer = new Answer(answerDto.getId(), answerDto.getText(), answerDto.getIsSelected(), question);
                question.getAnswers().add(answer);
            }
            test.getQuestionsBank().add(question);
        }
        return test;
    }
}
