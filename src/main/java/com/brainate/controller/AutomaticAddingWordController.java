package com.brainate.controller;

import com.brainate.ErrorMessage;
import com.brainate.domain.*;
import com.brainate.service.AccountService;
import com.brainate.service.AutomaticAddingWordService;
import com.brainate.service.CategoryService;
import com.brainate.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/languages/{id}/categories/{idCategory}/automatic")
@CrossOrigin("http://localhost:4200")
public class AutomaticAddingWordController {

    @Autowired
    AccountService accountService;

    @Autowired
    LanguageService languageService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    AutomaticAddingWordService automaticAddingWordService;

    @PostMapping("/lemma")
    public ResponseEntity<?> lemmatizationText(@PathVariable("id") Long id,
                                               @PathVariable("idCategory") Long idCategory,
                                               @RequestBody LemmaWord lemmaWord) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.getCategoryByIdAndLanguage(idCategory, language);
        if (category == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        if (category.getMode() != Mode.DICTIONARY) {
            return ErrorMessage.send("Auto translate works only for dictionary type.", HttpStatus.BAD_REQUEST);
        }

        List<WordConfig> lemmaWords = automaticAddingWordService.addWordsAutomatically(language.getDictionaryLanguage().getCodeForTranslator(), lemmaWord.getWord(), language, category);
        return new ResponseEntity<>(lemmaWords, HttpStatus.OK);
    }


    @GetMapping("/getLanguageAndCategory")
    public ResponseEntity<?> getLanguageAndCategory(@PathVariable("id") Long id,
                                                    @PathVariable("idCategory") Long idCategory) {

        Language language = languageService.getLanguageById(id);
        if (language == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.getCategoryByIdAndLanguage(idCategory, language);
        if (category == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        language.getCategories().clear();
        language.getCategories().add(category);
        language.setAccount(null);

        return new ResponseEntity<>(language, HttpStatus.OK);
    }

    @GetMapping("/getAllLanguages")
    public ResponseEntity<?> getAllLanguages(@PathVariable("id") Long id,
                                             @PathVariable("idCategory") Long idCategory) {

        Language language = languageService.getLanguageById(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.getCategoryByIdAndLanguage(idCategory, language);
        if (category == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        List<Language> languages = automaticAddingWordService.getAllLanguages();
        languages.forEach(l -> {
            l.setAccount(null);
            l.setCategorySettings(null);
        });
        return new ResponseEntity<>(languages, HttpStatus.OK);
    }

    @PostMapping("/hide")
    public ResponseEntity<?> hideWord(@RequestBody HiddenWord hiddenWord) {

        Account account = accountService.getLoggedAccount();
        if (account == null) {
            return ErrorMessage.send("You have to log in!", HttpStatus.BAD_REQUEST);
        }
        hiddenWord.setAccount(account);
        automaticAddingWordService.hideWord(hiddenWord);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> copyWord(@PathVariable("id") Long id,
                                      @PathVariable("idCategory") Long idCategory,
                                      @RequestBody List<HiddenWord> hiddenWords) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.getCategoryByIdAndLanguage(idCategory, language);
        if (category == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        List<HiddenWord> hiddenWords1 = automaticAddingWordService.copyWords(language, category, hiddenWords);
        if (hiddenWords1 == null) {
            return ErrorMessage.send("Something went wrong!", HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(hiddenWords1, HttpStatus.OK);
    }

}
