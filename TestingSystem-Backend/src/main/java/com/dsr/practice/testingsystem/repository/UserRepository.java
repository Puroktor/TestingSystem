package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.Role;
import com.dsr.practice.testingsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByNickname(String name);

    @Query(value = "SELECT u FROM User u LEFT JOIN Attempt a on u.id = a.user.id " +
            "WHERE u.role = ?1 AND (a.dateTime IS NULL OR a.dateTime IN" +
            "(SELECT MAX(dateTime) FROM Attempt WHERE user.id = u.id GROUP BY test))" +
            "GROUP BY u.id ORDER BY sum(a.score) DESC NULLS LAST, u.nickname")
    Page<User> findPageOfTopByRole(Role role, Pageable pageable);
}
