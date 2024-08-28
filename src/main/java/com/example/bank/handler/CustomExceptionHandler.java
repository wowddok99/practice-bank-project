package com.example.bank.handler;

import com.example.bank.dto.ResponseDto;
import com.example.bank.handler.ex.CustomApiException;
import com.example.bank.handler.ex.CustomFobiddenException;
import com.example.bank.handler.ex.CustomValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(CustomApiException.class)
    public ResponseEntity<?> apiException(CustomApiException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1,e.getMessage(),null), HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(CustomFobiddenException.class)
    public ResponseEntity<?> fobiddenException(CustomFobiddenException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1,e.getMessage(),null), HttpStatus.FORBIDDEN);

    }
    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<?> validateApiException(CustomValidationException e){
        log.error(e.getMessage());
        return new ResponseEntity<>(new ResponseDto<>(-1,e.getMessage(),e.getErrorMap()), HttpStatus.BAD_REQUEST);

    }
}
