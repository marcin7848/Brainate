package com.brainate.service;

import com.brainate.domain.*;
import com.brainate.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WordConfigRepository wordConfigRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private StatisticRepository statisticRepository;

    @Autowired
    private TaskRepository taskRepository;

    public Category addNewCategory(Category category){
        if(category.getIdParent() != 0) {
            Category parentCategory = categoryRepository.findCategoryById(category.getIdParent());
            if (parentCategory == null) {
                return null;
            }
            category.setMode(parentCategory.getMode());
        }

        category.setDefaultCategory(false);

        return categoryRepository.save(category);
    }

    public Category getCategoryByIdAndLanguage(Long id, Language language) {
        return categoryRepository.findCategoryByIdAndLanguage(id, language);
    }

    public Language getLanguageWithCategoryAndWords(Language language, Category category){
        List<Category> categories = new ArrayList<>();
        language.getCategories().removeIf(c -> c.getMode() != category.getMode());

        if(category.isDefaultCategory()){
            categories.addAll(language.getCategories());
        }
        else{
            this.createListOfAllCategoryChildren(category, language.getCategories(), categories);
        }

        List<WordConfig> wordConfigs = wordConfigRepository.findWordConfigsByAcceptedAndCategoriesIn(false, categories);
        category.setWordConfigs(wordConfigs);
        categories.clear();
        categories.add(category);

        Set<WordConfig> wordConfigsSet = new LinkedHashSet<>(categories.get(0).getWordConfigs());
        categories.get(0).setWordConfigs(new ArrayList<>(wordConfigsSet));

        categories.addAll(language.getCategories());
        Set<Category> categoriesSet = new LinkedHashSet<>(categories);
        language.setCategories(new ArrayList<>(categoriesSet));
        language.getCategories().forEach(c -> c.setLanguage(null));
        return language;
    }

    public List<WordConfig> searchWord(Language language, Category category, String text){
        List<Category> categories = new ArrayList<>();
        language.getCategories().removeIf(c -> c.getMode() != category.getMode());

        if(category.isDefaultCategory()){
            categories.addAll(language.getCategories());
        }
        else{
            this.createListOfAllCategoryChildren(category, language.getCategories(), categories);
        }

        List<Long> categoriesId = new ArrayList<>();
        categories.forEach(c -> categoriesId.add(c.getId()));
        List<WordConfig> wordConfigs = wordConfigRepository.findWordConfigsByWordAndCategoriesIn(text, categoriesId);
        return wordConfigs;
    }

    public void createListOfAllCategoryChildren(Category parentCategory, List<Category> categories,
                                                 List<Category> listOfChildren){
        listOfChildren.add(parentCategory);
        categories.forEach(c -> {
            if(c.getIdParent() == parentCategory.getId()){
                this.createListOfAllCategoryChildren(c, categories, listOfChildren);
            }
        });
    }

    public Category editCategory(Category oldCategory, Category newCategory, Language language){
        newCategory.setWordConfigs(oldCategory.getWordConfigs());
        newCategory.setDefaultCategory(oldCategory.isDefaultCategory());
        newCategory.setLanguage(oldCategory.getLanguage());
        newCategory.setMode(oldCategory.getMode());
        newCategory.setId(oldCategory.getId());

        List<Category> listOfChildren = new ArrayList<>();

        this.createListOfAllCategoryChildren(oldCategory, language.getCategories(), listOfChildren);
        for (Category category: listOfChildren) {
            if (category.getId().equals(newCategory.getIdParent())) {
                return null;
            }
        }

        Category defaultCategory = categoryRepository.findCategoryById(newCategory.getIdParent());
        if(defaultCategory != null && defaultCategory.isDefaultCategory()){
            newCategory.setIdParent(0L);
        }

        return categoryRepository.save(newCategory);
    }

    public void deleteCategory(Category category, Language language){
        List<Category> listOfChildren = new ArrayList<>();
        this.createListOfAllCategoryChildren(category, language.getCategories(), listOfChildren);
        List<WordConfig> wordConfigs = wordConfigRepository.findWordConfigsByCategoriesIn(listOfChildren);
        listOfChildren.forEach(lc -> categoryRepository.deleteCategoryById(lc.getId()));

        wordConfigs.forEach(wc -> {
            List<WordConfig> justOneWordConfig = new ArrayList<>();
            justOneWordConfig.add(wc);
            if(categoryRepository.findCategoriesByWordConfigs(justOneWordConfig).isEmpty()){
                wc.getWords().forEach(w -> wordRepository.deleteWordById(w.getId()));
                wordConfigRepository.deleteWordConfigById(wc.getId());
            }
        });
    }

    public WordConfig addNewWord(Language language, Category category, WordConfig wordConfig, boolean setAcceptedTo){
        wordConfig.setToday(false);
        wordConfig.setAccepted(setAcceptedTo);
        wordConfig.setDone(0);
        wordConfig.setLastPerfect(false);
        wordConfig.setLastPerfectNum(0);
        wordConfig.setProcessNo(0);
        wordConfig.setThisUnit(false);
        wordConfig.setRepeated(0);
        wordConfig.setId(null);
        wordConfig.setMechanism(category.getMode() == Mode.DICTIONARY ? Mechanism.BASIC : wordConfig.getMechanism());
        wordConfig.getWords().forEach(w -> w.setId(null));

        List<Word> words = wordConfig.getWords();
        wordConfig.setWords(null);

        if(category.getMode() == Mode.DICTIONARY) {
            List<String> wordsString = new ArrayList<>();
            words.forEach(w -> {
                if(w.isAnswer()) {
                    wordsString.add(w.getWord().toLowerCase());
                }
            });

            List<Long> categoriesId = new ArrayList<>();
            language.getCategories().forEach(c -> categoriesId.add(c.getId()));


            List<Word> wordsInDB = wordRepository.findWordByWordsInAndCategoriesInAndAnswerAndMode(wordsString, categoriesId, true, "DICTIONARY");

            if(!wordsInDB.isEmpty()){
                WordConfig wordConfig1 = wordsInDB.get(0).getWordConfig();
                wordConfig1.getWords().addAll(words);
                List<Word> words1 = new ArrayList<>(wordConfig1.getWords());
                wordConfig1.setWords(null);
                words1 = new ArrayList<>(new LinkedHashSet<>(words1));
                WordConfig newWordConfig = wordConfigRepository.save(wordConfig1);
                words1.forEach(w1 -> w1.setWordConfig(newWordConfig));
                wordRepository.saveAll(words1);

                boolean wordConfigExistsInGivenCategory = false;
                for (Word w : wordsInDB) {
                    for (Category c : w.getWordConfig().getCategories()) {
                        if (c.getId().equals(category.getId())) {
                            wordConfigExistsInGivenCategory = true;
                        }
                    }
                }

                if(!wordConfigExistsInGivenCategory){
                    categoryRepository.insertIntoCategoryWordConfig(category.getId(), wordConfig1.getId());
                }

                wordConfig1.setWords(words1);
                return wordConfig1;
            }
        }

        WordConfig wordConfig1 = wordConfigRepository.save(wordConfig);
        words.forEach(w -> w.setWordConfig(wordConfig1));
        wordRepository.saveAll(words);
        categoryRepository.insertIntoCategoryWordConfig(category.getId(), wordConfig1.getId());
        wordConfig.setWords(words);
        return wordConfig;
    }

    public WordConfig getWordConfigByIdAndCategory(Long id, Category category){
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        return wordConfigRepository.findWordConfigByIdAndCategories(id, categories);
    }

    public WordConfig changeThisUnitWordConfig(WordConfig wordConfig){
        wordConfig.setThisUnit(!wordConfig.isThisUnit());
        return wordConfigRepository.save(wordConfig);
    }

    public void deleteWordConfig(Category category, WordConfig wordConfig, boolean justForThisCategory){
        if(justForThisCategory){
            wordConfigRepository.deleteWordConfigCategoriesLink(category.getId(), wordConfig.getId());
            List<WordConfig> justOneWordConfig = new ArrayList<>();
            justOneWordConfig.add(wordConfig);
            List<Category> categories = categoryRepository.findCategoriesByWordConfigs(justOneWordConfig);
            if(categories.isEmpty()){
                wordConfig.getWords().forEach(w -> wordRepository.deleteWordById(w.getId()));
                wordConfigRepository.deleteWordConfigById(wordConfig.getId());
            }
        }else{
            wordConfigRepository.deleteWordConfigCategoriesLinks(wordConfig.getId());
            wordConfig.getWords().forEach(w -> wordRepository.deleteWordById(w.getId()));
            wordConfigRepository.deleteWordConfigById(wordConfig.getId());
        }
    }

    public WordConfig editWord(Category category, WordConfig oldWordConfig, WordConfig newWordConfig){
        oldWordConfig.setMechanism(category.getMode() == Mode.DICTIONARY ? Mechanism.BASIC : newWordConfig.getMechanism());
        oldWordConfig.setComment(newWordConfig.getComment());

        int z = 0;
        for(int i = 0; i < oldWordConfig.getWords().size(); i++){
            if(newWordConfig.getWords().size() > i){
                oldWordConfig.getWords().get(i).setWord(newWordConfig.getWords().get(i).getWord());
                oldWordConfig.getWords().get(i).setBasicWord(newWordConfig.getWords().get(i).getBasicWord());
                oldWordConfig.getWords().get(i).setAnswer(newWordConfig.getWords().get(i).isAnswer());
                oldWordConfig.getWords().get(i).setSeat(newWordConfig.getWords().get(i).getSeat());
            }else{
                wordRepository.deleteWordById(oldWordConfig.getWords().get(i).getId());
            }
            z++;
        }

        for(int j = z; j < newWordConfig.getWords().size(); j++){
            newWordConfig.getWords().get(j).setId(null);
            newWordConfig.getWords().get(j).setWordConfig(oldWordConfig);
            oldWordConfig.getWords().add(newWordConfig.getWords().get(j));
        }

        WordConfig wordConfig1 = wordConfigRepository.save(oldWordConfig);
        wordConfig1.setWords(newWordConfig.getWords());
        return wordConfig1;
    }

    public WordConfig acceptWordConfig(WordConfig wordConfig, Language language, Category category){

        language.getStatistics().removeIf(statistic -> statistic.getMode() != category.getMode());
        language.getStatistics().get(0).setAdded(language.getStatistics().get(0).getAdded()+1);
        statisticRepository.save(language.getStatistics().get(0));

        if(category.getMode() == Mode.DICTIONARY){
            language.getTasks().removeIf(task -> task.getTaskMode() != TaskMode.DICADD);
            language.getTasks().get(0).setDone(language.getTasks().get(0).getDone()+1);
        }
        else{
            language.getTasks().removeIf(task -> task.getTaskMode() != TaskMode.EXEADD);
            language.getTasks().get(0).setDone(language.getTasks().get(0).getDone()+1);
        }
        taskRepository.save(language.getTasks().get(0));


        wordConfig.setAccepted(true);
        return wordConfigRepository.save(wordConfig);
    }
}
