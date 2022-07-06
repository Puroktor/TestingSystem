package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.Attempt;
import com.dsr.practice.testingsystem.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AttemptRepository extends CrudRepository<Attempt, Integer> {
    List<Attempt> findAllByUser(User user);
}
