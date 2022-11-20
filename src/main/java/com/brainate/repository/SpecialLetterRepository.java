package com.brainate.repository;

import com.brainate.domain.SpecialLetter;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SpecialLetterRepository extends CrudRepository<SpecialLetter, Long> {

    @Query("DELETE FROM SpecialLetter sl WHERE sl.id = :id")
    @Transactional
    @Modifying
    void deleteSpecialLetterById(@Param("id") Long id);
}
