package com.example.bank.dto.user;

import com.example.bank.domain.user.User;
import lombok.Builder;
public record UserRespDto(){
    @Builder
    public record LoginRespDto(
            Long id,
            String username,
            String createdAt
    ){
        public LoginRespDto {
        }
    }
    @Builder
    public record JoinRespDto(
            Long id,
            String username,
            String fullname
    ){}

}
