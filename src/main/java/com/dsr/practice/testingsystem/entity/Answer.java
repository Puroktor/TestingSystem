package com.dsr.practice.testingsystem.entity;

import javax.persistence.*;

@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String text;

    private Boolean isRight;

    @ManyToOne
    @JoinColumn(name="question_id", nullable=false)
    private Question question;
}
