package com.example.demo.web.dto;

import java.util.List;

public record AnswerSummaryRes(List<AnswerSummary> answers) {
    public record AnswerSummary(
            Long answerId,
            String content,
            int isCorrect
    ) {}
}
