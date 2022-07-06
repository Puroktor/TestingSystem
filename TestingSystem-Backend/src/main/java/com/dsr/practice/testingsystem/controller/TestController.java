package com.dsr.practice.testingsystem.controller;

import com.dsr.practice.testingsystem.dto.FullTestDto;
import com.dsr.practice.testingsystem.mapper.TestMapper;
import com.dsr.practice.testingsystem.entity.Test;
import com.dsr.practice.testingsystem.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
public class TestController {
    private final TestService testService;

    @PostMapping("test")
    public ResponseEntity<?> createTest(@RequestBody FullTestDto testDto) {
        Test test = TestMapper.toEntity(testDto);
        Test savedTest = testService.createTest(test);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(TestMapper.toFullDto(savedTest));
    }

    @GetMapping("test")
    public ResponseEntity<?> fetchTestPage(@RequestParam(value = "programmingLang", required = false) String programmingLang,
                                           @RequestParam("index") int index,
                                           @RequestParam("size") int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(testService.fetchTestPage(programmingLang, index, size).map(TestMapper::tooInfoDto));
    }

    @GetMapping("test/{id}")
    @Transactional
    public ResponseEntity<?> getTest(@PathVariable Integer id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(TestMapper.toFullDto(testService.getTest(id)));
    }

    @PutMapping("test/{id}")
    public ResponseEntity<?> updateTest(@PathVariable int id, @RequestBody FullTestDto testDto) {
        Test test = TestMapper.toEntity(testDto);
        testService.updateTest(id, test);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("test/{id}")
    public ResponseEntity<?> deleteTest(@PathVariable int id) {
        testService.deleteTest(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
