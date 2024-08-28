package com.example.bank.config.jwt;
//SECRET은 노출되면 안된다.
//리플래시 토큰(X)
public interface JwtVO {
    public static final String SECRET = "메타코딩"; // HS256 (대칭키)
    public static final int EXPIRATION_TIME = 1000* 60 * 60 * 24 * 7; // 일주일
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER ="Authorization";
}

