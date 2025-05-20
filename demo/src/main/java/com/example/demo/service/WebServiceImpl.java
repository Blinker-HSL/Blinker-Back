package com.example.demo.service;

import com.example.demo.entity.Quiz;
import com.example.demo.exception.QuizNotFoundException;
import com.example.demo.respository.AnswerRepository;
import com.example.demo.respository.QuizRepository;
import com.example.demo.web.dto.AnswerSummaryRes;
import com.example.demo.web.dto.QuizRes;
import com.example.demo.web.dto.SummaryRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebServiceImpl implements WebService {
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;

    @Override
    public SummaryRes getQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(QuizNotFoundException::new);
        QuizRes qRes = new QuizRes(
                quiz.getId(),
                quiz.getQuestion()
        );
        AnswerSummaryRes res = new AnswerSummaryRes(
            answerRepository.findAllByQuizId(quizId).stream()
                    .map(a -> new AnswerSummaryRes.AnswerSummary(
                            a.getId(),
                            a.getContent(),
                            a.getIsCorrect()
                        ))
                        .collect(Collectors.toList()));

        return new SummaryRes(qRes, res);
    }
}
