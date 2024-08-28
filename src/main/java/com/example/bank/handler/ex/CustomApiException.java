package com.example.bank.handler.ex;

import lombok.Getter;

public class CustomApiException extends RuntimeException{
    public CustomApiException(String message){
        super(message);
    }
}
