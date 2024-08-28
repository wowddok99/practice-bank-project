package com.example.bank.domain.account;

import com.example.bank.domain.user.User;
import com.example.bank.handler.ex.CustomApiException;
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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor // 스프링이 user 객체생성할 때 빈생성자로 new를 하기 때문
@EntityListeners(AuditingEntityListener.class)
@Table(name="account_tb")
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true,nullable = false,length = 4)
    private Long number; //계좌번호
    @Column(nullable = false, length = 4)
    private Long password; //계좌비번
    @Column(nullable = false)
    private Long balance; //잔액(기본값 1000원)

    // 항상 ORM에서 fk의 주인은 ManyEntity 쪽이다. //account.getUser().아무필드호출() -> Lazy 발동
    @ManyToOne(fetch = FetchType.LAZY)
    private User user; //

    @CreatedDate // Insert
    @Column(nullable = false)
    private LocalDateTime createAt;
    @LastModifiedDate // Insert or Update
    @Column(nullable = false)
    private LocalDateTime updateAt;

    public void checkOwner(Long userId){
        //  String testUsername = user.getUsername(); // Lazy 로딩이 되어야 함.
        //  user.id로는 프록시 관련 문제로 null값 처리됨. user.getId() 메서드를 이용해야함.
        if(user.getId().longValue() != userId.longValue()){
            throw new CustomApiException("계좌 소유자가 아닙니다.");
        }
    }

    public void deposit(Long amount) {
        balance = balance + amount;
    }
    
    //Long type은 128넘어가면 비교연산자로는 비교 불가 -> equals() 사용
    public void checkSamePassword(Long password) {
//        if(this.password.longValue() != password.longValue()){
//            throw new CustomApiException("계좌 비밀번호 검증에 실패했습니다.");
//        }
        if(!this.password.equals(password)){
            throw new CustomApiException("계좌 비밀번호 검증에 실패했습니다.");
        }
    }

    public void checkBalance(Long amount) {
        if(this.balance < amount){
            throw new CustomApiException("계좌 잔액이 부족합니다.");
        }
    }

    public void withdraw(Long amount) {
        checkBalance(amount);
        balance = balance - amount;
    }
}

