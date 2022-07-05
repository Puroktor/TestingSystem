package com.dsr.practice.testingsystem.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull(message = "Enter programming language")
    @Size(min = 1, max = 20, message = "Programming language must be be between 1 and 20 characters")
    private String programmingLang;

    @NotNull(message = "Enter test name")
    @Size(min = 1, max = 50, message = "Test name must be be between 1 and 50 characters")
    private String name;

    @NotNull(message = "Enter questions count!")
    @Min(value = 1, message = "Questions count must be >= 1")
    @Max(value = 50, message = "Questions count must be <= 50")
    private Integer questionsCount;

    @OneToMany(mappedBy="test", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Attempt> attempts;

    @OneToMany(mappedBy="test", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Question> questionsBank;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Test test = (Test) o;
        return id != null && Objects.equals(id, test.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
