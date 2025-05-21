package com.example.demo.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatReq {
    private int age;
    private double vision;
    private List<String> diseaseTags;
}
