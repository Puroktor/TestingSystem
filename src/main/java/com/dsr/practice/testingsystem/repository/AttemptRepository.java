package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.Attempt;
import org.springframework.data.repository.CrudRepository;

public interface AttemptRepository extends CrudRepository<Attempt, Integer> {
}
