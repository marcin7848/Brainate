package com.brainate.controller;

import com.brainate.ErrorMessage;
import com.brainate.domain.*;
import com.brainate.service.AccountService;
import com.brainate.service.CategoryService;
import com.brainate.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/languages")
@CrossOrigin("http://localhost:4200")
public class LanguageController {

    private final LanguageService languageService;
    private final AccountService accountService;
    private final CategoryService categoryService;

    @Autowired
    public LanguageController(LanguageService languageService, AccountService accountService, CategoryService categoryService) {
        this.languageService = languageService;
        this.accountService = accountService;
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@Validated(Language.LanguageValidation.class) @RequestBody Language language,
                                 BindingResult result) {

        language.setAccount(accountService.getLoggedAccount());
        if (result.hasErrors()) {
            return ErrorMessage.send(result, HttpStatus.BAD_REQUEST);
        }

        Language language1 = languageService.addLanguage(language);
        if (language1 == null) {
            return ErrorMessage.send("The language has not created! Error!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(language1, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllLanguages() {
        List<Language> languages = languageService.getLanguagesOfAccount();
        return new ResponseEntity<>(languages, HttpStatus.OK);
    }

    @GetMapping("/{id}/get")
    public ResponseEntity<?> getLanguage(@PathVariable("id") Long id) {
        Language language = languageService.getLanguageById(id);
        if (language == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        language.getCategorySettings().sort(Comparator.comparing(CategorySetting::getId));
        language.setAccount(null);

        return new ResponseEntity<>(language, HttpStatus.OK);
    }

    @PatchMapping("/{id}/edit")
    public ResponseEntity<?> editLanguage(@PathVariable("id") Long id,
                                          @Validated(Language.LanguageValidation.class) @RequestBody Language language,
                                          BindingResult result) {

        Language oldLanguage = languageService.getLanguageByIdWithAllWords(id);

        if (oldLanguage == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        if (!languageService.checkLanguageBelongingToAccount(oldLanguage)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        if (result.hasErrors()) {
            return ErrorMessage.send(result, HttpStatus.BAD_REQUEST);
        }

        Language language1 = languageService.updateLanguage(language, oldLanguage);
        if (language1 == null) {
            return ErrorMessage.send("The language has not updated! Error!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(language1, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteLanguage(@PathVariable("id") Long id) {
        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (language == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        languageService.deleteLanguage(language);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PatchMapping("/{id}/categorySettings/{id_category_setting}/edit")
    public ResponseEntity<?> editCategorySettings(@PathVariable("id") Long id,
                                                  @PathVariable("id_category_setting") Long id_category_setting,
                                                  @Validated(CategorySetting.CategorySettingValidation.class) @RequestBody CategorySetting categorySetting,
                                                  BindingResult result) {

        if (!languageService.checkLanguageBelongingToAccount(id)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        if (result.hasErrors()) {
            return ErrorMessage.send(result, HttpStatus.BAD_REQUEST);
        }

        CategorySetting categorySetting1 = languageService.editCategorySetting(id, id_category_setting, categorySetting);
        if (categorySetting1 == null) {
            return ErrorMessage.send("The category setting has not updated! Error!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(categorySetting1, HttpStatus.OK);
    }

    @PatchMapping("/{id}/resetThisUnit")
    public ResponseEntity<?> resetThisUnit(@PathVariable("id") Long id,
                                           @RequestBody List<Category> categories) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        languageService.resetThisUnit(language, categories);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PatchMapping("/{id}/resetWords")
    public ResponseEntity<?> resetWords(@PathVariable("id") Long id,
                                           @RequestBody List<Category> categories) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        languageService.resetWords(language, categories);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/{id}/startRepeating/{numberOfWords}")
    public ResponseEntity<?> startRepeating(@PathVariable("id") Long id,
                                        @PathVariable("numberOfWords") int numberOfWords,
                                        @RequestBody List<Category> categories) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        if (numberOfWords <= 0) {
            return ErrorMessage.send("You have to provide number of words greater than 0!", HttpStatus.BAD_REQUEST);
        }

        languageService.startRepeating(language, categories, numberOfWords);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/{id}/getTodayWords")
    public ResponseEntity<?> getLanguageWithTodayWords(@PathVariable("id") Long id) {
        Language language = languageService.getLanguageWithTodayWords(id);
        if (language == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        language.getCategorySettings().sort(Comparator.comparing(CategorySetting::getId));
        language.setAccount(null);

        return new ResponseEntity<>(language, HttpStatus.OK);
    }

    @PatchMapping("/{id}/resetRepeating")
    public ResponseEntity<?> resetRepeating(@PathVariable("id") Long id) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        languageService.resetRepeating(language);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PatchMapping("/{id}/categories/{idCategory}/changeDone")
    public ResponseEntity<?> changeDone(@PathVariable("id") Long id,
                                        @PathVariable("idCategory") Long idCategory,
                                        @RequestBody WordConfig wordConfig) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.getCategoryByIdAndLanguage(idCategory, language);
        if (category == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        WordConfig wordConfigOld = categoryService.getWordConfigByIdAndCategory(wordConfig.getId(), category);
        if(wordConfigOld == null){
            return ErrorMessage.send("There is no such word.", HttpStatus.BAD_REQUEST);
        }

        WordConfig wordConfig1 = languageService.changeDone(wordConfig, wordConfigOld, category, language);
        if (wordConfig1 == null) {
            return ErrorMessage.send("The word config has not updated! Error!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PatchMapping("/{id}/editTasks")
    public ResponseEntity<?> editTasks(@PathVariable("id") Long id,
                                       @RequestBody List<Task> tasks) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        Iterable<Task> tasks1 = languageService.editTasks(tasks, language);

        return new ResponseEntity<>(tasks1, HttpStatus.OK);
    }

    @GetMapping("/reloadTasks")
    public ResponseEntity<?> reloadTasks() {
        languageService.reloadTasks();
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
