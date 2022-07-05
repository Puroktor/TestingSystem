package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.Question;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Integer> {
}
