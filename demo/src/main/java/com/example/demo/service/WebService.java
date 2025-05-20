package com.example.demo.service;

import com.example.demo.web.dto.SummaryRes;

public interface WebService {
    SummaryRes getQuiz(Long quizId);
}
