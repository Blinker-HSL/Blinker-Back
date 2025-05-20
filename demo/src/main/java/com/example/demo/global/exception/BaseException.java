package com.example.demo.global.exception;

import com.example.demo.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private final BaseResponseCode errorCode;
}
