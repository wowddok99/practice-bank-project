package com.example.bank.config.jwt;

import com.example.bank.config.auth.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//모든 주소에서 동작함 (토큰 검증)
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final Logger log = LoggerFactory.getLogger(getClass());
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    //JWT 토큰 헤더를 추가하지 않아도 해당 필터는 통과는 할 수 있지만, 결국 시큐리티단(Security Config)에서 세션 값 검증에 실패함.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(isHeaderVerify(request,response)){
            //토큰이 존재함
            log.debug("디버그 : 토큰이 존재함");
            
            String token = request.getHeader(JwtVO.HEADER).replace(JwtVO.TOKEN_PREFIX, "");
            LoginUser loginUser = JwtProcess.verify(token);
            log.debug("디버그 : 토큰이 검증이 완료됨");
            
            //임시 세션(UserDetails 타입 or username)
            Authentication authentication = new UsernamePasswordAuthenticationToken(loginUser,null,loginUser.getAuthorities()); //id와 롤만 존재
            SecurityContextHolder.getContext().setAuthentication(authentication); //세션생성
            log.debug("디버그 : 임시 세션이 생성됨");
        }
            chain.doFilter(request,response);
    }

    private boolean isHeaderVerify(HttpServletRequest request, HttpServletResponse response){
        String header = request.getHeader(JwtVO.HEADER);
        if(header == null || !header.startsWith(JwtVO.TOKEN_PREFIX)){
            return false;
        }else{
            return true;
        }
    }
}
