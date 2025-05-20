package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;

    @OneToMany(mappedBy = "quiz")
    private List<Answer> answers = new ArrayList<>();

}
