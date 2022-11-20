package com.brainate.repository;

import com.brainate.domain.Category;
import com.brainate.domain.Language;
import com.brainate.domain.WordConfig;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

    Category findCategoryById(Long id);
    Category findCategoryByIdAndLanguage(Long id, Language language);
    List<Category> findCategoriesByWordConfigs(List<WordConfig> justOneWordConfig);
    List<Category> findCategoriesByLanguage(Language language);

    @Query("DELETE FROM Category WHERE id = :id")
    @Transactional
    @Modifying
    void deleteCategoryById(@Param("id") Long id);

    @Query(value = "INSERT INTO categorywordconfig VALUES(:idCategory, :idWordConfig)", nativeQuery = true)
    @Transactional
    @Modifying
    void insertIntoCategoryWordConfig(@Param("idCategory") Long idCategory, @Param("idWordConfig") long idWordConfig);

    @Query(value = "SELECT * FROM categorywordconfig where id_category=:idCategory AND id_wordconfig=:idWordConfig", nativeQuery = true)
    boolean checkCategoryWordConfigExists(@Param("idCategory") Long idCategory, @Param("idWordConfig") Long idWordConfig);
}
