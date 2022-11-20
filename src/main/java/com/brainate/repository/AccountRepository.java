package com.brainate.repository;

import com.brainate.domain.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    List<Account> findAccountsByUsernameOrEmail(String username, String email);
    Account findAccountByUsername(String username);

}
