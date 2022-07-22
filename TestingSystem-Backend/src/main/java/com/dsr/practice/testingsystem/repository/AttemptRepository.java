package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.Attempt;
import com.dsr.practice.testingsystem.entity.Test;
import com.dsr.practice.testingsystem.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends CrudRepository<Attempt, Integer> {
    List<Attempt> findAllByUser(User user);

    @Query("SELECT a FROM Attempt a WHERE a.user = ?1 AND a.test = ?2 AND a.dateTime = " +
            "(SELECT MAX(dateTime) FROM Attempt WHERE user = ?1 AND test = ?2)")
    Optional<Attempt> findLatestByUserAndTest(User user, Test test);
}
