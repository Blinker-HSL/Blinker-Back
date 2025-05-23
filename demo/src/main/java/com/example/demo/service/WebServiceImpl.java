package com.example.demo.service;

import com.example.demo.entity.Answer;
import com.example.demo.entity.Quiz;
import com.example.demo.respository.AnswerRepository;
import com.example.demo.respository.QuizRepository;
import com.example.demo.web.dto.AllSummaryRes;
import com.example.demo.web.dto.OneSummaryRes;
import com.example.demo.web.dto.Test;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebServiceImpl implements WebService {
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;

    @Override
    public AllSummaryRes getQuiz() {
        return new AllSummaryRes(
                quizRepository.findAll().stream()
                        .map(q -> new AllSummaryRes.QuizSum(
                                q.getId(),
                                q.getQuestion(),
                                q.getAnswers().stream()
                                        .map(a-> new AllSummaryRes.QuizSum.ASet(
                                                a.getContent(),
                                                a.getIsCorrect()))
                                        .collect(Collectors.toList())
                        ))
                        .collect(Collectors.toList())
        );
//        return new OneSummaryRes(
//                answerRepository.findAll().stream()
//                        .map(a -> new OneSummaryRes.QuizSummary(
//
//                                a.getId(),
//                                a.getContent(),
//                                a.getIsCorrect()
//                        ))
//                        .collect(Collectors.toList())
//        );
    }
}
