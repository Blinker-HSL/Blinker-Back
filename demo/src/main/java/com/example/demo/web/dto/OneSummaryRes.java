package com.example.demo.web.dto;


import com.example.demo.entity.Quiz;

import java.util.List;

public record OneSummaryRes(
        List<QuizSummary> quizSummaryList) {
    public record QuizSummary(
            Long answerId,
            String content,
            Integer isCorrect
    ) { }
}