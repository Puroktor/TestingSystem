package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.SampleDataProvider;
import com.dsr.practice.testingsystem.dto.QuestionDto;
import com.dsr.practice.testingsystem.dto.TestDto;
import com.dsr.practice.testingsystem.dto.TestInfoDto;
import com.dsr.practice.testingsystem.entity.Attempt;
import com.dsr.practice.testingsystem.entity.Question;
import com.dsr.practice.testingsystem.entity.Test;
import com.dsr.practice.testingsystem.entity.User;
import com.dsr.practice.testingsystem.mapper.TestMapper;
import com.dsr.practice.testingsystem.repository.AttemptRepository;
import com.dsr.practice.testingsystem.repository.QuestionRepository;
import com.dsr.practice.testingsystem.repository.TestRepository;
import com.dsr.practice.testingsystem.repository.UserRepository;
import org.mockito.ArgumentMatchers;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class TestServiceTests {
    @Autowired
    private SampleDataProvider dataProvider;
    @Autowired
    private TestService testService;
    @MockBean
    private TestRepository testRepository;
    @MockBean
    private QuestionRepository questionRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AttemptRepository attemptRepository;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private TestMapper testMapper;

    @org.junit.jupiter.api.Test
    public void createValidTest() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.setId(null);
        Test testEntity = new Test();
        when(testMapper.toEntity(testDto)).thenReturn(testEntity);
        when(testRepository.save(testEntity)).thenReturn(testEntity);
        when(testMapper.toDto(testEntity)).thenReturn(dataProvider.getValidTestDtoWithBank());

        assertEquals(dataProvider.getValidTestDtoWithBank(), testService.createTest(testDto));
        verify(testMapper, times(1)).toEntity(testDto);
        verify(testRepository, times(1)).save(testEntity);
        verify(testMapper, times(1)).toDto(testEntity);
    }

    @org.junit.jupiter.api.Test
    public void createTestWithId() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();

        assertThrows(IllegalArgumentException.class, () -> testService.createTest(testDto));
    }

    @org.junit.jupiter.api.Test
    public void createTestWithoutRightAnswer() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).getAnswers().forEach(ans -> ans.setIsRight(false));

        assertThrows(IllegalArgumentException.class, () -> testService.createTest(testDto));
    }

    @org.junit.jupiter.api.Test
    public void createTestWithLackOfQuestions() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.setQuestionsCount(10);

        assertThrows(IllegalArgumentException.class, () -> testService.createTest(testDto));
    }

    @org.junit.jupiter.api.Test
    public void createTestWithBankWithTemplateIndex() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).setQuestionTemplateIndex(0);

        assertThrows(IllegalArgumentException.class, () -> testService.createTest(testDto));
    }

    @org.junit.jupiter.api.Test
    public void fetchTestPageOfMissingUser() {
        String filter = "filter", nickname = "user";
        int index = 0, size = 1;
        when(userRepository.findByNickname(nickname)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> testService.fetchTestPage(filter, index, size, nickname));
        verify(userRepository, times(1)).findByNickname(nickname);
    }

    @org.junit.jupiter.api.Test
    public void fetchTestPageWithFilter() {
        fetchTestPage("filter");
    }

    @org.junit.jupiter.api.Test
    public void fetchTestPageWithNullFilter() {
        fetchTestPage(null);
    }

    @org.junit.jupiter.api.Test
    public void fetchTestPageWithBlankFilter() {
        fetchTestPage("        ");
    }

    private void fetchTestPage(String filter) {
        String nickname = "user";
        int index = 0, size = 1;
        Pageable pageable = PageRequest.of(index, size, Sort.by("id").descending());
        Test test = dataProvider.getValidTestWithBank();
        User user = dataProvider.getUser();
        Page<Test> page = new PageImpl<>(Collections.singletonList(test));
        Attempt attempt = new Attempt();
        attempt.setScore(100d);
        when(userRepository.findByNickname(nickname)).thenReturn(Optional.of(user));
        when(testRepository.findAllByProgrammingLang(filter, pageable)).thenReturn(page);
        when(testRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(test, TestInfoDto.class)).thenReturn(new TestInfoDto());
        when(attemptRepository.findTopByUserAndTestOrderByDateTimeDesc(user, test)).thenReturn(Optional.of(attempt));

        TestInfoDto infoDto = new TestInfoDto();
        infoDto.setUserScore(attempt.getScore());
        boolean isBlank = filter == null || filter.trim().isEmpty();
        assertEquals(new PageImpl<>(Collections.singletonList(infoDto)),
                testService.fetchTestPage(filter, index, size, nickname));
        verify(userRepository, times(1)).findByNickname(nickname);
        verify(testRepository, times(isBlank ? 0 : 1)).findAllByProgrammingLang(filter, pageable);
        verify(testRepository, times(isBlank ? 1 : 0)).findAll(pageable);
        verify(modelMapper, times(1)).map(test, TestInfoDto.class);
        verify(attemptRepository, times(1)).findTopByUserAndTestOrderByDateTimeDesc(
                dataProvider.getUser(), dataProvider.getValidTestWithBank());
    }

    @org.junit.jupiter.api.Test
    public void getExistingTest() {
        Test testEntity = new Test();
        TestDto testDto = new TestDto();
        testEntity.setId(1);
        testDto.setId(1);
        when(testRepository.findById(testEntity.getId())).thenReturn(Optional.of(testEntity));
        when(testMapper.toDto(testEntity)).thenReturn(new TestDto());

        assertEquals(new TestDto(), testService.getTest(testEntity.getId()));
        verify(testRepository, times(1)).findById(testEntity.getId());
        verify(testMapper, times(1)).toDto(testEntity);
    }

    @org.junit.jupiter.api.Test
    public void getMissingTest() {
        int testId = 1;
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> testService.getTest(testId));
        verify(testRepository, times(1)).findById(testId);
    }

    @org.junit.jupiter.api.Test
    public void getShuffledTestWithBank() {
        Test testEntity = new Test();
        testEntity.setId(1);
        when(testRepository.findById(testEntity.getId())).thenReturn(Optional.of(testEntity));
        when(testMapper.toDto(testEntity)).thenReturn(dataProvider.getValidTestDtoWithBank());
        TestDto returned = testService.getShuffledTest(testEntity.getId());
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        QuestionDto returnedQuestion = returned.getQuestions().get(0);
        QuestionDto modelQuestion;
        if (returnedQuestion.getId().equals(testDto.getQuestions().get(0).getId())) {
            modelQuestion = testDto.getQuestions().get(0);
        } else {
            modelQuestion = testDto.getQuestions().get(1);
        }
        modelQuestion.getAnswers().forEach(ans -> ans.setIsRight(false));

        assertTrue(new ReflectionEquals(returned, "questions").matches(testDto));
        assertEquals(testDto.getQuestionsCount(), returned.getQuestions().size());
        assertTrue(new ReflectionEquals(returnedQuestion, "answers").matches(modelQuestion));
        assertEquals(modelQuestion.getAnswers().size(), returnedQuestion.getAnswers().size());
        assertTrue(returnedQuestion.getAnswers().containsAll(modelQuestion.getAnswers()));
        assertTrue(modelQuestion.getAnswers().containsAll(returnedQuestion.getAnswers()));
        verify(testRepository, times(1)).findById(testEntity.getId());
        verify(testMapper, times(1)).toDto(testEntity);
    }

    @org.junit.jupiter.api.Test
    public void getShuffledTestWithOptions() {
        Test testEntity = new Test();
        testEntity.setId(1);
        when(testRepository.findById(testEntity.getId())).thenReturn(Optional.of(testEntity));
        when(testMapper.toDto(testEntity)).thenReturn(dataProvider.getValidTestDtoWithOptions());
        TestDto returned = testService.getShuffledTest(testEntity.getId());
        TestDto testDto = dataProvider.getValidTestDtoWithOptions();

        assertTrue(new ReflectionEquals(returned, "questions").matches(testDto));
        assertEquals(testDto.getQuestionsCount(), returned.getQuestions().size());
        for (int i = 0; i < 2; i++) {
            QuestionDto returnedQuestion = returned.getQuestions().get(i);
            assertEquals(i, returnedQuestion.getQuestionTemplateIndex());

            QuestionDto modelQuestion = returnedQuestion.getId().equals(testDto.getQuestions().get(2 * i).getId())
                    ? testDto.getQuestions().get(2 * i) : testDto.getQuestions().get(2 * i + 1);
            modelQuestion.getAnswers().forEach(ans -> ans.setIsRight(false));

            assertTrue(new ReflectionEquals(returnedQuestion, "answers").matches(modelQuestion));
            assertEquals(modelQuestion.getAnswers().size(), returnedQuestion.getAnswers().size());
            assertTrue(returnedQuestion.getAnswers().containsAll(modelQuestion.getAnswers()));
            assertTrue(modelQuestion.getAnswers().containsAll(returnedQuestion.getAnswers()));
        }
        verify(testRepository, times(1)).findById(testEntity.getId());
        verify(testMapper, times(1)).toDto(testEntity);
    }

    @org.junit.jupiter.api.Test
    public void getShuffledMissingTest() {
        int testId = 1;
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> testService.getShuffledTest(testId));
        verify(testRepository, times(1)).findById(testId);
    }

    @org.junit.jupiter.api.Test
    public void updateExistingTest() {
        TestDto testDto = dataProvider.getValidTestDtoWithOptions();
        Test oldTest = dataProvider.getValidTestWithBank();
        oldTest.setName("old");
        oldTest.getQuestions().get(0).setText("old");
        Test newTest = dataProvider.getValidTestWithBank();
        when(testMapper.toEntity(testDto)).thenReturn(newTest);
        when(testRepository.findById(testDto.getId())).thenReturn(Optional.of(oldTest));
        when(testRepository.save(oldTest)).thenReturn(oldTest);
        when(questionRepository.findById(ArgumentMatchers.anyInt())).thenReturn(Optional.empty());
        when(questionRepository.save(ArgumentMatchers.any(Question.class))).thenAnswer(i -> i.getArguments()[0]);
        testService.updateTest(testDto.getId(), testDto);

        assertEquals(oldTest, newTest);
        verify(testRepository, times(1)).findById(testDto.getId());
        verify(testMapper, times(1)).toEntity(testDto);
        verify(testRepository, times(1)).save(oldTest);
        verify(questionRepository, times(2)).findById(ArgumentMatchers.anyInt());
        verify(questionRepository, times(2)).save(ArgumentMatchers.any(Question.class));
    }

    @org.junit.jupiter.api.Test
    public void updateMissingTest() {
        TestDto testDto = dataProvider.getValidTestDtoWithOptions();
        Test testEntity = new Test();
        when(testMapper.toEntity(testDto)).thenReturn(testEntity);
        when(testRepository.findById(testDto.getId())).thenReturn(Optional.empty());
        when(testRepository.save(testEntity)).thenReturn(testEntity);
        testService.updateTest(testDto.getId(), testDto);

        verify(testMapper, times(1)).toEntity(testDto);
        verify(testRepository, times(1)).findById(testDto.getId());
        verify(testRepository, times(1)).save(testEntity);
    }

    @org.junit.jupiter.api.Test
    public void updateTestWithOptionsWithoutTemplateIndex() {
        TestDto testDto = dataProvider.getValidTestDtoWithOptions();
        testDto.getQuestions().get(0).setQuestionTemplateIndex(null);

        assertThrows(IllegalArgumentException.class, () -> testService.updateTest(testDto.getId(), testDto));
    }

    @org.junit.jupiter.api.Test
    public void updateTestWithOptionsWithInvalidTemplateIndex() {
        TestDto testDto = dataProvider.getValidTestDtoWithOptions();
        testDto.getQuestions().get(0).setQuestionTemplateIndex(10);

        assertThrows(IllegalArgumentException.class, () -> testService.updateTest(testDto.getId(), testDto));
    }

    @org.junit.jupiter.api.Test
    public void updateTestWithOptionsWithEmptyTemplate() {
        TestDto testDto = dataProvider.getValidTestDtoWithOptions();
        testDto.getQuestions().forEach(quest -> quest.setQuestionTemplateIndex(0));

        assertThrows(IllegalArgumentException.class, () -> testService.updateTest(testDto.getId(), testDto));
    }

    @org.junit.jupiter.api.Test
    public void deleteExistingTest() {
        Test testEntity = new Test();
        testEntity.setId(10);
        when(testRepository.findById(testEntity.getId())).thenReturn(Optional.of(testEntity));
        testService.deleteTest(testEntity.getId());

        verify(testRepository, times(1)).findById(testEntity.getId());
        verify(testRepository, times(1)).delete(testEntity);
    }

    @org.junit.jupiter.api.Test
    public void deleteMissingTest() {
        int testId = 1;
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> testService.deleteTest(testId));
        verify(testRepository, times(1)).findById(testId);
    }
}
