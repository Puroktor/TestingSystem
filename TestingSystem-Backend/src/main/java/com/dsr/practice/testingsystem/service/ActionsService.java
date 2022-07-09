package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.LeaderboardPageDto;
import com.dsr.practice.testingsystem.entity.*;
import com.dsr.practice.testingsystem.repository.AnswerRepository;
import com.dsr.practice.testingsystem.repository.AttemptRepository;
import com.dsr.practice.testingsystem.repository.TestRepository;
import com.dsr.practice.testingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ActionsService {
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final TestRepository testRepository;
    private final AttemptRepository attemptRepository;

    public void submitAttempt(List<AnswerDto> answers, int userId) {
        class Score {
            private int correct;
            private int all;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Invalid user Id:" + userId));
        HashMap<Question, Score> questionIdToScoreMap = new HashMap<>();
        for (AnswerDto submittedAnswer : answers) {
            Answer dbAnswer = answerRepository.findById(submittedAnswer.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid answer Id:" + submittedAnswer.getId()));
            Question question = dbAnswer.getQuestion();
            Score score = questionIdToScoreMap.getOrDefault(question, new Score());
            score.correct += dbAnswer.getIsRight() == submittedAnswer.getIsRight() ? 1 : -1;
            score.all++;
            questionIdToScoreMap.put(question, score);
        }
        double score = 0, maxScore = 0;
        for (Map.Entry<Question, Score> entry : questionIdToScoreMap.entrySet()) {
            score += entry.getKey().getMaxScore() * Math.max((double) entry.getValue().correct / entry.getValue().all, 0);
            maxScore += entry.getKey().getMaxScore();
        }
        attemptRepository.save(new Attempt(user, questionIdToScoreMap.keySet().iterator().next().getTest(),
                score / maxScore * 100));
    }

    public LeaderboardPageDto getLeaderboardPage(int index, int size) {
        Pageable pageable = PageRequest.of(index, size);
        Page<User> userPage = userRepository.findPageOfTopByRole(Role.STUDENT, pageable);
        if (userPage.isEmpty()) {
            return new LeaderboardPageDto(null, userPage.map(user -> null));
        }
        Iterable<Test> testList = testRepository.findAll();
        List<User> userList = userPage.getContent();
        Map<User, LeaderboardPageDto.UserRecord> userToRecordMap = new HashMap<>();
        for (User user : userList) {
            List<Attempt> attempts = attemptRepository.findAllByUser(user);
            Map<Integer, Double> testIdToScoreMap = new HashMap<>();
            Double total = 0d;
            for (Attempt attempt : attempts) {
                total += attempt.getScore();
                testIdToScoreMap.put(attempt.getTest().getId(), attempt.getScore());
            }
            userToRecordMap.put(user, new LeaderboardPageDto.UserRecord(user.getNickname(), total, testIdToScoreMap));
        }
        List<LeaderboardPageDto.TestRecord> testRecordList = new ArrayList<>();
        for (Test test : testList) {
            testRecordList.add(new LeaderboardPageDto.TestRecord(test.getId(), test.getName()));
        }
        return new LeaderboardPageDto(testRecordList, userPage.map(userToRecordMap::get));
    }
}
