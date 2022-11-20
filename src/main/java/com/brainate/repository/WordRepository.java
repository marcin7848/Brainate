package com.brainate.repository;

import com.brainate.domain.Word;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface WordRepository extends CrudRepository<Word, Long> {

    @Query("DELETE FROM Word WHERE id = :id")
    @Transactional
    @Modifying
    void deleteWordById(@Param("id") Long id);

    @Query(value = "SELECT * FROM words w, wordconfigs wc, categories c, categorywordconfig cwc " +
            "WHERE w.word = :word AND w.answer = :answer AND c.mode = :mode " +
            "AND w.id_wordconfig = wc.id_wordconfig AND cwc.id_wordconfig = wc.id_wordconfig AND cwc.id_category = c.id_category LIMIT 1",
            nativeQuery = true)
    Word findWordByWordAndAnswerAndMode(@Param("word") String word, @Param("answer") boolean answer, @Param("mode") String mode);

    @Query(value = "SELECT * FROM words w, wordconfigs wc, categories c, categorywordconfig cwc \n" +
            "WHERE lower(w.word) IN :words AND w.answer = :answer AND c.mode = :mode \n" +
            "AND w.id_wordconfig = wc.id_wordconfig AND cwc.id_wordconfig = wc.id_wordconfig AND cwc.id_category = c.id_category\n" +
            "AND c.id_category IN :categoriesId",
            nativeQuery = true)
    List<Word> findWordByWordsInAndCategoriesInAndAnswerAndMode(@Param("words") List<String> words, @Param("categoriesId") List<Long> categoriesId, @Param("answer") boolean answer, @Param("mode") String mode);

    @Query(value = "SELECT * FROM words w, wordconfigs wc, categories c, categorywordconfig cwc, languages l, accounts a \n" +
            "WHERE w.word = :word \n" +
            "AND w.id_wordconfig = wc.id_wordconfig AND cwc.id_wordconfig = wc.id_wordconfig AND cwc.id_category = c.id_category \n" +
            "AND c.id_language = l.id_language AND l.id_account = a.id_account AND a.id_account = :idAccount LIMIT 1",
            nativeQuery = true)
    Word findWordByWordAndIdAccount(@Param("word") String word, @Param("idAccount") Long idAccount);

}
