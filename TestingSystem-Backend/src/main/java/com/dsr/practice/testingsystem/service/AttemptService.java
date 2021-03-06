package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.dto.*;
import com.dsr.practice.testingsystem.entity.*;
import com.dsr.practice.testingsystem.mapper.AttemptMapper;
import com.dsr.practice.testingsystem.mapper.TestMapper;
import com.dsr.practice.testingsystem.repository.AnswerRepository;
import com.dsr.practice.testingsystem.repository.AttemptRepository;
import com.dsr.practice.testingsystem.repository.TestRepository;
import com.dsr.practice.testingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttemptService {
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final TestRepository testRepository;
    private final AttemptRepository attemptRepository;
    private final TestMapper testMapper;
    private final AttemptMapper attemptMapper;

    public AttemptResultDto submitAttempt(List<AnswerDto> answers, String nickname) {
        class Score {
            private int correct;
            private int all;
        }
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("Invalid user nickname"));
        HashMap<Question, Score> questionIdToScoreMap = new HashMap<>();
        Map<Answer, Boolean> submittedAnswers = new HashMap<>();
        for (AnswerDto submittedAnswer : answers) {
            Answer dbAnswer = answerRepository.findById(submittedAnswer.getId())
                    .orElseThrow(() -> new NoSuchElementException("Invalid answer Id"));
            Question question = dbAnswer.getQuestion();
            Score score = questionIdToScoreMap.getOrDefault(question, new Score());
            if (submittedAnswer.getIsRight()) {
                score.correct += dbAnswer.getIsRight() ? 1 : -1;
            }
            if (dbAnswer.getIsRight()) {
                score.all++;
            }
            questionIdToScoreMap.put(question, score);
            submittedAnswers.put(dbAnswer, submittedAnswer.getIsRight());
        }
        double score = 0, maxScore = 0;
        for (Map.Entry<Question, Score> entry : questionIdToScoreMap.entrySet()) {
            score += entry.getKey().getMaxScore() * Math.max((double) entry.getValue().correct / entry.getValue().all, 0);
            maxScore += entry.getKey().getMaxScore();
        }
        double scorePercentage = score / maxScore * 100;
        Test test = questionIdToScoreMap.keySet().iterator().next().getTest();
        Attempt attempt = attemptRepository.save(new Attempt(null, user, test, scorePercentage, LocalDateTime.now(),
                submittedAnswers));
        return attemptMapper.toResultDto(attempt);
    }

    public List<AttemptResultDto> getAttemptsResults(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Invalid user nickname"));
        List<Attempt> attempts = attemptRepository.findAllByUserOrderByDateTimeDesc(user);
        return attempts.stream()
                .map(attemptMapper::toResultDto)
                .collect(Collectors.toList());
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
            Map<Integer, Double> testIdToScoreMap = new HashMap<>();
            Double total = 0d;
            for (Test test : testList) {
                Optional<Attempt> optionalAttempt = attemptRepository.findTopByUserAndTestOrderByDateTimeDesc(user, test);
                if (optionalAttempt.isPresent()) {
                    Attempt attempt = optionalAttempt.get();
                    total += attempt.getScore();
                    testIdToScoreMap.put(attempt.getTest().getId(), attempt.getScore());
                }
            }
            userToRecordMap.put(user, new LeaderboardPageDto.UserRecord(user.getId(), user.getNickname(), total,
                    testIdToScoreMap));
        }
        List<LeaderboardPageDto.TestRecord> testRecordList = new ArrayList<>();
        for (Test test : testList) {
            testRecordList.add(new LeaderboardPageDto.TestRecord(test.getId(), test.getName(), test.getPassingScore()));
        }
        return new LeaderboardPageDto(testRecordList, userPage.map(userToRecordMap::get));
    }

    @Transactional
    public AttemptDto getAttempt(int attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new NoSuchElementException("No such attempt"));
        TestDto testDto = testMapper.toDto(attempt.getTest());
        Map<Integer, Boolean> submittedAnswers = attempt.getSubmittedAnswers().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));
        testDto.setQuestions(
                testDto.getQuestions().stream()
                        .filter(questionDto -> submittedAnswers.containsKey(questionDto.getAnswers().get(0).getId()))
                        .collect(Collectors.toList()));
        return new AttemptDto(attempt.getUser().getId(), attempt.getUser().getNickname(),
                attempt.getScore(), attempt.getDateTime(), testDto, submittedAnswers);
    }
}
