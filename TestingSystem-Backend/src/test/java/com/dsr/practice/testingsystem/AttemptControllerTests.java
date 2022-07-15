package com.dsr.practice.testingsystem;

import com.dsr.practice.testingsystem.controller.AttemptController;
import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.AttemptDto;
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

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AttemptControllerTests {
    private final AttemptController attemptController;
    @MockBean
    private AttemptService attemptService;

    @Autowired
    public AttemptControllerTests(AttemptController attemptController) {
        this.attemptController = attemptController;
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void submitAttempt() {
        int userId = 1;
        AnswerDto dto = new AnswerDto(1, "Answer", true);
        List<AnswerDto> list = Collections.singletonList(dto);
        doNothing().when(attemptService).submitAttempt(list, userId);
        ResponseEntity<Void> returned = attemptController.submitAttempt(list, userId);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        verify(attemptService, times(1)).submitAttempt(list, userId);
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void submitAttemptWithInvalidAnswer() {
        AnswerDto dto = new AnswerDto(1, "Answer", null);
        assertThrows(ValidationException.class,
                () -> attemptController.submitAttempt(Collections.singletonList(dto), 1));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void submitAttemptWithNullAnswerList() {
        assertThrows(ValidationException.class, () -> attemptController.submitAttempt(null, 1));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void submitAttemptWithEmptyAnswerList() {
        assertThrows(ValidationException.class, () -> attemptController.submitAttempt(new ArrayList<>(), 1));
    }

    @Test
    public void submitAttemptAsUnauthorized() {
        assertThrows(AuthenticationException.class, () -> attemptController.submitAttempt(new ArrayList<>(), 1));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void getAttempt() {
        AttemptDto attemptDto = new AttemptDto();
        int userId = 1, testId = 1;
        when(attemptService.getAttempt(userId, testId)).thenReturn(attemptDto);
        ResponseEntity<AttemptDto> returned = attemptController.getAttempt(userId, testId);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(attemptDto, returned.getBody());
        verify(attemptService, times(1)).getAttempt(userId, testId);
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void getAttemptAsStudent() {
        assertThrows(AccessDeniedException.class, () -> attemptController.getAttempt(1, 1));
    }

    @Test
    public void getAttemptAsUnauthorized() {
        assertThrows(AuthenticationException.class, () -> attemptController.getAttempt(1, 1));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void getLeaderboardPage() {
        LeaderboardPageDto dto = new LeaderboardPageDto();
        int index = 0, size = 1;
        when(attemptService.getLeaderboardPage(index, size)).thenReturn(dto);
        ResponseEntity<LeaderboardPageDto> returned = attemptController.getLeaderboardPage(index, size);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(dto, returned.getBody());
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
}
