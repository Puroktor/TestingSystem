package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.Student;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Integer> {
}
