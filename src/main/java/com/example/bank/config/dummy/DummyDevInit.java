package com.example.bank.config.dummy;

import com.example.bank.domain.account.Account;
import com.example.bank.domain.account.AccountRepository;
import com.example.bank.domain.transaction.Transaction;
import com.example.bank.domain.transaction.TransactionRepository;
import com.example.bank.domain.user.User;
import com.example.bank.domain.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DummyDevInit extends DummyObject{
    @Profile("dev")
    @Bean
    CommandLineRunner init(UserRepository userRepository, AccountRepository accountRepository, TransactionRepository transactionRepository){
        return (args) -> {
            User ssar = userRepository.save(newUser("ssar", "쌀"));
            User cos = userRepository.save(newUser("cos", "코스,"));
            User love = userRepository.save(newUser("love", "러브"));
            User admin = userRepository.save(newUser("admin", "관리자"));

            Account ssarAccount1 = accountRepository.save(newAccount(1111L, ssar));
            Account cosAccount = accountRepository.save(newAccount(2222L, cos));
            Account loveAccount = accountRepository.save(newAccount(3333L, love));
            Account ssarAccount2 = accountRepository.save(newAccount(4444L, ssar));

            Transaction withdrawTransaction1 = transactionRepository
                    .save(newWithdrawTransaction(ssarAccount1, accountRepository));
            Transaction depositTransaction1 = transactionRepository
                    .save(newDepositTransaction(cosAccount, accountRepository));
            Transaction transferTransaction1 = transactionRepository
                    .save(newTransferTransaction(ssarAccount1, cosAccount, accountRepository));
            Transaction transferTransaction2 = transactionRepository
                    .save(newTransferTransaction(ssarAccount1, loveAccount, accountRepository));
            Transaction transferTransaction3 = transactionRepository
                    .save(newTransferTransaction(cosAccount, ssarAccount1, accountRepository));
        };
    }
}
