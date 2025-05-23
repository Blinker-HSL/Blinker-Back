package com.example.demo.service;

import com.example.demo.entity.Quiz;
import com.example.demo.web.dto.AllSummaryRes;
import com.example.demo.web.dto.OneSummaryRes;
import com.example.demo.web.dto.Test;

import java.util.List;

public interface WebService {
    AllSummaryRes getQuiz();
}
