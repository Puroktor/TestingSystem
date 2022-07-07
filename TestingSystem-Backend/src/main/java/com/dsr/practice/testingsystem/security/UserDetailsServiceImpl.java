package com.dsr.practice.testingsystem.security;

import com.dsr.practice.testingsystem.entity.User;
import com.dsr.practice.testingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("User was not found"));
        Collection<SimpleGrantedAuthority> authorities = user.getRole().getAuthorities();
        return new org.springframework.security.core.userdetails.User(user.getNickname(), user.getPassword(), authorities);
    }
}
