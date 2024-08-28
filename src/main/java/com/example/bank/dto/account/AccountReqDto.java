package com.example.bank.dto.account;

import com.example.bank.domain.account.Account;
import com.example.bank.domain.user.User;
import lombok.Builder;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record AccountReqDto(){
    @Builder
    public record AccountSaveReqDto(
            @NotNull
            @Digits(integer = 4, fraction = 4)
            Long number,
            @NotNull
            @Digits(integer = 4, fraction = 4)
            Long password
    ) {
        public Account toEntity(User user){
            return Account.builder()
                    .number(this.number)
                    .password(this.password)
                    .balance(1000L)
                    .user(user)
                    .build();
        }
    }
    @Builder
    public record AccountDepositReqDto(
            @NotNull
            @Digits(integer = 4, fraction = 4)
            Long number, //계좌번호
            @NotNull
            Long amount, //금액
            @NotEmpty
            @Pattern(regexp = "^(DEPOSIT)$")
            String gubun, //DEPOSIT

            @NotEmpty
            @Pattern(regexp ="^[0-9]{11}")
            String tel // 전화번호(입금실수 대비 연락처)
    ){}

    @Builder
    public record AccountWithdrawReqDto(
            @NotNull
            @Digits(integer = 4, fraction = 4)
            Long number,
            @NotNull
            @Digits(integer = 4, fraction = 4)
            Long password,
            @NotNull
            Long amount,
            @NotEmpty
            @Pattern(regexp = "^(WITHDRAW)$")
            String gubun
    ){}

    @Builder
    public record AccountTransferReqDto(
            @NotNull
            @Digits(integer = 4, fraction = 4)
            Long withdrawNumber,
            @NotNull
            @Digits(integer = 4, fraction = 4)
            Long depositNumber,
            @NotNull
            @Digits(integer = 4, fraction = 4)
            Long withdrawPassword,
            @NotNull
            Long amount,
            @NotEmpty
            @Pattern(regexp = "^(TRANSFER)$")
            String gubun
    ){}
}