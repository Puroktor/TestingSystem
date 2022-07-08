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
@Table(name = "system_user")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Enter your name")
    @Size(min = 1, max = 100, message = "Your name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Enter your nickname")
    @Size(min = 1, max = 50, message = "Your nickname must be between 1 and 50 characters")
    @Column(unique = true)
    private String nickname;

    @NotNull(message = "Enter your password")
    @Size(min = 1, max = 256, message = "Your password must be between 1 and 256 characters")
    private String password;

    @NotNull(message = "Enter your role")
    private Role role;

    @NotNull(message = "Enter your university")
    @Size(min = 1, max = 100, message = "University name must be between 1 and 100 characters")
    private String university;

    @Min(value = 1, message = "Year must be >= 1")
    @Max(value = 6, message = "Year must be <= 6")
    private Integer year;

    @Min(value = 1, message = "Group number must be >= 1")
    private Integer groupNumber;

    @NotNull(message = "Enter your email")
    @Size(min = 1, max = 320, message = "Email must be between 1 and 320 characters")
    private String email;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Attempt> attempts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
