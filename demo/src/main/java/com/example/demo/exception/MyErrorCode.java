package com.example.demo.exception;

import com.example.demo.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MyErrorCode implements BaseResponseCode {
    QUIZ_NOT_FOUND_404("QUIZ_NOT_FOUND_404", 404, "해당 퀴즈를 조회할 수 없습니다.");


    private final String code;
    private final int httpStatus;
    private final String message;
}


