package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.dto.JwtTokensDto;
import com.dsr.practice.testingsystem.entity.Role;
import com.dsr.practice.testingsystem.entity.User;
import com.dsr.practice.testingsystem.repository.UserRepository;
import com.dsr.practice.testingsystem.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Validated
public class UserAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public User createUser(@Valid User user) {
        if (userRepository.findByNickname(user.getNickname()).isPresent()) {
            throw new IllegalArgumentException("User with such nickname already exists");
        } else if (user.getRole() == Role.STUDENT && (user.getGroupNumber() == null || user.getYear() == null)) {
            throw new IllegalArgumentException("Student must have university year and group number!");
        } else if (user.getRole() == Role.TEACHER && (user.getGroupNumber() != null || user.getYear() != null)) {
            throw new IllegalArgumentException("Student must not have university year or group number!");
        } else if (!user.getEmail().matches("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
            throw new ValidationException("Not valid email");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public JwtTokensDto loginUser(User user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getNickname(), user.getPassword()));
        User dbUser = userRepository.findByNickname(user.getNickname())
                .orElseThrow(() -> new NoSuchElementException("User doesn't exist"));
        return new JwtTokensDto(tokenProvider.generateAccessToken(dbUser), tokenProvider.generateRefreshToken(dbUser));
    }

    public JwtTokensDto refreshToken(String refreshToken) {
        String nickname = tokenProvider.getUsernameFromJwt(refreshToken);
        User dbUser = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("User doesn't exist"));
        return new JwtTokensDto(tokenProvider.generateAccessToken(dbUser), tokenProvider.generateRefreshToken(dbUser));
    }
}
