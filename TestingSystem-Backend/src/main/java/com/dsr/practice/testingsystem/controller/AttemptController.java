package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.dto.AnswerDto;
import com.dsr.practice.testingsystem.dto.AttemptDto;
import com.dsr.practice.testingsystem.dto.AttemptResultDto;
import com.dsr.practice.testingsystem.dto.LeaderboardPageDto;
import com.dsr.practice.testingsystem.service.AttemptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public ResponseEntity<AttemptResultDto> submitAttempt(@RequestBody @NotNull(message = "Provide answers")
                                                          @Size(min = 1, message = "Provide at least one answer") @Valid
                                                          @ApiParam(value = "Your answers") List<AnswerDto> answers) {
        String nickname = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(attemptService.submitAttempt(answers, nickname));
    }

    @ApiOperation(value = "Returns attempt with submitted answers")
    @GetMapping("attempt/{attemptId}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<AttemptDto> getAttempt(
            @PathVariable @ApiParam(value = "Id of requested attempt", example = "1") int attemptId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(attemptService.getAttempt(attemptId));
    }

    @ApiOperation(value = "Returns results of attempts of specific user")
    @GetMapping("attempts/{userId}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<List<AttemptResultDto>> getAttemptsResults(
            @PathVariable @ApiParam(value = "Id of the user", example = "1") int userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(attemptService.getAttemptsResults(userId));
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
