package com.dsr.practice.testingsystem.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name="test_id", nullable=false)
    private Test test;

    @NotNull(message = "Enter your question")
    @Size(min = 1, max = 200, message = "Question must be between 1 and 200 characters")
    private String text;

    @NotNull(message = "Enter question score")
    @Min(value = 1, message = "Question score must be >= 1")
    private Integer maxScore;

    @OneToMany(mappedBy="question", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @NotNull(message = "Enter at least 1 answer!")
    @Size(min = 1, message = "Enter at least 1 answer!")
    @Valid
    private List<Answer> answers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Question question = (Question) o;
        return id != null && Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
