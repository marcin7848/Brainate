package com.brainate.repository;

import com.brainate.domain.DictionaryLanguage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictionaryLanguageRepository extends CrudRepository<DictionaryLanguage, Long> {

}
