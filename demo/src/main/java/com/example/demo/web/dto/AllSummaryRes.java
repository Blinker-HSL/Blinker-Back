package com.example.demo.web.dto;

import com.example.demo.entity.Answer;
import com.example.demo.entity.Quiz;

import java.util.List;
import java.util.Map;

public record AllSummaryRes(
       List<QuizSum> all
) {
    public record QuizSum(
            Long id,
            String Question,
            List<ASet> AnswerSet
    ) {
        public record ASet(
                String content,
                int isCorrect
        ) {}
    }
}
