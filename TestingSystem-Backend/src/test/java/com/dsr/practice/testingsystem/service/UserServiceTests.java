package com.dsr.practice.testingsystem.service;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.dsr.practice.testingsystem.SampleDataProvider;
import com.dsr.practice.testingsystem.dto.JwtTokensDto;
import com.dsr.practice.testingsystem.dto.RegistrationResponseDto;
import com.dsr.practice.testingsystem.dto.UserLoginDto;
import com.dsr.practice.testingsystem.dto.UserRegistrationDto;
import com.dsr.practice.testingsystem.entity.Role;
import com.dsr.practice.testingsystem.entity.User;
import com.dsr.practice.testingsystem.repository.UserRepository;
import com.dsr.practice.testingsystem.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTests {
    @Autowired
    private SampleDataProvider dataProvider;
    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtTokenProvider tokenProvider;
    @MockBean
    private ModelMapper modelMapper;

    @Test
    public void createValidUser() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        User user = dataProvider.getUser();
        when(userRepository.findByNickname(dto.getNickname())).thenReturn(Optional.empty());
        when(modelMapper.map(dto, User.class)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, RegistrationResponseDto.class)).thenReturn(new RegistrationResponseDto());

        assertEquals(new RegistrationResponseDto(), userService.createUser(dto));
        verify(userRepository, times(1)).findByNickname(dto.getNickname());
        verify(modelMapper, times(1)).map(dto, User.class);
        verify(passwordEncoder, times(1)).encode(user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(modelMapper, times(1)).map(user, RegistrationResponseDto.class);
    }

    @Test
    public void createExistingUser() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        User user = dataProvider.getUser();
        when(userRepository.findByNickname(dto.getNickname())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
        verify(userRepository, times(1)).findByNickname(dto.getNickname());
    }

    @Test
    public void createStudentWithoutYear() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setYear(null);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    public void createStudentWithoutGroup() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setGroupNumber(null);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    public void createTeacherWithYear() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setRole(Role.TEACHER);
        dto.setGroupNumber(null);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    public void createTeacherWithGroup() {
        UserRegistrationDto dto = dataProvider.getValidRegistrationDto();
        dto.setRole(Role.TEACHER);
        dto.setYear(null);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    public void loginUser() {
        UserLoginDto dto = dataProvider.getValidLoginDto();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                dto.getNickname(), dto.getPassword());
        User user = dataProvider.getUser();
        JwtTokensDto tokensDto = dataProvider.getJwtTokensDto();
        when(authenticationManager.authenticate(authToken)).thenReturn(authToken);
        when(userRepository.findByNickname(dto.getNickname())).thenReturn(Optional.of(user));
        when(tokenProvider.generateAccessToken(user)).thenReturn(tokensDto.getAccessToken());
        when(tokenProvider.generateRefreshToken(user)).thenReturn(tokensDto.getRefreshToken());

        assertEquals(tokensDto, userService.loginUser(dto));
        verify(authenticationManager, times(1)).authenticate(authToken);
        verify(userRepository, times(1)).findByNickname(dto.getNickname());
        verify(tokenProvider, times(1)).generateAccessToken(user);
        verify(tokenProvider, times(1)).generateRefreshToken(user);
    }

    @Test
    public void loginUserWithWrongCredentials() {
        UserLoginDto dto = dataProvider.getValidLoginDto();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                dto.getNickname(), dto.getPassword());
        when(authenticationManager.authenticate(authToken)).thenThrow(UsernameNotFoundException.class);

        assertThrows(AuthenticationException.class, () -> userService.loginUser(dto));
        verify(authenticationManager, times(1)).authenticate(authToken);
    }

    @Test
    public void refreshValidToken() {
        String token = "token";
        User user = dataProvider.getUser();
        JwtTokensDto tokensDto = dataProvider.getJwtTokensDto();
        when(tokenProvider.getUsernameFromJwt(token)).thenReturn(user.getNickname());
        when(userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(tokenProvider.generateAccessToken(user)).thenReturn(tokensDto.getAccessToken());
        when(tokenProvider.generateRefreshToken(user)).thenReturn(tokensDto.getRefreshToken());

        assertEquals(tokensDto, userService.refreshToken(token));
        verify(tokenProvider, times(1)).getUsernameFromJwt(token);
        verify(userRepository, times(1)).findByNickname(user.getNickname());
        verify(tokenProvider, times(1)).generateAccessToken(user);
        verify(tokenProvider, times(1)).generateRefreshToken(user);
    }

    @Test
    public void refreshTokenOfMissingUser() {
        String token = "token";
        User user = dataProvider.getUser();
        when(tokenProvider.getUsernameFromJwt(token)).thenReturn(user.getNickname());
        when(userRepository.findByNickname(user.getNickname())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.refreshToken(token));
        verify(tokenProvider, times(1)).getUsernameFromJwt(token);
        verify(userRepository, times(1)).findByNickname(user.getNickname());
    }

    @Test
    public void refreshInvalidToken() {
        String token = "token";
        when(tokenProvider.getUsernameFromJwt(token)).thenThrow(JWTDecodeException.class);

        assertThrows(JWTDecodeException.class, () -> userService.refreshToken(token));
        verify(tokenProvider, times(1)).getUsernameFromJwt(token);
    }
}
