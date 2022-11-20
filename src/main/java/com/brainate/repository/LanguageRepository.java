package com.brainate.repository;

import com.brainate.domain.Account;
import com.brainate.domain.Language;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public interface LanguageRepository extends CrudRepository<Language, Long> {

    List<Language> findLanguagesByAccount(Account account);
    Language findLanguageById(Long id);

    @Query("DELETE FROM Language l WHERE l.id = :id")
    @Transactional
    @Modifying
    void deleteLanguageById(@Param("id") Long id);

    List<Language> findLanguagesByAccountNotAndHidden(Account account, boolean hidden);

}
