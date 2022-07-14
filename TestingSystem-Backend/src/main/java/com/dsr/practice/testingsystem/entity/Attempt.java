package com.dsr.practice.testingsystem.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@IdClass(Attempt.AttemptId.class)
public class Attempt {
    @Data
    public static class AttemptId implements Serializable {
        private Integer user;
        private Integer test;
    }

    @Id
    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne()
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @NotNull(message = "Enter your score")
    @Min(value = 0, message = "Your score must be >= 0")
    private Double score;

    @ElementCollection
    @JoinTable(name = "submitted_answer", joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            @JoinColumn(name = "test_id", referencedColumnName = "test_id")})
    @Column(name = "submitted_value")
    @MapKeyJoinColumn(name = "answer_id")
    private Map<Answer, Boolean> submittedAnswers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Attempt attempt = (Attempt) o;
        return user != null && Objects.equals(user, attempt.user)
                && test != null && Objects.equals(test, attempt.test);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, test);
    }
}
