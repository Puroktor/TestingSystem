package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.Role;
import com.dsr.practice.testingsystem.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByNickname(String name);

    List<User> findAllByRole(Role role);
}
