package com.example.bank.service;

import com.example.bank.domain.user.User;
import com.example.bank.domain.user.UserRepository;
import com.example.bank.dto.user.UserReqDto.JoinReqDto;
import com.example.bank.dto.user.UserRespDto.JoinRespDto;
import com.example.bank.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    //서비스는 DTO를 요청받고, DTO로 응답
    @Transactional // 트랜잭션이 메서드 시작할 때, 시작되고, 종료될 때 함꼐 종료
    public JoinRespDto signIn(JoinReqDto joinReqDto){
        // 1. 동일 유저네임 존재 검사
        Optional<User> userOP = userRepository.findByUsername(joinReqDto.username());
        if(userOP.isPresent()){
            throw new CustomApiException("동일한 username이 존재합니다");
        }
        // 2. 패스워드 인코딩 + 회원가입
        User userPS = userRepository.save(joinReqDto.toEntity(passwordEncoder));

        // 3. dto 응답
        return JoinRespDto.builder()
                .id(userPS.getId())
                .username(userPS.getUsername())
                .fullname(userPS.getFullname())
                .build();
    }



}
