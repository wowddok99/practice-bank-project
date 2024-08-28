package com.example.bank.service;

import com.example.bank.domain.account.Account;
import com.example.bank.domain.account.AccountRepository;
import com.example.bank.domain.transaction.Transaction;
import com.example.bank.domain.transaction.TransactionEnum;
import com.example.bank.domain.transaction.TransactionRepository;
import com.example.bank.domain.user.User;
import com.example.bank.domain.user.UserRepository;
import com.example.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import com.example.bank.dto.account.AccountReqDto.AccountSaveReqDto;
import com.example.bank.dto.account.AccountReqDto.AccountTransferReqDto;
import com.example.bank.dto.account.AccountReqDto.AccountWithdrawReqDto;
import com.example.bank.dto.account.AccountRespDto;
import com.example.bank.dto.account.AccountRespDto.AccountDepositRespDto;
import com.example.bank.dto.account.AccountRespDto.AccountDepositRespDto.TransactionDto;
import com.example.bank.dto.account.AccountRespDto.AccountDetailRespDto;
import com.example.bank.dto.account.AccountRespDto.AccountDto;
import com.example.bank.dto.account.AccountRespDto.AccountListRespDto;
import com.example.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import com.example.bank.dto.account.AccountRespDto.AccountTransferRespDto;
import com.example.bank.dto.account.AccountRespDto.AccountWithdrawRespDto;
import com.example.bank.handler.ex.CustomApiException;
import com.example.bank.util.CustomDateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private Account account;

    public AccountListRespDto getAccountListByUser(Long userId){
        //계좌 확인
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다"));
        // userId로 유저의 모든 계좌목록(정보) 조회
        List<Account> accountListPS = accountRepository.findByUser_id(userId);
        return new AccountListRespDto(
                userPS.getFullname(),
                accountListPS.stream()
                        .map(account -> new AccountDto(account.getId(), account.getNumber(), account.getBalance()))
                        .collect(Collectors.toList())
        );
    }

    @Transactional //DB의 변경이 진행되므로 @Transactional 처리
    public AccountSaveRespDto accountRegister(AccountSaveReqDto accountSaveReqDto, Long userId){
        // User가 DB에 있는지 검증
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new CustomApiException("유저를 찾을 수 없습니다.")
        );

        // 해당 계좌가 DB에 있는지 중복여부를 체크
        Optional<Account> accountOP = accountRepository.findByNumber(accountSaveReqDto.number());
        if(accountOP.isPresent()){
            throw new CustomApiException("해당 계좌가 이미 존재합니다");
        }

        // 계좌 등록(Dto -> Entity 변환)
        Account accountPS = accountRepository.save(accountSaveReqDto.toEntity(userPS));

        // DTO를 응답
        return new AccountSaveRespDto(accountPS.getId(),accountPS.getNumber(),accountPS.getBalance());
    }
    @Transactional
    public void deleteAccount(Long number, Long userId){
        // 1. 계좌 확인
        Account accountPS = accountRepository.findByNumber(number).orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다"));

        // 2. 계좌 소유자 확인
        accountPS.checkOwner(userId);

        // 3. 계좌 삭제
        accountRepository.deleteById(accountPS.getId());
    }

    // 인증이 필요 없음
    @Transactional
    public AccountDepositRespDto accountDeposit(AccountDepositReqDto accountDepositReqDto){ // ATM -> 누군가의 계좌
        // 0원 체크 -> DTO 자체에서도 필터링 가능
        if(accountDepositReqDto.amount()<=0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }
        // 입급계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(accountDepositReqDto.number()).
                orElseThrow(
                () -> new CustomApiException("계좌를 찾을 수 없습니다"));

        // 입금(해당 계좌 balance 조정 ->  update문 -> 더티체킹)

        depositAccountPS.deposit(accountDepositReqDto.amount());

        // 거래내역 남기기
        Transaction transaction = Transaction.builder()
                .withdrawAccount(null)
                .depositAccount(depositAccountPS)
                .withDrawAccountBalance(null)
                .depositAccountBalance(depositAccountPS.getBalance())
                .amount(accountDepositReqDto.amount())
                .gubun(TransactionEnum.DEPOSIT)
                .sender("ATM")
                .receiver(accountDepositReqDto.number()+"")
                .tel(accountDepositReqDto.tel())
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);

        return new AccountDepositRespDto(
                depositAccountPS.getId(),
                depositAccountPS.getNumber(),
                new TransactionDto(transactionPS.getId(),
                                   transactionPS.getGubun().value,
                                   transactionPS.getSender(),
                                   transactionPS.getReceiver(),
                                   transactionPS.getAmount(),
                                   transactionPS.getDepositAccountBalance(),
                                   transactionPS.getTel(),
                                   CustomDateUtil.toStringFormat(transactionPS.getCreateAt())
                ));
    }

    @Transactional
    public AccountWithdrawRespDto accountWithdraw(AccountWithdrawReqDto accountWithdrawReqDto, Long userId){
        // 0원 체크
        if(accountWithdrawReqDto.amount()<=0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }

        // 출금계좌 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountWithdrawReqDto.number()).
                orElseThrow(
                        () -> new CustomApiException("계좌를 찾을 수 없습니다"));

        // 출금 소유자 확인(로그인한 사람과 동일한지)
        withdrawAccountPS.checkOwner(userId);

        // 출금계좌 비밀번호 확인
        withdrawAccountPS.checkSamePassword(accountWithdrawReqDto.password());

        // 출금계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountWithdrawReqDto.amount());

        // 출금하기
        withdrawAccountPS.withdraw(accountWithdrawReqDto.amount());

        // 거래내역 남기기
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(null)
                .withDrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(null)
                .amount(accountWithdrawReqDto.amount())
                .gubun(TransactionEnum.WITHDRAW)
                .sender(accountWithdrawReqDto.number()+"")
                .receiver("ATM")
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);


        // DTO 응답
        return new AccountWithdrawRespDto(
                withdrawAccountPS.getId(),
                withdrawAccountPS.getNumber(),
                withdrawAccountPS.getBalance(),
                new AccountWithdrawRespDto.TransactionDto(transactionPS.getId(),
                        transactionPS.getGubun().value,
                        transactionPS.getSender(),
                        transactionPS.getReceiver(),
                        transactionPS.getAmount(),
                        CustomDateUtil.toStringFormat(transactionPS.getCreateAt())
                ));
    }

    @Transactional
    public AccountTransferRespDto accountTransfer(AccountTransferReqDto accountTransferReqDto, Long userId){

        // 출금계좌와 입금계좌가 동일하면 안됨
        if(accountTransferReqDto.withdrawNumber() == accountTransferReqDto.depositNumber()){
            throw new CustomApiException("입출금계좌가 동일할 수 없습니다");
        }
        // 0원 체크
        if(accountTransferReqDto.amount()<=0L){
            throw new CustomApiException("0원 이하의 금액을 입금할 수 없습니다.");
        }

        // 출금계좌 확인
        Account withdrawAccountPS = accountRepository.findByNumber(accountTransferReqDto.withdrawNumber()).
                orElseThrow(
                        () -> new CustomApiException("출금계좌를 찾을 수 없습니다"));

        // 입금계좌 확인
        Account depositAccountPS = accountRepository.findByNumber(accountTransferReqDto.depositNumber()).
                orElseThrow(
                        () -> new CustomApiException("입금계좌를 찾을 수 없습니다"));

        // 출금 소유자 확인(로그인한 사람과 동일한지)
        withdrawAccountPS.checkOwner(userId);

        // 출금계좌 비밀번호 확인
        withdrawAccountPS.checkSamePassword(accountTransferReqDto.withdrawPassword());

        // 출금계좌 잔액 확인
        withdrawAccountPS.checkBalance(accountTransferReqDto.amount());

        // 이체하기
        withdrawAccountPS.withdraw(accountTransferReqDto.amount());
        depositAccountPS.deposit(accountTransferReqDto.amount());

        // 거래내역 남기기
        Transaction transaction = Transaction.builder()
                .withdrawAccount(withdrawAccountPS)
                .depositAccount(depositAccountPS)
                .withDrawAccountBalance(withdrawAccountPS.getBalance())
                .depositAccountBalance(depositAccountPS.getBalance())
                .amount(accountTransferReqDto.amount())
                .gubun(TransactionEnum.TRANSFER)
                .sender(accountTransferReqDto.withdrawNumber()+"")
                .receiver(accountTransferReqDto.depositNumber()+"")
                .build();

        Transaction transactionPS = transactionRepository.save(transaction);


        // DTO 응답
        return new AccountTransferRespDto(
                withdrawAccountPS.getId(),
                withdrawAccountPS.getNumber(),
                withdrawAccountPS.getBalance(),
                new AccountTransferRespDto.TransactionDto(transactionPS.getId(),
                        transactionPS.getGubun().value,
                        transactionPS.getSender(),
                        transactionPS.getReceiver(),
                        transactionPS.getAmount(),
                        transactionPS.getDepositAccountBalance(),
                        CustomDateUtil.toStringFormat(transactionPS.getCreateAt())
                ));
    }

    public AccountDetailRespDto findDetailAccount(Long number, Long userId, Integer page){
        // 1. 구분값 고정
        String gubun = "ALL";

        // 2. 출금계좌 확인
        Account accountPS = accountRepository.findByNumber(number).
                orElseThrow(() -> new CustomApiException("계좌를 찾을 수 없습니다"));

        // 3. 계좌소유자 확인
        accountPS.checkOwner(userId);

        // 4. 입출금목록보기
        List<Transaction> transactionList = transactionRepository.findTransactionList(accountPS.getId(), gubun, page);
        return new AccountDetailRespDto(accountPS, transactionList);
    }
}
