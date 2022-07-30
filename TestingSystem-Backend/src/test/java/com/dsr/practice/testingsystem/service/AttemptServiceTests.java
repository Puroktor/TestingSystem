package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.SampleDataProvider;
import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.AttemptDto;
import com.dsr.practice.testingsystem.dto.LeaderboardPageDto;
import com.dsr.practice.testingsystem.dto.TestDto;
import com.dsr.practice.testingsystem.entity.*;
import com.dsr.practice.testingsystem.mapper.AttemptMapper;
import com.dsr.practice.testingsystem.mapper.TestMapper;
import com.dsr.practice.testingsystem.repository.AnswerRepository;
import com.dsr.practice.testingsystem.repository.AttemptRepository;
import com.dsr.practice.testingsystem.repository.TestRepository;
import com.dsr.practice.testingsystem.repository.UserRepository;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
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
    @MockBean
    private AttemptMapper attemptMapper;

    @org.junit.jupiter.api.Test
    public void submitAttemptOfMissingUser() {
        List<AnswerDto> answers = dataProvider.getValidTestDtoWithBank().getQuestions().get(0).getAnswers();
        String nickname = "nickname";
        when(userRepository.findByNickname(nickname)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> attemptService.submitAttempt(answers, nickname));
        verify(userRepository, times(1)).findByNickname(nickname);
    }

    @org.junit.jupiter.api.Test
    public void submitAttemptWithMissingAnswer() {
        List<AnswerDto> answers = dataProvider.getValidTestDtoWithBank().getQuestions().get(0).getAnswers();
        User user = dataProvider.getUser();
        when(userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(answerRepository.findById(answers.get(0).getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> attemptService.submitAttempt(answers, user.getNickname()));
        verify(userRepository, times(1)).findByNickname(user.getNickname());
        verify(answerRepository, times(1)).findById(answers.get(0).getId());
    }

    @org.junit.jupiter.api.Test
    public void submitAttempt() {
        List<AnswerDto> answerDtoList = dataProvider.getValidTestDtoWithBank().getQuestions().get(0).getAnswers();
        List<Answer> answerList = dataProvider.getValidTestWithBank().getQuestions().get(0).getAnswers();
        User user = dataProvider.getUser();
        when(userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(answerRepository.findById(anyInt()))
                .thenAnswer(i -> Optional.of(answerList.get((Integer) (i.getArguments()[0]) - 1)));
        when(attemptRepository.save(any(Attempt.class))).thenAnswer(i -> i.getArguments()[0]);
        when(attemptMapper.toResultDto(any(Attempt.class))).thenReturn(dataProvider.getAttemptResult());

        Map<Answer, Boolean> modelMap = new HashMap<>();
        modelMap.put(answerList.get(0), false);
        modelMap.put(answerList.get(1), true);
        Attempt modelAttempt = new Attempt(null, user, answerList.get(0).getQuestion().getTest(), 100D,
                LocalDateTime.MIN, modelMap);

        assertEquals(dataProvider.getAttemptResult(), attemptService.submitAttempt(answerDtoList, user.getNickname()));
        verify(userRepository, times(1)).findByNickname(user.getNickname());
        verify(answerRepository, times(1)).findById(answerDtoList.get(0).getId());
        verify(attemptRepository, times(1)).save(argThat(
                attempt -> new ReflectionEquals(modelAttempt, "dateTime").matches(attempt)));
        verify(attemptMapper, times(1)).toResultDto(argThat(
                attempt -> new ReflectionEquals(modelAttempt, "dateTime").matches(attempt)));
    }

    @org.junit.jupiter.api.Test
    public void getMissingAttempt() {
        int attemptId = 1;
        when(attemptRepository.findById(attemptId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> attemptService.getAttempt(attemptId));
        verify(attemptRepository, times(1)).findById(attemptId);
    }

    @org.junit.jupiter.api.Test
    public void getAttempt() {
        int attemptId = 1;
        User user = dataProvider.getUser();
        Test test = dataProvider.getValidTestWithBank();
        Map<Answer, Boolean> submittedAnswers = new HashMap<>();
        submittedAnswers.put(test.getQuestions().get(0).getAnswers().get(0), true);
        submittedAnswers.put(test.getQuestions().get(0).getAnswers().get(1), false);
        Attempt attempt = new Attempt(1, user, test, 0D, LocalDateTime.MIN, submittedAnswers);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(testMapper.toDto(test)).thenReturn(dataProvider.getValidTestDtoWithBank());
        AttemptDto attemptDto = attemptService.getAttempt(attemptId);

        TestDto modelTestDto = dataProvider.getValidTestDtoWithBank();
        modelTestDto.getQuestions().remove(1);
        Map<Integer, Boolean> modelMap = new HashMap<>();
        modelMap.put(1, true);
        modelMap.put(2, false);

        assertEquals(user.getId(), attemptDto.getUserId());
        assertEquals(user.getNickname(), attemptDto.getNickname());
        assertEquals(attempt.getScore(), attemptDto.getScore());
        assertEquals(attempt.getDateTime(), attemptDto.getDateTime());
        assertEquals(modelTestDto, attemptDto.getTest());
        assertEquals(modelMap, attemptDto.getAnswerToSubmittedValueMap());
        verify(attemptRepository, times(1)).findById(attemptId);
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
        Attempt attempt = new Attempt(1, user, test, score, LocalDateTime.MIN, submittedAnswers);
        user.getAttempts().add(attempt);
        List<User> users = Arrays.asList(user, otherUser);
        Pageable pageable = PageRequest.of(index, size);

        when(userRepository.findPageOfTopByRole(Role.STUDENT, pageable)).
                thenReturn(new PageImpl<>(users));
        when(testRepository.findAll()).thenReturn(Collections.singletonList(test));
        when(attemptRepository.findTopByUserAndTestOrderByDateTimeDesc(user, test)).thenReturn(Optional.of(attempt));
        when(attemptRepository.findTopByUserAndTestOrderByDateTimeDesc(otherUser, test)).thenReturn(Optional.empty());

        LeaderboardPageDto pageDto = attemptService.getLeaderboardPage(index, size);
        Map<Integer, Double> modelMap = new HashMap<>();
        modelMap.put(test.getId(), score);

        assertEquals(1, pageDto.getTestRecords().size());
        assertEquals(new LeaderboardPageDto.TestRecord(test.getId(), test.getName(), test.getPassingScore()),
                pageDto.getTestRecords().get(0));
        assertEquals(2, pageDto.getUserRecords().getContent().size());
        assertEquals(new LeaderboardPageDto.UserRecord(user.getId(), user.getNickname(), score, modelMap),
                pageDto.getUserRecords().getContent().get(0));
        assertEquals(new LeaderboardPageDto.UserRecord(otherUser.getId(), otherUser.getNickname(), 0d, new HashMap<>()),
                pageDto.getUserRecords().getContent().get(1));
        verify(userRepository, times(1)).findPageOfTopByRole(Role.STUDENT, pageable);
        verify(testRepository, times(1)).findAll();
        verify(attemptRepository, times(1)).findTopByUserAndTestOrderByDateTimeDesc(user, test);
        verify(attemptRepository, times(1)).findTopByUserAndTestOrderByDateTimeDesc(otherUser, test);
    }

    @org.junit.jupiter.api.Test
    public void getAttemptsResultsOfMissingUser() {
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> attemptService.getAttemptsResults(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @org.junit.jupiter.api.Test
    public void getEmptyAttemptsResults() {
        User user = dataProvider.getUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(attemptRepository.findAllByUserOrderByDateTimeDesc(user)).thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), attemptService.getAttemptsResults(user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verify(attemptRepository, times(1)).findAllByUserOrderByDateTimeDesc(dataProvider.getUser());
    }

    @org.junit.jupiter.api.Test
    public void getAttemptsResults() {
        User user = dataProvider.getUser();
        Attempt attempt = new Attempt();
        attempt.setId(1);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(attemptRepository.findAllByUserOrderByDateTimeDesc(user)).thenReturn(
                Collections.singletonList(attempt));
        when(attemptMapper.toResultDto(attempt)).thenReturn(dataProvider.getAttemptResult());

        assertEquals(Collections.singletonList(dataProvider.getAttemptResult()),
                attemptService.getAttemptsResults(user.getId()));
        verify(userRepository, times(1)).findById(user.getId());
        verify(attemptRepository, times(1)).findAllByUserOrderByDateTimeDesc(dataProvider.getUser());
        verify(attemptMapper, times(1)).toResultDto(attempt);
    }
}
