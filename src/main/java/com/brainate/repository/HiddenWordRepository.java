package com.brainate.repository;

import com.brainate.domain.HiddenWord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HiddenWordRepository extends CrudRepository<HiddenWord, Long> {

}
