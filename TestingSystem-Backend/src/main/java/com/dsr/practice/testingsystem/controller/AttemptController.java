package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.AttemptDto;
import com.dsr.practice.testingsystem.dto.LeaderboardPageDto;
import com.dsr.practice.testingsystem.service.AttemptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Validated
@Api(tags = "Attempt API")
public class AttemptController {
    private final AttemptService attemptService;

    @ApiOperation(value = "Checks your test")
    @PostMapping("attempt")
    @PreAuthorize("hasAuthority('USER_SUBMIT')")
    public ResponseEntity<Void> submitAttempt(@RequestBody @NotNull(message = "Provide answers")
                                              @Size(min = 1, message = "Provide at least one answer")
                                              @ApiParam(value = "Your answers") List<AnswerDto> answers,
                                              @RequestParam("userId") @ApiParam(value = "Your user id", example = "1")
                                                      int userId) {
        attemptService.submitAttempt(answers, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @ApiOperation(value = "Returns attempt with submitted answers")
    @GetMapping("attempt")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<AttemptDto> getAttempt(
            @RequestParam("userId") @ApiParam(value = "Id of requested user", example = "1") int userId,
            @RequestParam("testId") @ApiParam(value = "Id of requested test", example = "1") int testId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(attemptService.getAttempt(userId, testId));
    }

    @ApiOperation(value = "Returns requested page of leaderboard")
    @GetMapping("leaderboard")
    @PreAuthorize("hasAuthority('USER_SUBMIT')")
    public ResponseEntity<LeaderboardPageDto> getLeaderboardPage(
            @RequestParam("index") @Min(value = 0, message = "Index must be >=0")
            @ApiParam(value = "Index of desired page", example = "1") int index,
            @RequestParam("size") @Min(value = 1, message = "Page size must be >=1")
            @ApiParam(value = "Size of pages", example = "1") int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(attemptService.getLeaderboardPage(index, size));
    }
}
