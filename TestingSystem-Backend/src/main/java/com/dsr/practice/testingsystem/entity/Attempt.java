package com.dsr.practice.testingsystem.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(indexes = @Index(columnList = "dateTime"))
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne()
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @NotNull(message = "Enter your score")
    @Min(value = 0, message = "Your score must be >= 0")
    private Double score;

    @NotNull(message = "Enter attempt dateTime")
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime dateTime;

    @ElementCollection
    @JoinTable(name = "submitted_answer", joinColumns = @JoinColumn(name = "attempt_id", referencedColumnName = "id"))
    @Column(name = "submitted_value")
    @MapKeyJoinColumn(name = "answer_id")
    @ToString.Exclude
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
