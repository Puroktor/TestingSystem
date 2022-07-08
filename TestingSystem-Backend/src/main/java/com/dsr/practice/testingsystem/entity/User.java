package com.dsr.practice.testingsystem.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "system_user")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Enter your name")
    @Size(max = 100, message = "Your name length must be <= 100 characters")
    private String name;

    @NotBlank(message = "Enter your nickname")
    @Size(max = 50, message = "Your nickname length must be <= 50 characters")
    @Column(unique = true)
    private String nickname;

    @NotBlank(message = "Enter your password")
    @Size(max = 256, message = "Your password length must be <= 256 characters")
    private String password;

    @NotNull(message = "Enter your role")
    private Role role;

    @NotBlank(message = "Enter your university")
    @Size(max = 100, message = "University name length must be <= 100 characters")
    private String university;

    @Min(value = 1, message = "Year must be >= 1")
    @Max(value = 6, message = "Year must be <= 6")
    private Integer year;

    @Min(value = 1, message = "Group number must be >= 1")
    private Integer groupNumber;

    @NotBlank(message = "Enter your email")
    @Size(max = 320, message = "Email length must be <= 320 characters")
    @Email(message = "Not valid email", regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")
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
