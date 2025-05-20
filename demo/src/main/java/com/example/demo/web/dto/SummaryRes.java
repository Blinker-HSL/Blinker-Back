package com.example.demo.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SummaryRes {
    QuizRes qRes;
    AnswerSummaryRes aRes;
}
