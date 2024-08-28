package com.example.bank.handler.aop;

import com.example.bank.dto.ResponseDto;
import com.example.bank.handler.ex.CustomValidationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
public class CustomValidationAdvice {
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping(){}
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void putMapping(){}

    @Around("postMapping() || putMapping()") // jointPoint의 전후 제어가 됨
    public Object validationAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        Object[] args = proceedingJoinPoint.getArgs(); // jointPoint의 매개변수
        for(Object arg : args) {
            if(arg instanceof BindingResult){   //BindingResult가 있으면 동작
                BindingResult bindingResult = (BindingResult) arg;

                if(bindingResult.hasErrors()){ //에러가 있을때 throw를 날림 에러가 없다면 roceedingJoinPoint.proceed();
                    Map<String,String> errorMap = new HashMap<>();

                    for(FieldError error : bindingResult.getFieldErrors()){
                        errorMap.put(error.getField(), error.getDefaultMessage());
                    }
                    throw new CustomValidationException("유효성검사 실패", errorMap);
                }
            }
        }
        return proceedingJoinPoint.proceed(); //정상적으로 해당 메서드를 실행해라!!
    }
}

//get, delete -> body (x)     //post, put -> body (o)