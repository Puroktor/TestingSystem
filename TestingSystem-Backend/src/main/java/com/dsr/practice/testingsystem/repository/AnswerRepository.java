package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.Answer;
import org.springframework.data.repository.CrudRepository;

public interface AnswerRepository extends CrudRepository<Answer, Integer> {
}
