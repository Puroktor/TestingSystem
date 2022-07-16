package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.SampleDataProvider;
import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.AttemptDto;
import com.dsr.practice.testingsystem.dto.LeaderboardPageDto;
import com.dsr.practice.testingsystem.dto.TestDto;
import com.dsr.practice.testingsystem.entity.*;
import com.dsr.practice.testingsystem.mapper.TestMapper;
import com.dsr.practice.testingsystem.repository.AnswerRepository;
import com.dsr.practice.testingsystem.repository.AttemptRepository;
import com.dsr.practice.testingsystem.repository.TestRepository;
import com.dsr.practice.testingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AttemptServiceTests {
    @Autowired
    private SampleDataProvider dataProvider;
    @Autowired
    private AttemptService attemptService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AnswerRepository answerRepository;
    @MockBean
    private TestRepository testRepository;
    @MockBean
    private AttemptRepository attemptRepository;
    @MockBean
    private TestMapper testMapper;

    @org.junit.jupiter.api.Test
    public void submitAttemptOfMissingUser() {
        List<AnswerDto> answers = dataProvider.getValidTestDtoWithBank().getQuestions().get(0).getAnswers();
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> attemptService.submitAttempt(answers, userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @org.junit.jupiter.api.Test
    public void submitAttemptWithMissingAnswer() {
        List<AnswerDto> answers = dataProvider.getValidTestDtoWithBank().getQuestions().get(0).getAnswers();
        User user = dataProvider.getUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(answerRepository.findById(answers.get(0).getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> attemptService.submitAttempt(answers, user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verify(answerRepository, times(1)).findById(answers.get(0).getId());
    }

    @org.junit.jupiter.api.Test
    public void submitAttempt() {
        List<AnswerDto> answerDtoList = dataProvider.getValidTestDtoWithBank().getQuestions().get(0).getAnswers();
        List<Answer> answerList = dataProvider.getValidTestWithBank().getQuestions().get(0).getAnswers();
        User user = dataProvider.getUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(answerRepository.findById(anyInt()))
                .thenAnswer(i -> Optional.of(answerList.get((Integer) (i.getArguments()[0]) - 1)));
        when(attemptRepository.save(any(Attempt.class))).thenAnswer(i -> i.getArguments()[0]);
        attemptService.submitAttempt(answerDtoList, user.getId());

        Map<Answer, Boolean> modelMap = new HashMap<>();
        modelMap.put(answerList.get(0), false);
        modelMap.put(answerList.get(1), true);
        Attempt modelAttempt = new Attempt(user, answerList.get(0).getQuestion().getTest(), 100D, modelMap);

        verify(userRepository, times(1)).findById(user.getId());
        verify(answerRepository, times(1)).findById(answerDtoList.get(0).getId());
        verify(attemptRepository, times(1)).save(modelAttempt);
    }

    @org.junit.jupiter.api.Test
    public void getAttemptOfMissingUser() {
        int userId = 1, testId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> attemptService.getAttempt(userId, testId));
        verify(userRepository, times(1)).findById(userId);
    }

    @org.junit.jupiter.api.Test
    public void getAttemptOfMissingTest() {
        int userId = 1, testId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> attemptService.getAttempt(userId, testId));
        verify(userRepository, times(1)).findById(userId);
        verify(testRepository, times(1)).findById(testId);
    }

    @org.junit.jupiter.api.Test
    public void getMissingAttempt() {
        int userId = 1, testId = 1;
        User user = dataProvider.getUser();
        Test test = dataProvider.getValidTestWithBank();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(testRepository.findById(testId)).thenReturn(Optional.of(test));
        when(attemptRepository.findByUserAndTest(user, test)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> attemptService.getAttempt(userId, testId));
        verify(userRepository, times(1)).findById(userId);
        verify(testRepository, times(1)).findById(testId);
        verify(attemptRepository, times(1)).findByUserAndTest(user, test);
    }

    @org.junit.jupiter.api.Test
    public void getAttempt() {
        int userId = 1, testId = 1;
        User user = dataProvider.getUser();
        Test test = dataProvider.getValidTestWithBank();
        Map<Answer, Boolean> submittedAnswers = new HashMap<>();
        submittedAnswers.put(test.getQuestions().get(0).getAnswers().get(0), true);
        submittedAnswers.put(test.getQuestions().get(0).getAnswers().get(1), false);
        Attempt attempt = new Attempt(user, test, 0D, submittedAnswers);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(testRepository.findById(testId)).thenReturn(Optional.of(test));
        when(attemptRepository.findByUserAndTest(user, test)).thenReturn(Optional.of(attempt));
        when(testMapper.toDto(test)).thenReturn(dataProvider.getValidTestDtoWithBank());
        AttemptDto attemptDto = attemptService.getAttempt(userId, testId);

        TestDto modelTestDto = dataProvider.getValidTestDtoWithBank();
        modelTestDto.getQuestions().remove(1);
        Map<Integer, Boolean> modelMap = new HashMap<>();
        modelMap.put(1, true);
        modelMap.put(2, false);

        assertEquals(user.getNickname(), attemptDto.getNickname());
        assertEquals(modelTestDto, attemptDto.getTest());
        assertEquals(modelMap, attemptDto.getAnswerToSubmittedValueMap());
        verify(userRepository, times(1)).findById(userId);
        verify(testRepository, times(1)).findById(testId);
        verify(attemptRepository, times(1)).findByUserAndTest(user, test);
        verify(testMapper, times(1)).toDto(test);
    }


    @org.junit.jupiter.api.Test
    public void getLeaderboardPage() {
        int index = 0, size = 2;
        User user = dataProvider.getUser();
        User otherUser = dataProvider.getUser();
        otherUser.setId(3);
        otherUser.setNickname("Other");
        Test test = dataProvider.getValidTestWithBank();
        Map<Answer, Boolean> submittedAnswers = new HashMap<>();
        submittedAnswers.put(test.getQuestions().get(0).getAnswers().get(0), false);
        submittedAnswers.put(test.getQuestions().get(0).getAnswers().get(1), true);
        double score = 100d;
        Attempt attempt = new Attempt(user, test, score, submittedAnswers);
        user.getAttempts().add(attempt);
        List<User> users = Arrays.asList(user, otherUser);
        Pageable pageable = PageRequest.of(index, size);

        when(userRepository.findPageOfTopByRole(Role.STUDENT, pageable)).
                thenReturn(new PageImpl<>(users));
        when(testRepository.findAll()).thenReturn(Collections.singletonList(test));
        when(attemptRepository.findAllByUser(eq(user))).thenReturn(Collections.singletonList(attempt));
        when(attemptRepository.findAllByUser(eq(otherUser))).thenReturn(Collections.emptyList());

        LeaderboardPageDto pageDto = attemptService.getLeaderboardPage(index, size);
        Map<Integer, Double> modelMap = new HashMap<>();
        modelMap.put(test.getId(), score);

        assertEquals(1, pageDto.getTestRecords().size());
        assertEquals(new LeaderboardPageDto.TestRecord(test.getId(), test.getName()), pageDto.getTestRecords().get(0));
        assertEquals(2, pageDto.getUserRecords().getContent().size());
        assertEquals(new LeaderboardPageDto.UserRecord(user.getId(), user.getNickname(), score, modelMap),
                pageDto.getUserRecords().getContent().get(0));
        assertEquals(new LeaderboardPageDto.UserRecord(otherUser.getId(), otherUser.getNickname(), 0d, new HashMap<>()),
                pageDto.getUserRecords().getContent().get(1));
        verify(userRepository, times(1)).findPageOfTopByRole(Role.STUDENT, pageable);
        verify(testRepository, times(1)).findAll();
        verify(attemptRepository, times(1)).findAllByUser(eq(user));
        verify(attemptRepository, times(1)).findAllByUser(eq(otherUser));
    }
}
