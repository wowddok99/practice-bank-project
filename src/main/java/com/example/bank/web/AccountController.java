package com.example.bank.web;

import com.example.bank.config.auth.LoginUser;
import com.example.bank.dto.ResponseDto;
import com.example.bank.dto.account.AccountReqDto;
import com.example.bank.dto.account.AccountReqDto.AccountDepositReqDto;
import com.example.bank.dto.account.AccountRespDto.AccountDepositRespDto;
import com.example.bank.dto.account.AccountRespDto.AccountDetailRespDto;
import com.example.bank.dto.account.AccountRespDto.AccountListRespDto;
import com.example.bank.dto.account.AccountRespDto.AccountSaveRespDto;
import com.example.bank.dto.account.AccountRespDto.AccountTransferRespDto;
import com.example.bank.dto.account.AccountRespDto.AccountWithdrawRespDto;
import com.example.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/s/account")
    public ResponseEntity<?> saveAccount(@RequestBody @Valid AccountReqDto.AccountSaveReqDto accountSaveReqDto,
                                         BindingResult bindingResult, @AuthenticationPrincipal LoginUser loginUser){
        AccountSaveRespDto accountSaveRespDto = accountService.accountRegister(accountSaveReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1,"계좌등록 성공",accountSaveRespDto), HttpStatus.CREATED);
    }

    // 인증이 필요하고, account 테이블에 login한 유저의 계좌만 주세요.
    @GetMapping("/s/account/login-user")
    public ResponseEntity<?> findUserAccount (@AuthenticationPrincipal LoginUser loginUser){
        AccountListRespDto accountListRespDto = accountService.getAccountListByUser(loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1,"계좌목록보기_유저별 성공",accountListRespDto),HttpStatus.OK);
    }

    @DeleteMapping("/s/account/{number}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long number, @AuthenticationPrincipal LoginUser loginUser){
        accountService.deleteAccount(number, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1,"계좌 삭제 완료",null),HttpStatus.OK);
    }

    @PostMapping("/account/deposit")
    public ResponseEntity<?> depositAccount(@RequestBody @Valid AccountDepositReqDto accountDepositReqDto,
                                            BindingResult bindingResult){
        AccountDepositRespDto accountDepositRespDto = accountService.accountDeposit(accountDepositReqDto);
        return new ResponseEntity<>(new ResponseDto<>(1,"계좌 입금 완료",accountDepositRespDto),HttpStatus.CREATED);
    }

    @PostMapping("/s/account/withdraw")
    public ResponseEntity<?> withdrawAccount(@RequestBody @Valid AccountReqDto.AccountWithdrawReqDto accountWithdrawReqDto,
                                            BindingResult bindingResult,
                                             @AuthenticationPrincipal LoginUser loginUser){
        AccountWithdrawRespDto accountWithdrawRespDto = accountService.accountWithdraw(accountWithdrawReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1,"계좌 출금 완료",accountWithdrawRespDto),HttpStatus.CREATED);
    }

    @PostMapping("/s/account/transfer")
    public ResponseEntity<?> transferAccount(@RequestBody @Valid AccountReqDto.AccountTransferReqDto accountTransferReqDto,
                                             BindingResult bindingResult,
                                             @AuthenticationPrincipal LoginUser loginUser){
        AccountTransferRespDto accountTransferRespDto = accountService.accountTransfer(accountTransferReqDto, loginUser.getUser().getId());
        return new ResponseEntity<>(new ResponseDto<>(1,"계좌 출금 완료",accountTransferRespDto),HttpStatus.CREATED);
    }

    @GetMapping("/s/account/{number}")
    public ResponseEntity<?> findDetailAccount(@PathVariable Long number,
                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                               @AuthenticationPrincipal LoginUser loginUser) {
        AccountDetailRespDto accountDetailRespDto = accountService.findDetailAccount(number, loginUser.getUser().getId(), page);
        return new ResponseEntity<>(new ResponseDto<>(1, "계좌상세보기 성공", accountDetailRespDto), HttpStatus.OK);
    }
}
