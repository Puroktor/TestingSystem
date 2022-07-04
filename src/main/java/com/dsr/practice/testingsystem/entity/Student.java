package com.dsr.practice.testingsystem.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull(message = "Enter your university")
    @Size(min = 1, max = 100, message = "University name must be be between 1 and 100 characters")
    private String university;

    @NotNull(message = "Enter your university year")
    @Min(value = 1, message = "Year must be >= 1")
    @Max(value = 6, message = "Year must be <= 6")
    private Integer year;

    @NotNull(message = "Enter your group number")
    @Min(value = 1, message = "Group number must be >= 1")
    private Integer groupNumber;

    @NotNull(message = "Enter your email")
    @Size(min = 1, max = 320 , message = "Email must be be between 1 and 320 characters")
    private String email;

    @OneToMany(mappedBy="student", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<CompletedTest> completedTests;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Student student = (Student) o;
        return id != null && Objects.equals(id, student.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
