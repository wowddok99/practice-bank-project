package com.example.bank.dto.user;


import com.example.bank.domain.user.User;
import com.example.bank.domain.user.UserEnum;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record UserReqDto(

) {
    @Builder
    public record LoginReqDto(
       String username,
       String password
    ){}
    @Builder
    public record JoinReqDto(
            //영문, 숫자는 되고, 길이 최소 2~20자 이내
            @Pattern(regexp = "^[a-zA-z0-9]{2,20}$", message = "영문/숫자 2~20이내로 작성해주세요")
            @NotEmpty //null이거나, 공백일 수 없다.
            String username,

            // 길이 4~20
            @NotEmpty
            @Size(min = 4, max = 20)
            String password,

            //이메일 형식
            @NotEmpty
            @Pattern(regexp = "^[a-zA-z0-9]{2,10}@[a-zA-z0-9]{2,6}\\.[a-zA-Z]{2,3}$", message = "이메일 형식으로 작성해주세요")
            String email,

            // 영어, 한글 1~20
            @NotEmpty
            @Pattern(regexp = "^[a-zA-z가-힣]{1,20}$", message = "한글/영문 1~20자 이내로 작성해주세요")
            String fullname
    ){
        public User toEntity(BCryptPasswordEncoder passwordEncoder){
            return User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .email(email)
                    .fullname(fullname)
                    .role(UserEnum.CUSTOMER)
                    .build();
        }
    }



}