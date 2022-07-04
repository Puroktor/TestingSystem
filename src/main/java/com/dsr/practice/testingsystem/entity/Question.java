package com.dsr.practice.testingsystem.entity;

import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name="test_id", nullable=false)
    private Test test;

    @NotNull(message = "Enter your question")
    @Size(min = 1, max = 200, message = "Question must be be between 1 and 200 characters")
    private String text;

    @NotNull(message = "Enter question score")
    @Min(value = 1, message = "Question score must be >= 1")
    private Integer maxScore;

    @OneToMany(mappedBy="question", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Answer> answers;
}
