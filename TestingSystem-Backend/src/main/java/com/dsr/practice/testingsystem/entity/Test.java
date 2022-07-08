package com.dsr.practice.testingsystem.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Enter programming language")
    @Size(max = 50, message = "Programming language length must be <= 50 characters")
    private String programmingLang;

    @NotBlank(message = "Enter test name")
    @Size(max = 50, message = "Test name length must be <= 50 characters")
    private String name;

    @NotNull(message = "Enter questions count!")
    @Min(value = 1, message = "Questions count must be >= 1")
    @Max(value = 50, message = "Questions count must be <= 50")
    private Integer questionsCount;

    @OneToMany(mappedBy = "test", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @NotNull
    @Valid
    private List<Attempt> attempts;

    @OneToMany(mappedBy = "test", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @NotNull(message = "Enter at least 1 question")
    @Size(min = 1, message = "Enter at least 1 question")
    @Valid
    private List<Question> questions;

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
