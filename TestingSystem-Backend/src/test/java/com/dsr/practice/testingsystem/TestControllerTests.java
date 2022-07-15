package com.dsr.practice.testingsystem;

import com.dsr.practice.testingsystem.controller.TestController;
import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.QuestionDto;
import com.dsr.practice.testingsystem.dto.TestDto;
import com.dsr.practice.testingsystem.dto.TestInfoDto;
import com.dsr.practice.testingsystem.entity.TestType;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TestControllerTests {
    private final TestController testController;
    @MockBean
    private TestService testService;

    @Autowired
    public TestControllerTests(TestController testController) {
        this.testController = testController;
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createValidTest() {
        TestDto testDto = getValidTestDto();
        when(testService.createTest(testDto)).thenReturn(testDto);
        ResponseEntity<TestDto> returned = testController.createTest(testDto);

        assertEquals(HttpStatus.CREATED, returned.getStatusCode());
        assertEquals(testDto, returned.getBody());
        verify(testService, times(1)).createTest(testDto);
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void createTestAsStudent() {
        TestDto testDto = getValidTestDto();
        assertThrows(AccessDeniedException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithBlankName() {
        TestDto testDto = getValidTestDto();
        testDto.setName("");
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithNullName() {
        TestDto testDto = getValidTestDto();
        testDto.setName(null);
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithTooBigName() {
        TestDto testDto = getValidTestDto();
        testDto.setName(new String(new char[51]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithoutQuestions() {
        TestDto testDto = getValidTestDto();
        testDto.setQuestions(null);
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWitBlankQuestionText() {
        TestDto testDto = getValidTestDto();
        testDto.getQuestions().get(0).setText("");
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWitNullQuestionText() {
        TestDto testDto = getValidTestDto();
        testDto.getQuestions().get(0).setText(null);
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWitTooBigQuestionText() {
        TestDto testDto = getValidTestDto();
        testDto.getQuestions().get(0).setText(new String(new char[501]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWitNegativeQuestionTemplateIndex() {
        TestDto testDto = getValidTestDto();
        testDto.getQuestions().get(0).setQuestionTemplateIndex(-1);
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithoutAnswers() {
        TestDto testDto = getValidTestDto();
        testDto.getQuestions().get(0).setAnswers(null);
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithBlankAnswerText() {
        TestDto testDto = getValidTestDto();
        testDto.getQuestions().get(0).getAnswers().get(0).setText("");
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithNullAnswerText() {
        TestDto testDto = getValidTestDto();
        testDto.getQuestions().get(0).getAnswers().get(0).setText(null);
        assertThrows(ValidationException.class, () -> testController.createTest(testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void createTestWithTooBigAnswerText() {
        TestDto testDto = getValidTestDto();
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
        assertEquals(page, returned.getBody());
        verify(testService, times(1)).fetchTestPage(filter, index, size);
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
        TestDto testDto = getValidTestDto();
        when(testService.getTest(testDto.getId())).thenReturn(testDto);
        ResponseEntity<TestDto> returned = testController.getTest(testDto.getId());

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(testDto, returned.getBody());
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
        TestDto testDto = getValidTestDto();
        when(testService.getShuffledTest(testDto.getId())).thenReturn(testDto);
        ResponseEntity<TestDto> returned = testController.getShuffledTest(testDto.getId());

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(testDto, returned.getBody());
        verify(testService, times(1)).getShuffledTest(testDto.getId());
    }

    @Test
    public void getShuffledTestAsUnauthorized() {
        assertThrows(AuthenticationException.class, () -> testController.getShuffledTest(1));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTest() {
        TestDto testDto = getValidTestDto();
        doNothing().when(testService).updateTest(testDto.getId(), testDto);
        ResponseEntity<Void> returned = testController.updateTest(testDto.getId(), testDto);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        verify(testService, times(1)).updateTest(testDto.getId(), testDto);
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithBlankLang() {
        TestDto testDto = getValidTestDto();
        testDto.setProgrammingLang("");
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithNullLang() {
        TestDto testDto = getValidTestDto();
        testDto.setProgrammingLang(null);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithTooBigLang() {
        TestDto testDto = getValidTestDto();
        testDto.setProgrammingLang(new String(new char[51]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithoutType() {
        TestDto testDto = getValidTestDto();
        testDto.setTestType(null);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithTooSmallQuestionCount() {
        TestDto testDto = getValidTestDto();
        testDto.setQuestionsCount(0);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithTooBigQuestionCount() {
        TestDto testDto = getValidTestDto();
        testDto.setQuestionsCount(100);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithNullScore() {
        TestDto testDto = getValidTestDto();
        testDto.getQuestions().get(0).setMaxScore(null);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithZeroScore() {
        TestDto testDto = getValidTestDto();
        testDto.getQuestions().get(0).setMaxScore(0);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void updateValidTestWithNullRightAnswer() {
        TestDto testDto = getValidTestDto();
        testDto.getQuestions().get(0).getAnswers().get(0).setIsRight(null);
        assertThrows(ValidationException.class, () -> testController.updateTest(testDto.getId(), testDto));
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void updateTestAsStudent() {
        assertThrows(AccessDeniedException.class, () -> testController.updateTest(1, getValidTestDto()));
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

    private TestDto getValidTestDto() {
        List<AnswerDto> answers = Arrays.asList(new AnswerDto(1, "First Answer", false),
                new AnswerDto(2, "Second Answer", true),
                new AnswerDto(3, "Third Answer", true),
                new AnswerDto(4, "Fourth Answer", false),
                new AnswerDto(5, "Fifth Answer", true));
        List<QuestionDto> questions = Arrays.asList(
                new QuestionDto(1, "First Question", 2, null, answers.subList(0, 2)),
                new QuestionDto(2, "Second question", 4, null, answers.subList(2, 5)));
        return new TestDto(1, "Java", "First test", 1, TestType.WITH_BANK, questions);
    }
}
