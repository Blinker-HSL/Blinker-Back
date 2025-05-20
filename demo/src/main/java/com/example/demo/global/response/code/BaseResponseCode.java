package com.example.demo.global.response.code;

public interface BaseResponseCode {
    String getCode();
    String getMessage();
    int getHttpStatus();
}