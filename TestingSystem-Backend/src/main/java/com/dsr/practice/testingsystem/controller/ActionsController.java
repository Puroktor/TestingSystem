package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.LeaderboardDto;
import com.dsr.practice.testingsystem.entity.Answer;
import com.dsr.practice.testingsystem.mapper.AnswerMapper;
import com.dsr.practice.testingsystem.service.ActionsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Api(tags = "User actions API")
public class ActionsController {

    private final ActionsService actionsService;

    @ApiOperation(value = "Checks your test")
    @PostMapping("submit")
    @PreAuthorize("hasAuthority('USER_SUBMIT')")
    public ResponseEntity<Void> submitAttempt(@RequestBody @ApiParam(value = "Your answers") AnswerDto[] answerDtos,
                                              @RequestParam("userId") @ApiParam(value = "Your user id", example = "1")
                                                      int userId) {
        List<Answer> answers = Arrays.stream(answerDtos).map(AnswerMapper::toEntity).collect(Collectors.toList());
        actionsService.submitAttempt(answers, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @ApiOperation(value = "Returns current leaderboard")
    @GetMapping("leaderboard")
    public ResponseEntity<LeaderboardDto> getLeaderboard() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(actionsService.getLeaderboard());
    }
}
