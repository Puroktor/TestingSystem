package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.dto.LeaderboardDto;
import com.dsr.practice.testingsystem.entity.*;
import com.dsr.practice.testingsystem.repository.AnswerRepository;
import com.dsr.practice.testingsystem.repository.AttemptRepository;
import com.dsr.practice.testingsystem.repository.TestRepository;
import com.dsr.practice.testingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Size;
import java.util.*;

@Service
@RequiredArgsConstructor
@Validated
public class ActionsService {
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final TestRepository testRepository;
    private final AttemptRepository attemptRepository;

    public void submitAttempt(@Size(min = 1, message = "Provide at least one answer!") List<Answer> answers, int userId) {
        class Score {
            private int correct;
            private int all;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Invalid user Id:" + userId));
        HashMap<Question, Score> questionIdToScoreMap = new HashMap<>();
        for (Answer submittedAnswer : answers) {
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

    public LeaderboardDto getLeaderboard() {
        Iterable<Test> testList = testRepository.findAll();
        Iterable<User> userList = userRepository.findAllByRole(Role.STUDENT);
        List<LeaderboardDto.UserRecord> userRecordList = new ArrayList<>();
        for (User user : userList) {
            List<Attempt> attempts = attemptRepository.findAllByUser(user);
            Map<Integer, Double> testIdToScoreMap = new HashMap<>();
            Double total = 0d;
            for (Attempt attempt : attempts) {
                total += attempt.getScore();
                testIdToScoreMap.put(attempt.getTest().getId(), attempt.getScore());
            }
            userRecordList.add(new LeaderboardDto.UserRecord(user.getNickname(), total, testIdToScoreMap));
        }
        List<LeaderboardDto.TestRecord> testRecordList = new ArrayList<>();
        for (Test test : testList) {
            testRecordList.add(new LeaderboardDto.TestRecord(test.getId(), test.getName()));
        }
        userRecordList.sort(Comparator.comparing(LeaderboardDto.UserRecord::getTotal).reversed());
        return new LeaderboardDto(testRecordList, userRecordList);
    }
}
