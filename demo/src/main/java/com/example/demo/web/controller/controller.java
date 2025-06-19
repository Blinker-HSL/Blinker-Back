package com.example.demo.web.controller;

import com.example.demo.entity.Quiz;
import com.example.demo.global.response.SuccessResponse;
import com.example.demo.service.WebService;
import com.example.demo.web.dto.*;
import com.example.demo.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
@Slf4j
public class controller {
    private final AiService aiService;
    private final WebService webService;

    ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/chat")
    public ResponseEntity<?> send(
            @RequestParam("age") int age,
            @RequestParam("vision") double vision,
            @RequestParam("tags") String tagsJson,
            @RequestPart("images") List<MultipartFile> images
    ) {
        log.info(tagsJson);
        log.info("size", images.size());
        try {
            // 받아온 tagsJson(눈 증상)을 DTO 형식에 맞게 objectMapper로 문자열을 리스트로 파싱
            List<String> tags = objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});

            // setter로 DTO 생성
            ChatReq request = new ChatReq();
            request.setAge(age);
            request.setVision(vision);
            request.setDiseaseTags(tags);

            // 이미지 List도 Service 로직에게 전달
            ChatRes res = aiService.chat(request, images);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(res);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/quiz")
    public ResponseEntity<SuccessResponse<?>> getQuizWithAnswer() {
        AllSummaryRes whole = webService.getQuiz();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(whole));
    }
}
