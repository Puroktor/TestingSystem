package com.dsr.practice.testingsystem.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum Role {
    TEACHER(new HashSet<>(Arrays.asList(Authorities.USER_SUBMIT, Authorities.USER_EDIT))),
    STUDENT(new HashSet<>(Arrays.asList(Authorities.USER_SUBMIT)));

    private final Set<Authorities> myAuthorities;

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return getMyAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.name()))
                .collect(Collectors.toSet());
    }
}
