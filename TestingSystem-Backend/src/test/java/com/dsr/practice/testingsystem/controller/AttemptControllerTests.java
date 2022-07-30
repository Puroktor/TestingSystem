package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.SampleDataProvider;
import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.AttemptDto;
import com.dsr.practice.testingsystem.dto.AttemptResultDto;
import com.dsr.practice.testingsystem.dto.LeaderboardPageDto;
import com.dsr.practice.testingsystem.service.AttemptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class AttemptControllerTests {
    @Autowired
    private SampleDataProvider dataProvider;
    @Autowired
    private AttemptController attemptController;
    @MockBean
    private AttemptService attemptService;

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void submitAttempt() {
        AnswerDto dto = dataProvider.getValidAnswerDto();
        List<AnswerDto> list = Collections.singletonList(dto);
        when(attemptService.submitAttempt(list, "user")).thenReturn(dataProvider.getAttemptResult());
        ResponseEntity<AttemptResultDto> returned = attemptController.submitAttempt(list);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(dataProvider.getAttemptResult(), returned.getBody());
        verify(attemptService, times(1)).submitAttempt(list, "user");
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void submitAttemptWithInvalidAnswer() {
        AnswerDto dto = dataProvider.getValidAnswerDto();
        dto.setIsRight(null);
        assertThrows(ValidationException.class,
                () -> attemptController.submitAttempt(Collections.singletonList(dto)));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void submitAttemptWithNullAnswerList() {
        assertThrows(ValidationException.class, () -> attemptController.submitAttempt(null));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void submitAttemptWithEmptyAnswerList() {
        assertThrows(ValidationException.class, () -> attemptController.submitAttempt(new ArrayList<>()));
    }

    @Test
    public void submitAttemptAsUnauthorized() {
        assertThrows(AuthenticationException.class, () -> attemptController.submitAttempt(new ArrayList<>()));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void getAttempt() {
        AttemptDto attemptDto = new AttemptDto();
        int attemptId = 1;
        when(attemptService.getAttempt(attemptId)).thenReturn(attemptDto);
        ResponseEntity<AttemptDto> returned = attemptController.getAttempt(attemptId);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(new AttemptDto(), returned.getBody());
        verify(attemptService, times(1)).getAttempt(attemptId);
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void getAttemptAsStudent() {
        assertThrows(AccessDeniedException.class, () -> attemptController.getAttempt(1));
    }

    @Test
    public void getAttemptAsUnauthorized() {
        assertThrows(AuthenticationException.class, () -> attemptController.getAttempt(1));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void getLeaderboardPage() {
        LeaderboardPageDto dto = new LeaderboardPageDto();
        int index = 0, size = 1;
        when(attemptService.getLeaderboardPage(index, size)).thenReturn(dto);
        ResponseEntity<LeaderboardPageDto> returned = attemptController.getLeaderboardPage(index, size);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(new LeaderboardPageDto(), returned.getBody());
        verify(attemptService, times(1)).getLeaderboardPage(index, size);
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void getLeaderboardPageWithNegativeIndex() {
        assertThrows(ValidationException.class, () -> attemptController.getLeaderboardPage(-1, 1));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void getLeaderboardPageWithZeroSize() {
        assertThrows(ValidationException.class, () -> attemptController.getLeaderboardPage(1, 0));
    }

    @Test
    public void getLeaderboardPageAsUnauthorized() {
        assertThrows(AuthenticationException.class, () -> attemptController.getLeaderboardPage(1, 1));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void getAttemptsResults() {
        int attemptId = 1;
        when(attemptService.getAttemptsResults(attemptId)).thenReturn(
                Collections.singletonList(dataProvider.getAttemptResult()));
        ResponseEntity<List<AttemptResultDto>> returned = attemptController.getAttemptsResults(attemptId);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(Collections.singletonList(dataProvider.getAttemptResult()), returned.getBody());
        verify(attemptService, times(1)).getAttemptsResults(attemptId);
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void getAttemptsResultsAsStudent() {
        assertThrows(AccessDeniedException.class, () -> attemptController.getAttemptsResults(1));
    }

    @Test
    public void getAttemptsResultsAsUnauthorized() {
        assertThrows(AuthenticationException.class, () -> attemptController.getAttemptsResults(1));
    }
}
