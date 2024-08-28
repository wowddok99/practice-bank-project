package com.example.bank.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor // 스프링이 user 객체생성할 때 빈생성자로 new를 하기 때문
@EntityListeners(AuditingEntityListener.class)
@Table(name="user_tb")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length=60) //패스워드 인코딩(BCrypt)
    private String password;

    @Column(nullable = false, length=20)
    private String email;

    @Column(nullable = false,length=20)
    private String fullname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
//    @Column
    private UserEnum role; //ADMIN,CUSTOMER

    @CreatedDate // Insert
    @Column(nullable = false)
    private LocalDateTime createAt;

    @LastModifiedDate // Insert or Update
    @Column(nullable = false)
    private LocalDateTime updateAt;
}
