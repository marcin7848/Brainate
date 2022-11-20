package com.brainate.controller;

import com.brainate.ErrorMessage;
import com.brainate.domain.Account;
import com.brainate.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin("http://localhost:4200")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/getLoggedAccount")
    public ResponseEntity<?> getLoggedUser(){
        Account account = accountService.getLoggedAccount();
        if(account == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        account.getLanguages().forEach(l -> l.setCategories(null));

        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated(Account.AccountValidation.class) @RequestBody Account account,
                                      BindingResult result) {

        /*
        if (result.hasErrors()) {
            return ErrorMessage.send(result, HttpStatus.BAD_REQUEST);
        }

        Account account1 = accountService.addNewAccount(account);
        if(account1 == null){
            return ErrorMessage.send("The account with the given data exists!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(account1, HttpStatus.OK);
         */
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/getAllAccountsStatistics")
    public ResponseEntity<?> getAllAccountsStatistics(){
        return new ResponseEntity<>(accountService.getAllAccountsStatistics(), HttpStatus.OK);
    }
}
