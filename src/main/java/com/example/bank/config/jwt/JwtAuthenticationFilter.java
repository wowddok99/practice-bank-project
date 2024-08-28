package com.example.bank.config.jwt;

import com.example.bank.config.auth.LoginUser;
import com.example.bank.dto.user.UserReqDto.LoginReqDto;
import com.example.bank.dto.user.UserRespDto.LoginRespDto;
import com.example.bank.util.CustomDateUtil;
import com.example.bank.util.CustomResponseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager){
        super(authenticationManager);
        setFilterProcessesUrl("/api/login"); //기본값은 /login 임, setFilterProcessesUrl로 커스텀
        this.authenticationManager = authenticationManager;
    }

    // Post : /api/login시 동작
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.debug("디버그 : attemptAuthentication 호출됨");
        try{
            ObjectMapper om = new ObjectMapper();
            LoginReqDto loginReqDto = om.readValue(request.getInputStream(), LoginReqDto.class);

            // 강제 로그인
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginReqDto.username(), loginReqDto.password());

            // UserDetailsService의 loadUserByUsername 호출
            // 강제 로그인을 진행하는 이유 -> JWT를 쓴다고 하더라도, 컨트롤러 진입을 하면 시큐리티의 권한체크, 인증체크(authorizeRequests())의 도움을 받을 수 있게 세션을 만든다.
            // 이 세션의 유효기간은 request하고, response하면 끝!!
            Authentication authentication = authenticationManager.authenticate(authenticationToken); //세션생성
            return authentication;
        }catch (Exception e){
           // unsuccessfulAuthentication 호출함
           throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }
    // 로그인 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        CustomResponseUtil.fail(response,"로그인실패", HttpStatus.UNAUTHORIZED); //401
    }

    // return authentication 잘 작동하면 successfulAuthentication 메서드가 호출된다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.debug("디버그 : successfulAuthentication 호출됨"); //얘가 호출되었다는건 로그인이 되고 세션이 만들어짐.
        LoginUser loginUser = (LoginUser) authResult.getPrincipal();
        String jwtToken = JwtProcess.create(loginUser);
        response.addHeader(JwtVO.HEADER, jwtToken);

        LoginRespDto loginRespDto = new LoginRespDto(loginUser.getUser().getId(), loginUser.getUser().getUsername(), CustomDateUtil.toStringFormat(loginUser.getUser().getCreateAt()));
        CustomResponseUtil.success(response,loginRespDto);
    }
}
