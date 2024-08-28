package com.example.bank.dto;

public record ResponseDto<T>(
        Integer code, //1 성공, -1 실패
        String msg,
        T data
) { }
//@RequiredArgsConstructor
//@Builder
//public class ResponseDto<T> {
//    private final Integer code;
//    private final String msg;
//    private final T data;
//}