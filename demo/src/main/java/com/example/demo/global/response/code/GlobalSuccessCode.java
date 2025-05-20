package com.example.demo.global.response.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.example.demo.global.constant.StaticValue.*;


@Getter
@AllArgsConstructor
public enum GlobalSuccessCode implements BaseResponseCode {
    SUCCESS_OK( "SUCCESS_200", OK,"호출에 성공하였습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;

}
