package com.example.demo.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TagRequest {
    private int age;
    private double vision;
    private int blinkCount;
    private List<String> tag;
}
