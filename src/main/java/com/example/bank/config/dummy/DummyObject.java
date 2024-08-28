package com.example.bank.config.dummy;

import com.example.bank.domain.account.Account;
import com.example.bank.domain.account.AccountRepository;
import com.example.bank.domain.transaction.Transaction;
import com.example.bank.domain.transaction.TransactionEnum;
import com.example.bank.domain.user.User;
import com.example.bank.domain.user.UserEnum;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

public class DummyObject {
    protected static User newUser(String username, String fullname){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encPassword = passwordEncoder.encode("1234");
        return User.builder()
                .username(username)
                .password(encPassword)
                .email(username+"@naver.com")
                .fullname("쌀333333")
                .role(UserEnum.CUSTOMER)
                .build();
    }

    protected Account newAccount(Long number, User user){
        return Account.builder()
                .number(number)
                .password(1234L)
                .balance(1000L)
                .user(user)
                .build();
    }
    protected Transaction newWithdrawTransaction(Account account, AccountRepository accountRepository){
        account.withdraw(100L); // 1000원이 있었다면 900원이 됨.
        // 더티체킹이 안되기 때문에
        if(accountRepository != null){
            accountRepository.save(account);
        }
        Transaction transaction = Transaction.builder()
                .withdrawAccount(account)
                .depositAccount(null)
                .withDrawAccountBalance(account.getBalance())
                .depositAccountBalance(null)
                .amount(100L)
                .gubun(TransactionEnum.WITHDRAW)
                .sender(account.getNumber() + "")
                .receiver("ATM")
                .build();
        return transaction;
    }

    protected Transaction newDepositTransaction(Account account, AccountRepository accountRepository){
        account.deposit(100L); // 1000원이 있었다면 900원이 됨.
        // 더티체킹이 안되기 때문에
        if(accountRepository != null){
            accountRepository.save(account);
        }
        Transaction transaction = Transaction.builder()
                .withdrawAccount(null)
                .depositAccount(account)
                .withDrawAccountBalance(null)
                .depositAccountBalance(account.getBalance())
                .amount(100L)
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(account.getNumber() + "")
                .tel("01022223332")
                .build();
        return transaction;
    }

    protected Transaction newTransferTransaction(Account withdrawAccount, Account depositAccount, AccountRepository accountRepository){
        withdrawAccount.withdraw(100L);
        depositAccount.deposit(100L);

        // Repository Test에서는 더티체킹 됨
        // Controller Test에서는 더티체킹 안됨
        if(accountRepository != null){
            accountRepository.save(withdrawAccount);
            accountRepository.save(depositAccount);
        }
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccount)
                .depositAccount(depositAccount)
                .withDrawAccountBalance(withdrawAccount.getBalance())
                .depositAccountBalance(depositAccount.getBalance())
                .amount(100L)
                .gubun(TransactionEnum.TRANSFER)
                .sender(withdrawAccount.getNumber() + "")
                .receiver(depositAccount.getNumber() + "")
                .build();
        return transaction;
    }
}
