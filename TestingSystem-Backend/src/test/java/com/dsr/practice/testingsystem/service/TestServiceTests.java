package com.dsr.practice.testingsystem.service;

import com.dsr.practice.testingsystem.dto.TestDto;
import com.dsr.practice.testingsystem.dto.TestInfoDto;
import com.dsr.practice.testingsystem.entity.Test;
import com.dsr.practice.testingsystem.mapper.TestMapper;
import com.dsr.practice.testingsystem.repository.QuestionRepository;
import com.dsr.practice.testingsystem.repository.TestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TestServiceTests {
    @Autowired
    private TestService testService;
    @MockBean
    private TestRepository testRepository;
    @MockBean
    private QuestionRepository questionRepository;
    @MockBean
    private ModelMapper modelMapper;
    @MockBean
    private TestMapper testMapper;

    @org.junit.jupiter.api.Test
    public void fetchTestPageWithFilter() {
        String filter = "filter";
        int index = 0, size = 1;
        Pageable pageable = PageRequest.of(index, size, Sort.by("id").descending());
        Test test = new Test();
        TestInfoDto testInfoDto = new TestInfoDto();
        Page<Test> page = new PageImpl<>(Collections.singletonList(test));
        when(testRepository.findAllByProgrammingLang(filter, pageable)).thenReturn(page);
        when(modelMapper.map(test, TestInfoDto.class)).thenReturn(testInfoDto);

        assertEquals(new PageImpl<>(Collections.singletonList(testInfoDto)), testService.fetchTestPage(filter, index, size));
        verify(testRepository, times(1)).findAllByProgrammingLang(filter, pageable);
        verify(modelMapper, times(1)).map(test, TestInfoDto.class);
    }

    @org.junit.jupiter.api.Test
    public void fetchTestPageWithNullFilter(){
        int index = 0, size = 1;
        Pageable pageable = PageRequest.of(index, size, Sort.by("id").descending());
        Test test = new Test();
        TestInfoDto testInfoDto = new TestInfoDto();
        Page<Test> page = new PageImpl<>(Collections.singletonList(test));
        when(testRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(test, TestInfoDto.class)).thenReturn(testInfoDto);

        assertEquals(new PageImpl<>(Collections.singletonList(testInfoDto)), testService.fetchTestPage(null, index, size));
        verify(testRepository, times(1)).findAll(pageable);
        verify(modelMapper, times(1)).map(test, TestInfoDto.class);
    }

    @org.junit.jupiter.api.Test
    public void fetchTestPageWithBlankFilter(){
        String filter = "         ";
        int index = 0, size = 1;
        Pageable pageable = PageRequest.of(index, size, Sort.by("id").descending());
        Test test = new Test();
        TestInfoDto testInfoDto = new TestInfoDto();
        Page<Test> page = new PageImpl<>(Collections.singletonList(test));
        when(testRepository.findAll(pageable)).thenReturn(page);
        when(modelMapper.map(test, TestInfoDto.class)).thenReturn(testInfoDto);

        assertEquals(new PageImpl<>(Collections.singletonList(testInfoDto)), testService.fetchTestPage(null, index, size));
        verify(testRepository, times(1)).findAll(pageable);
        verify(modelMapper, times(1)).map(test, TestInfoDto.class);
    }

    @org.junit.jupiter.api.Test
    public void getExistingTest() {
        Test testEntity = new Test();
        TestDto testDto = new TestDto();
        testEntity.setId(1);
        testDto.setId(1);
        when(testRepository.findById(testEntity.getId())).thenReturn(Optional.of(testEntity));
        when(testMapper.toDto(testEntity)).thenReturn(testDto);

        assertEquals(testDto, testService.getTest(testEntity.getId()));
        verify(testRepository, times(1)).findById(testEntity.getId());
        verify(testMapper, times(1)).toDto(testEntity);
    }

    @org.junit.jupiter.api.Test
    public void getMissingTest() {
        int testId = 1;
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> testService.getTest(testId));
        verify(testRepository, times(1)).findById(testId);
    }

    @org.junit.jupiter.api.Test
    public void deleteExistingTest() {
        Test testEntity = new Test();
        testEntity.setId(10);
        when(testRepository.findById(testEntity.getId())).thenReturn(Optional.of(testEntity));
        testService.deleteTest(testEntity.getId());

        verify(testRepository, times(1)).findById(testEntity.getId());
        verify(testRepository, times(1)).delete(testEntity);
    }

    @org.junit.jupiter.api.Test
    public void deleteMissingTest() {
        int testId = 1;
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> testService.deleteTest(testId));
        verify(testRepository, times(1)).findById(testId);
    }
}
