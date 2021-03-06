package com.dsr.practice.testingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardPageDto {
    @Data
    @AllArgsConstructor
    public static class UserRecord {
        private Integer id;
        private String nickname;
        private Double total;
        private Map<Integer, Double> testToScoreMap;
    }

    @Data
    @AllArgsConstructor
    public static class TestRecord {
        private Integer id;
        private String name;
        private Integer passingScore;
    }

    private List<TestRecord> testRecords;
    private Page<UserRecord> userRecords;
}
