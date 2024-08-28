package com.example.bank.dto.account;

import com.example.bank.domain.account.Account;
import com.example.bank.domain.transaction.Transaction;
import com.example.bank.domain.transaction.TransactionEnum;
import com.example.bank.domain.transaction.TransactionRepository;
import com.example.bank.dto.transaction.TransactionRespDto;
import com.example.bank.dto.transaction.TransactionRespDto.TransactionDto;
import com.example.bank.util.CustomDateUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record AccountRespDto(){
    public record AccountSaveRespDto(
            Long id,
            Long number,
            Long balance
    ){}

    @Builder
    public record AccountListRespDto(
            String fullname,
            List<AccountDto> accounts
    ){}

    @Builder
    public record AccountDto(
            Long id,
            Long number,
            Long balance
    ){}

    @Builder
    public record AccountDepositRespDto(
            Long id, // 계좌ID
            Long number, // 계좌번호
            TransactionDto transactionDto
    ){
        @Builder
        public record TransactionDto(
                Long id,
                String gubun,
                String sender,
                String reciver,
                Long amount,
                @JsonIgnore
                Long depositAccountBalance, // 클라이언트에게 전달X -> 서비스단에서 테스트 용도로 잔액확인
                String tel,
                String createdAt
        ){}
    }

    // DTO 내용이 똑같아도 재사용 X (변경점이 생겼을때 대응이 어려움 -> 독립적으로 만들어야함)
    @Builder
    public record AccountWithdrawRespDto(
            Long id, // 계좌ID
            Long number, // 계좌번호
            Long balance, //잔액
            TransactionDto transactionDto
    ){
        @Builder
        public record TransactionDto(
                Long id,
                String gubun,
                String sender,
                String reciver,
                Long amount,
                String createdAt
        ){}
    }

    @Builder
    public record AccountTransferRespDto(
            Long id, // 계좌ID
            Long number, // 계좌번호
            Long balance, //출금계좌 잔액
            TransactionDto transactionDto
    ){
        @Builder
        public record TransactionDto(
                Long id,
                String gubun,
                String sender,
                String reciver,
                Long amount,
                @JsonIgnore
                Long depositAccountBalance,
                String createdAt
        ){}
    }

    @Builder
    public record AccountDetailRespDto(
            Long id,
            Long number,
            Long balance,
            List<TransactionDto> transactions
    ){
        public AccountDetailRespDto(Account account, List<Transaction> transactions) {
            this(account.getId(),
                    account.getNumber(),
                    account.getBalance(),
                    transactions.stream()
                    .map((transaction) -> new TransactionDto(transaction, account.getNumber()))
                    .collect(Collectors.toList()));
        }
        @Builder
        public record TransactionDto(
                Long id,
                String gubun,
                Long amount,
                String sender,
                String receiver,
                String tel,
                String createdAt,
                Long balance
        ){
            @Builder
            public TransactionDto(Transaction transaction, Long accountNumber){
                this(transaction.getId(),
                        transaction.getGubun().getValue(),
                        transaction.getAmount(),
                        transaction.getSender(),
                        transaction.getReceiver(),
                        transaction.getTel() == null ? "없음" : transaction.getTel(),
                        CustomDateUtil.toStringFormat(transaction.getCreateAt()),
                        setBalance(transaction,accountNumber));
            }
            private static Long setBalance(Transaction transaction, Long accountNumber) {
                // balance 조정값
                Long setBalance;

                // 입금계좌가 null 이면 출금잔액을 조회, 출금계좌가 null이면 입금계좌를 조회
                if (transaction.getDepositAccount() == null) {
                    setBalance = transaction.getWithDrawAccountBalance();
                } else if (transaction.getWithdrawAccount() == null) {
                    setBalance = transaction.getDepositAccountBalance();
                } else {
                    // 입금계좌도 있고 출금계좌도 있는 경우 -> 이체 -> 요청받은 accountNumber와 비교후 해당 잔액을 balance로 사용
                    if (accountNumber.longValue() == transaction.getDepositAccount().getNumber()) {
                        setBalance = transaction.getDepositAccountBalance();
                    } else {
                        setBalance = transaction.getWithDrawAccountBalance();
                    }
                }
                return setBalance;
            }
        }
    }

}
