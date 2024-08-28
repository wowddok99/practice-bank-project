package com.example.bank.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserEnum {
    ADMIN("관리자"), CUSTOMER("고객");
    private String  value;
}
