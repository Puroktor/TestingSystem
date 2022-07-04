package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.entity.SystemUser;
import org.springframework.data.repository.CrudRepository;

public interface SystemUserRepository extends CrudRepository<SystemUser, Integer> {
}
