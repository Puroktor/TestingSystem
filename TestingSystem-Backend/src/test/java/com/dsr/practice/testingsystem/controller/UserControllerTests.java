package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.SampleDataProvider;
import com.dsr.practice.testingsystem.dto.JwtTokensDto;
import com.dsr.practice.testingsystem.dto.UserDto;
import com.dsr.practice.testingsystem.dto.UserLoginDto;
import com.dsr.practice.testingsystem.dto.UserRegistrationDto;
import com.dsr.practice.testingsystem.service.UserService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTests {
    @Autowired
    private SampleDataProvider dataProvider;
    @Autowired
    private UserController userController;
    @MockBean
    private UserService userService;

    @Test
    public void createValidUser() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        when(userService.createUser(dto)).thenReturn(dataProvider.getUserDto());
        ResponseEntity<UserDto> returned = userController.createUser(dto);

        assertEquals(HttpStatus.CREATED, returned.getStatusCode());
        assertEquals(dataProvider.getUserDto(), returned.getBody());
        verify(userService, times(1)).createUser(dto);
    }

    @Test
    public void createNullUser() {
        assertThrows(ValidationException.class, () -> userController.createUser(null));
    }

    @Test
    public void createUserWithNullName() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setName(null);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithBlankName() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setName("");
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooBigName() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setName(dataProvider.getString(101));
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithNullNickname() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setNickname(null);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithBlankNickname() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setNickname("");
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooBigNickname() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setNickname(dataProvider.getString(51));
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithNullPassword() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setPassword(null);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithBlankPassword() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setPassword("");
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooBigPassword() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setNickname(dataProvider.getString(257));
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithNullRole() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setRole(null);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithNullUniversity() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setUniversity(null);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithBlankUniversity() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setUniversity("");
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooSmallYear() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setYear(0);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooBigYear() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setYear(7);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooSmallGroup() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setGroupNumber(0);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooBigUniversity() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setUniversity(dataProvider.getString(101));
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void loginValidUser() {
        UserLoginDto dto = dataProvider.getValidLoginDto();
        when(userService.loginUser(dto)).thenReturn(dataProvider.getJwtTokensDto());
        ResponseEntity<JwtTokensDto> returned = userController.loginUser(dto);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(dataProvider.getJwtTokensDto(), returned.getBody());
        verify(userService, times(1)).loginUser(dto);
    }

    @Test
    public void loginNullUser() {
        assertThrows(ValidationException.class, () -> userController.createUser(null));
    }

    @Test
    public void loginUserWithNullNickname() {
        UserLoginDto dto = dataProvider.getValidLoginDto();
        dto.setNickname(null);
        assertThrows(ValidationException.class, () -> userController.loginUser(dto));
    }

    @Test
    public void loginUserWithBlankNickname() {
        UserLoginDto dto = dataProvider.getValidLoginDto();
        dto.setNickname("");
        assertThrows(ValidationException.class, () -> userController.loginUser(dto));
    }

    @Test
    public void loginUserWithTooBigNickname() {
        UserLoginDto dto = dataProvider.getValidLoginDto();
        dto.setNickname(dataProvider.getString(51));
        assertThrows(ValidationException.class, () -> userController.loginUser(dto));
    }

    @Test
    public void loginUserWithNullPassword() {
        UserLoginDto dto = dataProvider.getValidLoginDto();
        dto.setPassword(null);
        assertThrows(ValidationException.class, () -> userController.loginUser(dto));
    }

    @Test
    public void loginUserWithBlankPassword() {
        UserLoginDto dto = dataProvider.getValidLoginDto();
        dto.setPassword("");
        assertThrows(ValidationException.class, () -> userController.loginUser(dto));
    }

    @Test
    public void loginUserWithTooBigPassword() {
        UserLoginDto dto = dataProvider.getValidLoginDto();
        dto.setNickname(dataProvider.getString(257));
        assertThrows(ValidationException.class, () -> userController.loginUser(dto));
    }

    @Test
    public void refreshValidToken() {
        String token = "token";
        when(userService.refreshToken(token)).thenReturn(dataProvider.getJwtTokensDto());
        ResponseEntity<JwtTokensDto> returned = userController.refreshToken(token);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(dataProvider.getJwtTokensDto(), returned.getBody());
        verify(userService, times(1)).refreshToken(token);
    }

    @Test
    public void refreshNullToken() {
        assertThrows(ValidationException.class, () -> userController.refreshToken(null));
    }

    @Test
    public void refreshBlankToken() {
        assertThrows(ValidationException.class, () -> userController.refreshToken(""));
    }

    @Test
    @WithMockUser(authorities = "USER_EDIT")
    public void getUser() {
        int userId = 1;
        when(userService.getUser(userId)).thenReturn(dataProvider.getUserDto());
        ResponseEntity<UserDto> returned = userController.getUser(userId);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(dataProvider.getUserDto(), returned.getBody());
        verify(userService, times(1)).getUser(userId);
    }

    @Test
    @WithMockUser(authorities = "USER_SUBMIT")
    public void getUserAsStudent() {
        assertThrows(AccessDeniedException.class, () -> userController.getUser(1));
    }

    @Test
    public void getUserAsUnauthorized() {
        assertThrows(AuthenticationException.class, () -> userController.getUser(1));
    }
}
