package com.brainate.repository;

import com.brainate.domain.Category;
import com.brainate.domain.WordConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface WordConfigRepository extends JpaRepository<WordConfig, Long> {

    List<WordConfig> findWordConfigsByCategoriesIn(List<Category> categories);
    List<WordConfig> findWordConfigsByAcceptedAndCategoriesIn(boolean accepted, List<Category> categories);
    List<WordConfig> findWordConfigsByTodayAndCategoriesIn(boolean today, List<Category> categories);
    List<WordConfig> findWordConfigsByThisUnitAndCategoriesIn(boolean thisUnit, List<Category> categories);
    WordConfig findWordConfigByIdAndCategories(Long id, List<Category> categories);
    WordConfig findWordConfigById(Long id);

    @Query(value = "DELETE FROM categorywordconfig WHERE id_category = :idCategory and id_wordconfig=:idWordConfig", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteWordConfigCategoriesLink(@Param("idCategory") Long idCategory, @Param("idWordConfig") Long idWordConfig);

    @Query(value = "DELETE FROM categorywordconfig WHERE id_wordconfig=:idWordConfig", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteWordConfigCategoriesLinks(@Param("idWordConfig") Long idWordConfig);

    @Query("DELETE FROM WordConfig WHERE id = :id")
    @Transactional
    @Modifying
    void deleteWordConfigById(@Param("id") Long id);

    @Query(value = "UPDATE wordconfigs SET thisunit=false WHERE id_wordconfig IN (SELECT cwc.id_wordconfig FROM categorywordconfig cwc WHERE cwc.id_category IN :categoriesId)", nativeQuery = true)
    @Transactional
    @Modifying
    void resetThisUnit(@Param("categoriesId") List<Long> categoriesId);

    @Query(value = "UPDATE wordconfigs SET today=false, done=0, processno=0 WHERE id_wordconfig IN (SELECT cwc.id_wordconfig FROM categorywordconfig cwc WHERE cwc.id_category IN :categoriesId)", nativeQuery = true)
    @Transactional
    @Modifying
    void resetWords(@Param("categoriesId") List<Long> categoriesId);

    @Query(value = "SELECT wc.* FROM words w, wordconfigs wc, categorywordconfig cwc, categories c \n" +
            "WHERE (wc.id_wordconfig = CASE WHEN :textWord~E'^[0-9]+$' THEN CAST(coalesce(:textWord, '0') AS integer) ELSE 0 END OR LOWER(w.word) LIKE concat('%', LOWER(:textWord), '%')) \n" +
            "AND w.id_wordconfig=wc.id_wordconfig AND wc.id_wordconfig=cwc.id_wordconfig \n" +
            "AND cwc.id_category=c.id_category AND c.id_category IN :categoriesId", nativeQuery = true)
    List<WordConfig> findWordConfigsByWordAndCategoriesIn(@Param("textWord") String textWord, @Param("categoriesId") List<Long> categoriesId);

    @Query(value = "UPDATE wordconfigs SET today=false, done = CASE WHEN done<>3 THEN 0 ELSE 3 END \n" +
            "WHERE id_wordconfig IN (SELECT cwc.id_wordconfig FROM categorywordconfig cwc WHERE cwc.id_category IN :categoriesId)", nativeQuery = true)
    @Transactional
    @Modifying
    void resetRepeating(@Param("categoriesId") List<Long> categoriesId);


    @Query(value = "SELECT wc.* FROM words w, wordconfigs wc, categorywordconfig cwc, categories c\n" +
            "WHERE wc.datetimedone IS NOT NULL AND (\n" +
            "(wc.processno = 1 AND CASE WHEN (SELECT CAST(coalesce(EXTRACT(epoch from age(now(), wc.datetimedone)) / 86400, '0') AS integer)) > 3 THEN true ELSE false END)\n" +
            "OR (wc.processno = 2 AND CASE WHEN (SELECT CAST(coalesce(EXTRACT(epoch from age(now(), wc.datetimedone)) / 86400, '0') AS integer)) > 5 THEN true ELSE false END)\n" +
            "OR (wc.processno = 3 AND CASE WHEN (SELECT CAST(coalesce(EXTRACT(epoch from age(now(), wc.datetimedone)) / 86400, '0') AS integer)) > 6 THEN true ELSE false END)\n" +
            "OR (wc.processno = 4 AND CASE WHEN (SELECT CAST(coalesce(EXTRACT(epoch from age(now(), wc.datetimedone)) / 86400, '0') AS integer)) > 7 THEN true ELSE false END)\n" +
            "OR (wc.processno = 5 AND CASE WHEN (SELECT CAST(coalesce(EXTRACT(epoch from age(now(), wc.datetimedone)) / 86400, '0') AS integer)) > 10 THEN true ELSE false END)\n" +
            "OR (wc.processno = 6 AND CASE WHEN (SELECT CAST(coalesce(EXTRACT(epoch from age(now(), wc.datetimedone)) / 86400, '0') AS integer)) > 11 THEN true ELSE false END)\n" +
            "OR (wc.processno = 7 AND CASE WHEN (SELECT CAST(coalesce(EXTRACT(epoch from age(now(), wc.datetimedone)) / 86400, '0') AS integer)) > 12 THEN true ELSE false END)\n" +
            "OR (wc.processno = 8 AND CASE WHEN (SELECT CAST(coalesce(EXTRACT(epoch from age(now(), wc.datetimedone)) / 86400, '0') AS integer)) > 13 THEN true ELSE false END)\n" +
            "OR (wc.processno = 9 AND CASE WHEN (SELECT CAST(coalesce(EXTRACT(epoch from age(now(), wc.datetimedone)) / 86400, '0') AS integer)) > 14 THEN true ELSE false END)\n" +
            "OR (wc.processno = 10 AND CASE WHEN (SELECT CAST(coalesce(EXTRACT(epoch from age(now(), wc.datetimedone)) / 86400, '0') AS integer)) > 15 THEN true ELSE false END)\n" +
            "OR (wc.processno = 11 AND CASE WHEN (SELECT CAST(coalesce(EXTRACT(epoch from age(now(), wc.datetimedone)) / 86400, '0') AS integer)) > 16 THEN true ELSE false END)\n" +
            ")\n" +
            "AND w.id_wordconfig=wc.id_wordconfig AND wc.id_wordconfig=cwc.id_wordconfig\n" +
            "AND cwc.id_category=c.id_category AND c.id_category IN :categoriesId ORDER BY random() LIMIT :limitWords", nativeQuery = true)
    List<WordConfig> findWordConfigsByCategoriesIdInIncludingDateTimeDone(@Param("categoriesId") List<Long> categoriesId, @Param("limitWords") int limitWords);


    @Query(value = "SELECT wc.* FROM words w, wordconfigs wc, categorywordconfig cwc, categories c \n" +
            "WHERE wc.accepted = :accepted AND wc.today = :today AND wc.done = :done \n" +
            "AND w.id_wordconfig=wc.id_wordconfig AND wc.id_wordconfig=cwc.id_wordconfig \n" +
            "AND cwc.id_category=c.id_category AND c.id_category IN :categoriesId ORDER BY random() LIMIT :limitWords", nativeQuery = true)
    List<WordConfig> findWordConfigsByAcceptedAndTodayAndDoneAndCategoriesIn(@Param("accepted") boolean accepted,
                                                                             @Param("today") boolean today,
                                                                             @Param("done") Integer done,
                                                                             @Param("categoriesId") List<Long> categoriesId,
                                                                             @Param("limitWords") int limitWords);

    @Query(value = "SELECT wc.* FROM categorywordconfig cwc, categories c, languages l, accounts a, wordconfigs wc \n" +
            "WHERE wc.id_wordconfig=cwc.id_wordconfig AND accepted=:accepted AND a.id_account<>:accountId \n" +
            "AND cwc.id_category=c.id_category AND c.id_language=l.id_language \n" +
            "AND l.id_account=a.id_account AND c.id_category=:categoryId AND l.id_language=:languageId \n" +
            "AND wc.id_wordconfig NOT IN (SELECT id_wordconfig FROM hiddenwords WHERE id_account=:accountId)", nativeQuery = true)
    List<WordConfig> findWordConfigsExceptHiddenWords(@Param("languageId") Long languageId,
                                                      @Param("categoryId") Long categoryId,
                                                      @Param("accountId") Long accountId,
                                                      @Param("accepted") boolean accepted);

}
