package com.dsr.practice.testingsystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            String token = ((HttpServletRequest) servletRequest).getHeader(HttpHeaders.AUTHORIZATION);
            if (token != null) {
                SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthTokenFromJwt(token));
            }
        } catch (Exception e) {
            throw new AuthenticationException("Invalid JWT token!");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

