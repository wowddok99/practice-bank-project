package com.example.bank.service;

import com.example.bank.domain.account.Account;
import com.example.bank.domain.account.AccountRepository;
import com.example.bank.domain.transaction.Transaction;
import com.example.bank.domain.transaction.TransactionRepository;
import com.example.bank.dto.transaction.TransactionRespDto.TransactionListRespDto;
import com.example.bank.handler.ex.CustomApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    public TransactionListRespDto findDepositWithdrawalList(Long userId, Long accountNumber, String gubun, int page) {
        Account accountPS = accountRepository.findByNumber(accountNumber)
                .orElseThrow(() -> new CustomApiException("해당 계좌를 찾을 수 없습니다"));

        accountPS.checkOwner(userId);

        List<Transaction> transactionListPS = transactionRepository.findTransactionList(accountPS.getId(), gubun, page);
        return new TransactionListRespDto(transactionListPS, accountPS);
    }
}