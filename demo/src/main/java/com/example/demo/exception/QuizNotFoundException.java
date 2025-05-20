package com.example.demo.exception;

import com.example.demo.global.exception.BaseException;

public class QuizNotFoundException extends BaseException {
    public QuizNotFoundException() {
        super(MyErrorCode.QUIZ_NOT_FOUND_404);
    }
}
