package com.brainate.controller;

import com.brainate.ErrorMessage;
import com.brainate.domain.Category;
import com.brainate.domain.Language;
import com.brainate.domain.WordConfig;
import com.brainate.service.CategoryService;
import com.brainate.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/languages/{id}/categories")
@CrossOrigin("http://localhost:4200")
public class CategoryController {

    @Autowired
    LanguageService languageService;

    @Autowired
    CategoryService categoryService;

    @PostMapping("/add")
    public ResponseEntity<?> addNewCategory(@PathVariable("id") Long id,
                                            @Validated(Category.CategoryValidation.class) @RequestBody Category category,
                                            BindingResult result) {

        if (!languageService.checkLanguageBelongingToAccount(id)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        if (result.hasErrors()) {
            return ErrorMessage.send(result, HttpStatus.BAD_REQUEST);
        }

        Language language = languageService.getLanguageByIdWithAllWords(id);
        category.setLanguage(language);
        category = categoryService.addNewCategory(category);
        if (category == null) {
            return ErrorMessage.send("The category has not added! Error!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping("/{idCategory}/get")
    public ResponseEntity<?> getLanguageWithCategoryAndWords(@PathVariable("id") Long id,
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

        Language language1 = categoryService.getLanguageWithCategoryAndWords(language, category);
        if (language1 == null) {
            return ErrorMessage.send("Language has not fetched! Error! ", HttpStatus.BAD_REQUEST);
        }

        language1.setAccount(null);

        return new ResponseEntity<>(language1, HttpStatus.OK);
    }

    @PostMapping("/{idCategory}/searchWord")
    public ResponseEntity<?> searchWordConfigs(@PathVariable("id") Long id,
                                               @PathVariable("idCategory") Long idCategory,
                                               @RequestBody Map<String, Object> textObj) {

        String text = (String) textObj.get("text");

        Language language = languageService.getLanguageByIdWithAllWords(id);
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

        List<WordConfig> wordConfigs = categoryService.searchWord(language, category, text);

        return new ResponseEntity<>(wordConfigs, HttpStatus.OK);
    }

    @PatchMapping("/{idCategory}/edit")
    public ResponseEntity<?> editCategory(@PathVariable("id") Long id,
                                          @PathVariable("idCategory") Long idCategory,
                                          @Validated(Category.CategoryValidation.class) @RequestBody Category newCategory,
                                          BindingResult result) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }
        if (result.hasErrors()) {
            return ErrorMessage.send(result, HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.getCategoryByIdAndLanguage(idCategory, language);
        if (category == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        if (category.isDefaultCategory()) {
            return ErrorMessage.send("You cannot edit this category.", HttpStatus.BAD_REQUEST);
        }

        category = categoryService.editCategory(category, newCategory, language);
        if (category == null) {
            return ErrorMessage.send("The category has not edited! Error!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(null, HttpStatus.OK);
    }


    @DeleteMapping("/{idCategory}/delete")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id,
                                            @PathVariable("idCategory") Long idCategory) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.getCategoryByIdAndLanguage(idCategory, language);
        if (category == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        if (category.isDefaultCategory()) {
            return ErrorMessage.send("You cannot delete this category.", HttpStatus.BAD_REQUEST);
        }

        categoryService.deleteCategory(category, language);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/{idCategory}/words/add")
    public ResponseEntity<?> addWord(@PathVariable("id") long id,
                                     @PathVariable("idCategory") long idCategory,
                                     @Validated(WordConfig.WordConfigValidation.class) @RequestBody WordConfig wordConfig,
                                     BindingResult result) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.getCategoryByIdAndLanguage(idCategory, language);
        if (category == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        if (result.hasErrors()) {
            return ErrorMessage.send(result, HttpStatus.BAD_REQUEST);
        }


        wordConfig = categoryService.addNewWord(language, category, wordConfig, false);
        if (wordConfig == null) {
            return ErrorMessage.send("The word has not added! Error!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(wordConfig, HttpStatus.OK);
    }

    @PatchMapping("/{idCategory}/words/{idWordConfig}/changeThisUnit")
    public ResponseEntity<?> changeThisUnit(@PathVariable("id") long id,
                                            @PathVariable("idCategory") long idCategory,
                                            @PathVariable("idWordConfig") long idWordConfig) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.getCategoryByIdAndLanguage(idCategory, language);
        if (category == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        WordConfig wordConfig = null;
        if(category.isDefaultCategory()){
            List<Category> categoryList = new ArrayList<>(language.getCategories());
            categoryList.removeIf(cl -> cl.getMode() != category.getMode());

            for (Category cl : categoryList) {
                wordConfig = categoryService.getWordConfigByIdAndCategory(idWordConfig, cl);
                if(wordConfig != null){
                    break;
                }
            }
        }
        else{
            List<Category> listOfChildren = new ArrayList<>();
            categoryService.createListOfAllCategoryChildren(category, language.getCategories(), listOfChildren);
            for (Category categoryChild : listOfChildren) {
                wordConfig = categoryService.getWordConfigByIdAndCategory(idWordConfig, categoryChild);
                if(wordConfig != null){
                    break;
                }
            }

        }

        if (wordConfig == null) {
            return ErrorMessage.send("There is no such word.", HttpStatus.BAD_REQUEST);
        }

        wordConfig = categoryService.changeThisUnitWordConfig(wordConfig);
        if (wordConfig == null) {
            return ErrorMessage.send("Something went wrong!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(wordConfig, HttpStatus.OK);
    }

    @DeleteMapping("/{idCategory}/words/{idWordConfig}/delete/{justForThisCategory}")
    public ResponseEntity<?> deleteWord(@PathVariable("id") long id,
                                        @PathVariable("idCategory") long idCategory,
                                        @PathVariable("idWordConfig") long idWordConfig,
                                        @PathVariable("justForThisCategory") boolean justForThisCategory) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.getCategoryByIdAndLanguage(idCategory, language);
        if (category == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        WordConfig wordConfig = null;
        Category category1 = null;
        if(category.isDefaultCategory()){
            List<Category> categoryList = new ArrayList<>(language.getCategories());
            categoryList.removeIf(cl -> cl.getMode() != category.getMode());

            for (Category cl : categoryList) {
                wordConfig = categoryService.getWordConfigByIdAndCategory(idWordConfig, cl);
                if(wordConfig != null){
                    category1 = cl;
                    break;
                }
            }
        }
        else{
            List<Category> listOfChildren = new ArrayList<>();
            categoryService.createListOfAllCategoryChildren(category, language.getCategories(), listOfChildren);
            for (Category categoryChild : listOfChildren) {
                wordConfig = categoryService.getWordConfigByIdAndCategory(idWordConfig, categoryChild);
                if(wordConfig != null){
                    category1 = categoryChild;
                    break;
                }
            }

        }

        if (wordConfig == null) {
            return ErrorMessage.send("There is no such word.", HttpStatus.BAD_REQUEST);
        }

        if (category1 == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        categoryService.deleteWordConfig(category1, wordConfig, justForThisCategory);

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PatchMapping("/{idCategory}/words/{idWordConfig}/edit")
    public ResponseEntity<?> editWord(@PathVariable("id") long id,
                                      @PathVariable("idCategory") long idCategory,
                                      @PathVariable("idWordConfig") long idWordConfig,
                                      @Validated(WordConfig.WordConfigValidation.class) @RequestBody WordConfig newWordConfig,
                                      BindingResult result) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.getCategoryByIdAndLanguage(idCategory, language);
        if (category == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        WordConfig wordConfig = null;
        Category category1 = null;
        if(category.isDefaultCategory()){
            List<Category> categoryList = new ArrayList<>(language.getCategories());
            categoryList.removeIf(cl -> cl.getMode() != category.getMode());

            for (Category cl : categoryList) {
                wordConfig = categoryService.getWordConfigByIdAndCategory(idWordConfig, cl);
                if(wordConfig != null){
                    category1 = cl;
                    break;
                }
            }
        }
        else{
            List<Category> listOfChildren = new ArrayList<>();
            categoryService.createListOfAllCategoryChildren(category, language.getCategories(), listOfChildren);
            for (Category categoryChild : listOfChildren) {
                wordConfig = categoryService.getWordConfigByIdAndCategory(idWordConfig, categoryChild);
                if(wordConfig != null){
                    category1 = categoryChild;
                    break;
                }
            }

        }

        if (wordConfig == null) {
            return ErrorMessage.send("There is no such word.", HttpStatus.BAD_REQUEST);
        }

        if (category1 == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        if (result.hasErrors()) {
            return ErrorMessage.send(result, HttpStatus.BAD_REQUEST);
        }

        wordConfig = categoryService.editWord(category, wordConfig, newWordConfig);
        if (wordConfig == null) {
            return ErrorMessage.send("Something went wrong!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(wordConfig, HttpStatus.OK);
    }

    @PatchMapping("/{idCategory}/words/{idWordConfig}/accept")
    public ResponseEntity<?> acceptWord(@PathVariable("id") long id,
                                        @PathVariable("idCategory") long idCategory,
                                        @PathVariable("idWordConfig") long idWordConfig) {

        Language language = languageService.getLanguageByIdWithAllWords(id);
        if (!languageService.checkLanguageBelongingToAccount(language)) {
            return ErrorMessage.send("You have no rights to this language.", HttpStatus.BAD_REQUEST);
        }

        Category category = categoryService.getCategoryByIdAndLanguage(idCategory, language);
        if (category == null) {
            return ErrorMessage.send("There is no such category.", HttpStatus.BAD_REQUEST);
        }

        WordConfig wordConfig = null;
        if(category.isDefaultCategory()){
            List<Category> categoryList = new ArrayList<>(language.getCategories());
            categoryList.removeIf(cl -> cl.getMode() != category.getMode());

            for (Category cl : categoryList) {
                wordConfig = categoryService.getWordConfigByIdAndCategory(idWordConfig, cl);
                if(wordConfig != null){
                    break;
                }
            }
        }
        else{
            List<Category> listOfChildren = new ArrayList<>();
            categoryService.createListOfAllCategoryChildren(category, language.getCategories(), listOfChildren);
            for (Category categoryChild : listOfChildren) {
                wordConfig = categoryService.getWordConfigByIdAndCategory(idWordConfig, categoryChild);
                if(wordConfig != null){
                    break;
                }
            }

        }

        if (wordConfig == null) {
            return ErrorMessage.send("There is no such word.", HttpStatus.BAD_REQUEST);
        }

        wordConfig = categoryService.acceptWordConfig(wordConfig, language, category);
        if (wordConfig == null) {
            return ErrorMessage.send("Something went wrong!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(wordConfig, HttpStatus.OK);
    }
}
