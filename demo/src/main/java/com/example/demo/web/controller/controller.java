package com.example.demo.web.controller;

import com.example.demo.entity.Quiz;
import com.example.demo.global.response.SuccessResponse;
import com.example.demo.respository.QuizRepository;
import com.example.demo.service.WebService;
import com.example.demo.web.dto.*;
import com.example.demo.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class controller {
    private final AiService aiService;
    private final WebService webService;

    @PostMapping("/chat")
    public ResponseEntity<?> send(@RequestBody ChatReq request) {
        ChatRes responses = aiService.chat(request);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<SuccessResponse<?>> getQuizWithAnswer(
            @PathVariable Long quizId
    ) {
        SummaryRes whole = webService.getQuiz(quizId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(whole));
    }
}
