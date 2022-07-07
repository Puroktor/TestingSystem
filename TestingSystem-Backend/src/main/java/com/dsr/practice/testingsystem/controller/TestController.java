package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.dto.FullTestDto;
import com.dsr.practice.testingsystem.dto.TestInfoDto;
import com.dsr.practice.testingsystem.entity.Test;
import com.dsr.practice.testingsystem.mapper.TestMapper;
import com.dsr.practice.testingsystem.service.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@Api(tags = "Tests API")
public class TestController {
    private final TestService testService;

    @ApiOperation(value = "Creates new test")
    @PostMapping("test")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<FullTestDto> createTest(
            @RequestBody @ApiParam(value = "Test you want to save") FullTestDto testDto) {
        Test test = TestMapper.toEntity(testDto);
        Test savedTest = testService.createTest(test);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TestMapper.toFullDtoWithRightAnswers(savedTest));
    }

    @ApiOperation(value = "Returns page of tests")
    @GetMapping("test")
    public ResponseEntity<Page<TestInfoDto>> fetchTestPage(
            @RequestParam(value = "programmingLang", required = false)
            @ApiParam(value = "String for filtering", example = "Java") String programmingLang,
            @RequestParam("index") @ApiParam(value = "Index of desired page", example = "1") int index,
            @RequestParam("size") @ApiParam(value = "Size of pages", example = "1") int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testService.fetchTestPage(programmingLang, index, size).map(TestMapper::tooInfoDto));
    }

    @ApiOperation(value = "Returns full test with right answers by id")
    @GetMapping("test/{id}")
    @Transactional
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<FullTestDto> getTest(@PathVariable @ApiParam(value = "Id of the test", example = "1") int id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TestMapper.toFullDtoWithRightAnswers(testService.getTest(id)));
    }

    @ApiOperation(value = "Returns shuffled test without right answers by id")
    @GetMapping("test/shuffled/{id}")
    @Transactional
    @PreAuthorize("hasAuthority('USER_SUBMIT')")
    public ResponseEntity<FullTestDto> getShuffledTest(@PathVariable @ApiParam(value = "Id of the test", example = "1") int id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TestMapper.toFullDtoWithoutRightAnswers(testService.getTest(id)));
    }

    @ApiOperation(value = "Changes test by id")
    @PutMapping("test/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<Void> updateTest(@PathVariable @ApiParam(value = "Id of the test", example = "1") int id,
                                           @RequestBody @ApiParam(value = "New test") FullTestDto testDto) {
        Test test = TestMapper.toEntity(testDto);
        testService.updateTest(id, test);
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
