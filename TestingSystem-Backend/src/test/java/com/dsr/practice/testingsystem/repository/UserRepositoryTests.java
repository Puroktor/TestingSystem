package com.dsr.practice.testingsystem.repository;

import com.dsr.practice.testingsystem.SampleDataProvider;
import com.dsr.practice.testingsystem.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestEntityManager
@Transactional
public class UserRepositoryTests {
    @Autowired
    private SampleDataProvider dataProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    @org.junit.jupiter.api.Test
    public void findPageOfTopByDifferentRole() {
        User user = dataProvider.getUser();
        user.setId(null);
        entityManager.persist(user);
        Page<User> page = userRepository.findPageOfTopByRole(Role.TEACHER, PageRequest.of(0, 1));

        assertTrue(page.isEmpty());
        assertEquals(0, page.getTotalElements());
        assertEquals(0, page.getContent().size());
    }

    @org.junit.jupiter.api.Test
    public void findPageOfTopByRole() {
        Test test = dataProvider.getValidTestWithBank();
        test.setId(null);
        for (Question question : test.getQuestions()) {
            question.setId(null);
            question.getAnswers().forEach(ans -> ans.setId(null));
        }
        entityManager.persist(test);
        for (int i = 0; i < 3; i++) {
            User user = dataProvider.getUser();
            user.setId(null);
            user.setNickname(String.valueOf(i));
            entityManager.persist(user);
            if (i == 1) {
                Attempt attempt = new Attempt(null, user, test, 100d, LocalDateTime.now(), new HashMap<>());
                user.getAttempts().add(attempt);
                entityManager.persist(attempt);
            } else if (i == 2) {
                Attempt attempt1 = new Attempt(null, user, test, 60d, LocalDateTime.now(), new HashMap<>());
                Attempt attempt2 = new Attempt(null, user, test, 70d, LocalDateTime.now().plusDays(1),
                        new HashMap<>());
                user.getAttempts().add(attempt1);
                user.getAttempts().add(attempt2);
                entityManager.persist(attempt1);
                entityManager.persist(attempt2);
            }
        }
        Page<User> page = userRepository.findPageOfTopByRole(Role.STUDENT, PageRequest.of(0, 2));
        List<User> topUsers = page.getContent();
        assertEquals("1", topUsers.get(0).getNickname());
        assertEquals("2", topUsers.get(1).getNickname());
    }
}