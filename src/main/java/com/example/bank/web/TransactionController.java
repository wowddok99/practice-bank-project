package com.example.bank.web;

import com.example.bank.config.auth.LoginUser;
import com.example.bank.dto.ResponseDto;
import com.example.bank.dto.transaction.TransactionRespDto.TransactionListRespDto;
import com.example.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/s/account/{number}/transaction")
    public ResponseEntity<?> findTransactionList(@PathVariable Long number,
                                                 @RequestParam(value = "gubun", defaultValue = "ALL") String gubun,
                                                 @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                 @AuthenticationPrincipal LoginUser loginUser) {
        TransactionListRespDto transactionListRespDto = transactionService.findDepositWithdrawalList(loginUser.getUser().getId(), number,
                gubun, page);
        // return new ResponseEntity<>(new ResponseDto<>(1, "입출금목록보기 성공",
        // transactionListRespDto), HttpStatus.OK);
        return ResponseEntity.ok().body(new ResponseDto<>(1, "입출금목록보기 성공", transactionListRespDto));
    }
}
