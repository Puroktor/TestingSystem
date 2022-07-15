package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.dto.JwtTokensDto;
import com.dsr.practice.testingsystem.dto.RegistrationResponseDto;
import com.dsr.practice.testingsystem.dto.UserLoginDto;
import com.dsr.practice.testingsystem.dto.UserRegistrationDto;
import com.dsr.practice.testingsystem.entity.Role;
import com.dsr.practice.testingsystem.entity.User;
import com.dsr.practice.testingsystem.repository.UserRepository;
import com.dsr.practice.testingsystem.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ModelMapper modelMapper;

    public RegistrationResponseDto createUser(UserRegistrationDto userDto) {
        if (userRepository.findByNickname(userDto.getNickname()).isPresent()) {
            throw new IllegalArgumentException("User with such nickname already exists");
        } else if (userDto.getRole() == Role.STUDENT && (userDto.getGroupNumber() == null || userDto.getYear() == null)) {
            throw new IllegalArgumentException("Student must have university year and group number!");
        } else if (userDto.getRole() == Role.TEACHER && (userDto.getGroupNumber() != null || userDto.getYear() != null)) {
            throw new IllegalArgumentException("Student must not have university year or group number!");
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return modelMapper.map(user, RegistrationResponseDto.class);
    }

    public JwtTokensDto loginUser(UserLoginDto userDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userDto.getNickname(), userDto.getPassword()));
        User dbUser = userRepository.findByNickname(userDto.getNickname())
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
