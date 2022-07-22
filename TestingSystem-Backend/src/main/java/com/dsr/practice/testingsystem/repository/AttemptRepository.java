package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.Attempt;
import com.dsr.practice.testingsystem.entity.Test;
import com.dsr.practice.testingsystem.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends CrudRepository<Attempt, Integer> {
    List<Attempt> findAllByUserOrderByDateTimeDesc(User user);

    Optional<Attempt> findTopByUserAndTestOrderByDateTimeDesc(User user, Test test);
}
