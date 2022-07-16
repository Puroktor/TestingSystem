package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.SampleDataProvider;
import com.dsr.practice.testingsystem.dto.TestDto;
import com.dsr.practice.testingsystem.dto.TestInfoDto;
import com.dsr.practice.testingsystem.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;

import javax.validation.ValidationException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TestControllerTests {
    @Autowired
    private SampleDataProvider dataProvider;
    @Autowired
    private TestController testController;
    @MockBean
    private TestService testService;

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createValidTest() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        when(testService.createTest(testDto)).thenReturn(testDto);
        ResponseEntity<TestDto> returned = testController.createTest(testDto);

        assertEquals(HttpStatus.CREATED, returned.getStatusCode());
        assertEquals(dataProvider.getValidTestDtoWithBank(), returned.getBody());
        verify(testService, times(1)).createTest(testDto);
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void createTestAsStudent() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        assertThrows(AccessDeniedException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithBlankName() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.setName("");
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithNullName() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.setName(null);
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithTooBigName() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.setName(new String(new char[51]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithoutQuestions() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.setQuestions(null);
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWitBlankQuestionText() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).setText("");
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWitNullQuestionText() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).setText(null);
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWitTooBigQuestionText() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).setText(new String(new char[501]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWitNegativeQuestionTemplateIndex() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).setQuestionTemplateIndex(-1);
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithoutAnswers() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).setAnswers(null);
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithBlankAnswerText() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).getAnswers().get(0).setText("");
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithNullAnswerText() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).getAnswers().get(0).setText(null);
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithTooBigAnswerText() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).getAnswers().get(0).setText(new String(new char[201]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void fetchValidTestPage() {
        Page<TestInfoDto> page = new PageImpl<>(Collections.emptyList());
        String filter = "filter";
        int index = 0, size = 1;
        when(testService.fetchTestPage(filter, index, size)).thenReturn(page);
        ResponseEntity<Page<TestInfoDto>> returned = testController.fetchTestPage(filter, index, size);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(new PageImpl<>(Collections.emptyList()), returned.getBody());
        verify(testService, times(1)).fetchTestPage(filter, index, size);
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void fetchTestPageWithTooBigFilter() {
        String filter = new String(new char[51]).replace('\0', 'a');
        int index = -1, size = 1;
        assertThrows(ValidationException.class, () -> testController.fetchTestPage(filter, index, size));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void fetchTestPageWithNegativeIndex() {
        String filter = "filter";
        int index = -1, size = 1;
        assertThrows(ValidationException.class, () -> testController.fetchTestPage(filter, index, size));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void fetchTestPageWithZeroSize() {
        String filter = "filter";
        int index = 1, size = 0;
        assertThrows(ValidationException.class, () -> testController.fetchTestPage(filter, index, size));
    }

    @Test
    public void fetchTestPageAsUnauthorized() {
        String filter = "filter";
        int index = 0, size = 1;
        assertThrows(AuthenticationException.class, () -> testController.fetchTestPage(filter, index, size));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void getTest() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        when(testService.getTest(testDto.getId())).thenReturn(testDto);
        ResponseEntity<TestDto> returned = testController.getTest(testDto.getId());

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(dataProvider.getValidTestDtoWithBank(), returned.getBody());
        verify(testService, times(1)).getTest(testDto.getId());
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void getTestAsStudent() {
        assertThrows(AccessDeniedException.class, () -> testController.getTest(1));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void getShuffledTest() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        when(testService.getShuffledTest(testDto.getId())).thenReturn(testDto);
        ResponseEntity<TestDto> returned = testController.getShuffledTest(testDto.getId());

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(dataProvider.getValidTestDtoWithBank(), returned.getBody());
        verify(testService, times(1)).getShuffledTest(testDto.getId());
    }

    @Test
    public void getShuffledTestAsUnauthorized() {
        assertThrows(AuthenticationException.class, () -> testController.getShuffledTest(1));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTest() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        doNothing().when(testService).updateTest(testDto.getId(), testDto);
        ResponseEntity<Void> returned = testController.updateTest(testDto.getId(), testDto);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        verify(testService, times(1)).updateTest(testDto.getId(), testDto);
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithBlankLang() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.setProgrammingLang("");
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithNullLang() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.setProgrammingLang(null);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithTooBigLang() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.setProgrammingLang(new String(new char[51]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithoutType() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.setTestType(null);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithTooSmallQuestionCount() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.setQuestionsCount(0);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithTooBigQuestionCount() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.setQuestionsCount(100);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithNullScore() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).setMaxScore(null);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithZeroScore() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).setMaxScore(0);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithNullRightAnswer() {
        TestDto testDto = dataProvider.getValidTestDtoWithBank();
        testDto.getQuestions().get(0).getAnswers().get(0).setIsRight(null);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void updateTestAsStudent() {
        assertThrows(AccessDeniedException.class, () -> testController.updateTest(1, dataProvider.getValidTestDtoWithBank()));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void deleteTest() {
        int testId = 1;
        doNothing().when(testService).deleteTest(testId);
        ResponseEntity<Void> returned = testController.deleteTest(testId);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        verify(testService, times(1)).deleteTest(testId);
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void deleteTestAsStudent() {
        assertThrows(AccessDeniedException.class, () -> testController.deleteTest(1));
    }
}
