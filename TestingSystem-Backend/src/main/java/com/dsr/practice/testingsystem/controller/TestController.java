package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.dto.TestDto;
import com.dsr.practice.testingsystem.dto.TestInfoDto;
import com.dsr.practice.testingsystem.service.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Validated
@Api(tags = "Tests API")
public class TestController {
    private final TestService testService;

    @ApiOperation(value = "Creates new test")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("test")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<TestDto> createTest(
            @RequestBody @Valid @ApiParam(value = "Test you want to save") TestDto testDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(testService.createTest(testDto));
    }

    @ApiOperation(value = "Returns page of tests")
    @GetMapping("test")
    @PreAuthorize("hasAuthority('USER_SUBMIT')")
    public ResponseEntity<Page<TestInfoDto>> fetchTestPage(
            @RequestParam(value = "programmingLang", required = false) @Size(max = 50, message = "Filter length must be <=50")
            @ApiParam(value = "String for filtering", example = "Java") String programmingLang,
            @RequestParam("index") @Min(value = 0, message = "Index must be >=0")
            @ApiParam(value = "Index of desired page", example = "1") int index,
            @RequestParam("size") @Min(value = 1, message = "Page size must be >=1")
            @ApiParam(value = "Size of pages", example = "1") int size) {
        String nickname = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testService.fetchTestPage(programmingLang, index, size, nickname));
    }

    @ApiOperation(value = "Returns full test with right answers by id")
    @GetMapping("test/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<TestDto> getTest(@PathVariable @ApiParam(value = "Id of the test", example = "1") int id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testService.getTest(id));
    }

    @ApiOperation(value = "Returns shuffled test without right answers by id")
    @GetMapping("test/shuffled/{id}")
    @PreAuthorize("hasAuthority('USER_SUBMIT')")
    public ResponseEntity<TestDto> getShuffledTest(@PathVariable @ApiParam(value = "Id of the test", example = "1") int id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testService.getShuffledTest(id));
    }

    @ApiOperation(value = "Changes test by id")
    @PutMapping("test/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<Void> updateTest(@PathVariable @ApiParam(value = "Id of the test", example = "1") int id,
                                           @RequestBody @Valid @ApiParam(value = "New test") TestDto testDto) {
        testService.updateTest(id, testDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @ApiOperation(value = "Deletes test by id")
    @DeleteMapping("test/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<Void> deleteTest(@PathVariable @ApiParam(value = "Id of the test", example = "1") int id) {
        testService.deleteTest(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
