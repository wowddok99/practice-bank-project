package com.example.bank.domain.transaction;

import com.example.bank.domain.account.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor // 스프링이 user 객체생성할 때 빈생성자로 new를 하기 때문
@EntityListeners(AuditingEntityListener.class)
@Table(name="transaction_tb")
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch= FetchType.LAZY)
    private Account withdrawAccount;

    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch= FetchType.LAZY)
    private Account depositAccount;

    @Column(nullable = false)
    private Long amount;

    private Long withDrawAccountBalance; //잔액 기록
    private Long depositAccountBalance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionEnum gubun; //WITHDRAW, DESPOSIT, TRANSPER, ALL

    //계좌가 사라져도 로그는 남아야 한다.
    private String sender;
    private String receiver;
    private String tel;

    @CreatedDate // Insert
    @Column(nullable = false)
    private LocalDateTime createAt;
    @LastModifiedDate // Insert or Update
    @Column(nullable = false)
    private LocalDateTime updateAt;
}
