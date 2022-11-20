package com.brainate.repository;

import com.brainate.domain.CategorySetting;
import com.brainate.domain.Language;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorySettingRepository extends CrudRepository<CategorySetting, Long> {

    CategorySetting findCategorySettingByIdAndLanguage(Long id, Language language);

}
