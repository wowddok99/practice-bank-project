//package com.example.bank.dto.transaction;
//
//import com.example.bank.domain.account.Account;
//import com.example.bank.domain.transaction.Transaction;
//import com.example.bank.util.CustomDateUtil;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//
//public class TransactionRespDto {
//    @Setter
//    @Getter
//    public static class TransactionListRespDto {
//        private List<TransactionDto> transactions = new ArrayList<>();
//
//        public TransactionListRespDto(List<Transaction> transactions, Account account) {
//            this.transactions = transactions.stream()
//                    .map((transaction) -> new TransactionDto(transaction, account.getNumber()))
//                    .collect(Collectors.toList());
//
//        }
//
//        @Setter
//        @Getter
//        public class TransactionDto {
//            private Long id;
//            private String gubun;
//            private Long amount;
//            private String sender;
//            private String reciver;
//            private String tel;
//            private String createdAt;
//            private Long balance;
//
//            public TransactionDto(Transaction transaction, Long accountNumber) {
//                this.id = transaction.getId();
//                this.gubun = transaction.getGubun().getValue();
//                this.amount = transaction.getAmount();
//                this.sender = transaction.getSender();
//                this.reciver = transaction.getReceiver();
//                this.createdAt = CustomDateUtil.toStringFormat(transaction.getCreateAt());
//                this.tel = transaction.getTel() == null ? "없음" : transaction.getTel();
//
//                // 1111 계좌의 입출금 내역 (출금계좌 = null, 입금계좌 = 값) (출금계좌 = 값, 입금계좌 = null)
//                if (transaction.getDepositAccount() == null) {
//                    this.balance = transaction.getWithDrawAccountBalance();
//                } else if (transaction.getWithdrawAccount() == null) {
//                    this.balance = transaction.getDepositAccountBalance();
//                } else {
//                    // 1111 계좌의 입출금 내역 (출금계좌 = 값, 입금계좌 = 값)
//                    if (accountNumber.longValue() == transaction.getDepositAccount().getNumber()) {
//                        this.balance = transaction.getDepositAccountBalance();
//                    } else {
//                        this.balance = transaction.getWithDrawAccountBalance();
//                    }
//                }
//            }
//        }
//    }
//}

package com.example.bank.dto.transaction;

import com.example.bank.domain.account.Account;
import com.example.bank.domain.transaction.Transaction;
import com.example.bank.util.CustomDateUtil;

import java.util.List;
import java.util.stream.Collectors;

public record TransactionRespDto() {
    public record TransactionListRespDto(
            List<TransactionDto> transactions
    ) {
        public TransactionListRespDto(List<Transaction> transactions, Account account) {
            this(transactions.stream()
                    .map((transaction) -> new TransactionDto(transaction, account.getNumber()))
                    .collect(Collectors.toList()));
        }
    }

    public record TransactionDto(
            Long id,
            String gubun,
            Long amount,
            String sender,
            String receiver,
            String tel,
            String createdAt,
            Long balance
    ) {
        public TransactionDto(Transaction transaction, Long accountNumber) {
            this(transaction.getId(),
                    transaction.getGubun().getValue(),
                    transaction.getAmount(),
                    transaction.getSender(),
                    transaction.getReceiver(),
                    transaction.getTel() == null ? "없음" : transaction.getTel(),
                    CustomDateUtil.toStringFormat(transaction.getCreateAt()),
                    setBalance(transaction,accountNumber));
        }

        private static Long setBalance(Transaction transaction, Long accountNumber){
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