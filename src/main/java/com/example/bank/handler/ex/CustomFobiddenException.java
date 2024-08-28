package com.example.bank.handler.ex;

import lombok.Getter;

// 추후에 사용할 예정
public class CustomFobiddenException extends RuntimeException{
    public CustomFobiddenException(String message){
        super(message);
    }
}
