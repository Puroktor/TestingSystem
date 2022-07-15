package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.dto.JwtTokensDto;
import com.dsr.practice.testingsystem.dto.RegistrationResponseDto;
import com.dsr.practice.testingsystem.dto.UserLoginDto;
import com.dsr.practice.testingsystem.dto.UserRegistrationDto;
import com.dsr.practice.testingsystem.entity.Role;
import com.dsr.practice.testingsystem.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserControllerTests {
    @Autowired
    private UserController userController;
    @MockBean
    private UserService userService;

    @Test
    public void createValidUser() {
        UserRegistrationDto dto = getValidRegistrationDto();
        RegistrationResponseDto responseDto = new RegistrationResponseDto();
        when(userService.createUser(dto)).thenReturn(responseDto);
        ResponseEntity<RegistrationResponseDto> returned = userController.createUser(dto);

        assertEquals(HttpStatus.CREATED, returned.getStatusCode());
        assertEquals(responseDto, returned.getBody());
        verify(userService, times(1)).createUser(dto);
    }

    @Test
    public void createNullUser() {
        assertThrows(ValidationException.class, () -> userController.createUser(null));
    }

    @Test
    public void createUserWithNullName() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setName(null);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithBlankName() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setName("");
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooBigName() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setName(new String(new char[101]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithNullNickname() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setNickname(null);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithBlankNickname() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setNickname("");
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooBigNickname() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setNickname(new String(new char[51]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithNullPassword() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setPassword(null);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithBlankPassword() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setPassword("");
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooBigPassword() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setNickname(new String(new char[257]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithNullRole() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setRole(null);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithNullUniversity() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setUniversity(null);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithBlankUniversity() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setUniversity("");
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooSmallYear() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setYear(0);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooBigYear() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setYear(7);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooSmallGroup() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setGroupNumber(0);
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void createUserWithTooBigUniversity() {
        UserRegistrationDto dto = getValidRegistrationDto();
        dto.setUniversity(new String(new char[101]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> userController.createUser(dto));
    }

    @Test
    public void loginValidUser() {
        UserLoginDto dto = getValidLoginDto();
        JwtTokensDto tokensDto = new JwtTokensDto("token1", "token2");
        when(userService.loginUser(dto)).thenReturn(tokensDto);
        ResponseEntity<JwtTokensDto> returned = userController.loginUser(dto);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(tokensDto, returned.getBody());
        verify(userService, times(1)).loginUser(dto);
    }

    @Test
    public void loginNullUser() {
        assertThrows(ValidationException.class, () -> userController.createUser(null));
    }

    @Test
    public void loginUserWithNullNickname() {
        UserLoginDto dto = getValidLoginDto();
        dto.setNickname(null);
        assertThrows(ValidationException.class, () -> userController.loginUser(dto));
    }

    @Test
    public void loginUserWithBlankNickname() {
        UserLoginDto dto = getValidLoginDto();
        dto.setNickname("");
        assertThrows(ValidationException.class, () -> userController.loginUser(dto));
    }

    @Test
    public void loginUserWithTooBigNickname() {
        UserLoginDto dto = getValidLoginDto();
        dto.setNickname(new String(new char[51]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> userController.loginUser(dto));
    }

    @Test
    public void loginUserWithNullPassword() {
        UserLoginDto dto = getValidLoginDto();
        dto.setPassword(null);
        assertThrows(ValidationException.class, () -> userController.loginUser(dto));
    }

    @Test
    public void loginUserWithBlankPassword() {
        UserLoginDto dto = getValidLoginDto();
        dto.setPassword("");
        assertThrows(ValidationException.class, () -> userController.loginUser(dto));
    }

    @Test
    public void loginUserWithTooBigPassword() {
        UserLoginDto dto = getValidLoginDto();
        dto.setNickname(new String(new char[257]).replace('\0', 'a'));
        assertThrows(ValidationException.class, () -> userController.loginUser(dto));
    }

    @Test
    public void refreshValidToken() {
        String token = "token";
        JwtTokensDto tokensDto = new JwtTokensDto("token1", "token2");
        when(userService.refreshToken(token)).thenReturn(tokensDto);
        ResponseEntity<JwtTokensDto> returned = userController.refreshToken(token);

        assertEquals(HttpStatus.OK, returned.getStatusCode());
        assertEquals(tokensDto, returned.getBody());
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

    private UserRegistrationDto getValidRegistrationDto() {
        return new UserRegistrationDto("User User User", "User", "qwerty", Role.STUDENT,
                "The university", 2, 4, "user@user.com");
    }

    private UserLoginDto getValidLoginDto() {
        return new UserLoginDto("User", "qwerty");
    }
}
