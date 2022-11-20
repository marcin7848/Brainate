package com.brainate.service;

import com.brainate.domain.Account;
import com.brainate.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.security.Principal;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    AccountRepository accountRepository;

    public Account getLoggedAccount() {
        Account account = accountRepository.findAccountByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if(account == null){
            return null;
        }

        return account;

    }

    public Account addNewAccount(Account account){
        if(!accountRepository.findAccountsByUsernameOrEmail(account.getUsername(), account.getEmail()).isEmpty()){
            return null;
        }
        account.setPassword(this.hashPassword(account.getPassword()));
        return accountRepository.save(account);
    }

    private String hashPassword(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    public Iterable<Account> getAllAccountsStatistics() {
        Iterable<Account> accounts = accountRepository.findAll();
        accounts.forEach(account -> {
            account.setPassword("");
            account.setEmail("");
            account.setId(0L);
            account.setHiddenWords(null);
            account.getLanguages().forEach(language -> {
                language.setCategorySettings(null);
                language.setCategories(null);
            });
        });
        return accounts;
    }

    public Iterable<Account> getAllAccountsForReloadTasks() {
        return accountRepository.findAll();
    }
}
