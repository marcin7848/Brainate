package com.brainate.service;

import com.brainate.domain.*;
import com.brainate.repository.*;
import org.apache.commons.codec.language.bm.Lang;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final DictionaryLanguageRepository dictionaryLanguageRepository;
    private final SpecialLetterRepository specialLetterRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategorySettingRepository categorySettingRepository;

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

    @Autowired
    private AccountRepository accountRepository;

    public LanguageService(LanguageRepository languageRepository, DictionaryLanguageRepository dictionaryLanguageRepository, SpecialLetterRepository specialLetterRepository) {
        this.languageRepository = languageRepository;
        this.dictionaryLanguageRepository = dictionaryLanguageRepository;
        this.specialLetterRepository = specialLetterRepository;
    }

    public boolean checkLanguageBelongingToAccount(Long id_language) {
        Language language = languageRepository.findLanguageById(id_language);
        if (language == null) {
            return false;
        }

        return accountService.getLoggedAccount() == language.getAccount();
    }

    public boolean checkLanguageBelongingToAccount(Language language) {
        if (language == null) {
            return false;
        }
        return accountService.getLoggedAccount() == language.getAccount();
    }

    public Language addLanguage(Language language) {
        List<CategorySetting> categorySettings = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        List<Statistic> statistics = new ArrayList<>();
        for (Mode mode : Mode.values()) {
            Statistic statistic = new Statistic(null, mode, 0L, 0L, language);
            statistics.add(statistic);

            CategorySetting categorySettingDictionary = new CategorySetting(null, mode, false, Method.FIRSTTOSECOND, language);
            categorySettings.add(categorySettingDictionary);

            Category category = new Category(null, "Default", mode, 0L, true,
                    language, null);
            categories.add(category);
        }
        language.setCategorySettings(categorySettings);
        language.setCategories(categories);
        language.setStatistics(statistics);

        List<Task> tasks = new ArrayList<>();
        for (TaskMode taskMode : TaskMode.values()) {
            Task task = new Task(null, taskMode, 0, 0, 0, 0, false, null, language);
            tasks.add(task);
        }
        language.setTasks(tasks);

        Language language1 = languageRepository.save(language);
        Iterable<SpecialLetter> specialLetters = language.getDictionaryLanguage().getSpecialLetters();
        specialLetters.forEach(sl -> sl.setDictionaryLanguage(language1.getDictionaryLanguage()));
        specialLetterRepository.saveAll(specialLetters);

        return language1;

    }

    public List<Language> getLanguagesOfAccount() {
        List<Language> languages = languageRepository.findLanguagesByAccount(accountService.getLoggedAccount());
        languages.forEach(l -> {
            l.setCategories(null);
            l.setAccount(null);
        });
        return languages;
    }

    public Language getLanguageById(Long id) {
        Language language = languageRepository.findLanguageById(id);
        if (language == null) {
            return null;
        }

        if (language.getAccount() != accountService.getLoggedAccount()) {
            return null;
        }

        language.getCategories().forEach(c -> c.setWordConfigs(null));

        return language;
    }

    public Language getLanguageByIdWithAllWords(Long id) {
        Language language = languageRepository.findLanguageById(id);
        if (language == null) {
            return null;
        }

        if (language.getAccount() != accountService.getLoggedAccount()) {
            return null;
        }

        return language;
    }

    public Language getLanguageWithTodayWords(Long id) {
        Language language = languageRepository.findLanguageById(id);
        if (language == null) {
            return null;
        }

        if (language.getAccount() != accountService.getLoggedAccount()) {
            return null;
        }

        List<WordConfig> wordConfigs = wordConfigRepository.findWordConfigsByTodayAndCategoriesIn(true, language.getCategories());


        language.getCategories().forEach(c -> c.setWordConfigs(new ArrayList<>()));
        language.getCategories().get(0).setWordConfigs(wordConfigs);
        return language;
    }

    public Language updateLanguage(Language newLanguage, Language oldLanguage) {
        oldLanguage.getDictionaryLanguage().getSpecialLetters().forEach(sl -> specialLetterRepository.deleteSpecialLetterById(sl.getId()));
        newLanguage.setId(oldLanguage.getId());
        newLanguage.getDictionaryLanguage().setId(oldLanguage.getDictionaryLanguage().getId());
        newLanguage.setAccount(oldLanguage.getAccount());
        List<SpecialLetter> specialLetters = newLanguage.getDictionaryLanguage().getSpecialLetters();
        specialLetters.forEach(sl -> sl.setDictionaryLanguage(newLanguage.getDictionaryLanguage()));
        newLanguage.getDictionaryLanguage().getSpecialLetters().addAll(specialLetters);
        Language savedLanguage = languageRepository.save(newLanguage);
        savedLanguage.setAccount(null);

        return newLanguage;
    }

    public void deleteLanguage(Language language) {
        language.getCategories().forEach(c -> {
            c.getWordConfigs().forEach(wc -> {
                wc.getWords().forEach(w -> {
                    wordRepository.deleteWordById(w.getId());
                });

                wordConfigRepository.deleteWordConfigById(wc.getId());
            });
        });
        languageRepository.delete(language);
    }

    public CategorySetting editCategorySetting(Long idLanguage, Long idCategorySetting, CategorySetting categorySetting) {
        CategorySetting categorySetting1 = categorySettingRepository
                .findCategorySettingByIdAndLanguage(idCategorySetting, languageRepository.findLanguageById(idLanguage));
        if(categorySetting1 == null){
            return null;
        }

        categorySetting1.setThisUnitOn(categorySetting.isThisUnitOn());
        categorySetting1.setMethod(categorySetting.getMethod());

        return categorySettingRepository.save(categorySetting1);
    }

    private List<Category> prepareCategories(Language language, List<Category> categoriesJustWithId){
        List<Category> categoriesList = new ArrayList<>();
        for (Category c : categoriesJustWithId) {
            Category category = categoryRepository.findCategoryById(c.getId());
            if (category != null) {
                List<Category> listOfChildren = new ArrayList<>();
                if(category.isDefaultCategory() && category.getMode() == Mode.DICTIONARY){
                    List<Category> categoriesDictionary = new ArrayList<>(language.getCategories());
                    categoriesDictionary.removeIf(cD -> cD.getMode() != Mode.DICTIONARY);
                    listOfChildren.addAll(categoriesDictionary);
                }
                else if(category.isDefaultCategory() && category.getMode() == Mode.EXERCISE){
                    List<Category> categoriesExercise = new ArrayList<>(language.getCategories());
                    categoriesExercise.removeIf(cD -> cD.getMode() != Mode.EXERCISE);
                    listOfChildren.addAll(categoriesExercise);
                }else {
                    this.categoryService.createListOfAllCategoryChildren(category, language.getCategories(), listOfChildren);
                }
                categoriesList.addAll(listOfChildren);
            }
        }

        return new ArrayList<>(new LinkedHashSet<>(categoriesList));
    }


    public void resetThisUnit(Language language, List<Category> categoriesJustWithId){
        List<Category> categories = this.prepareCategories(language, categoriesJustWithId);
        List<Long> categoriesId = new ArrayList<>();
        categories.forEach(c -> categoriesId.add(c.getId()));
        wordConfigRepository.resetThisUnit(categoriesId);
    }

    public void resetWords(Language language, List<Category> categoriesJustWithId){
        List<Category> categories = this.prepareCategories(language, categoriesJustWithId);
        List<Long> categoriesId = new ArrayList<>();
        categories.forEach(c -> categoriesId.add(c.getId()));
        wordConfigRepository.resetWords(categoriesId);
    }

    public void startRepeating(Language language, List<Category> categoriesJustWithId, int numberOfWords){
        language.setStarted(true);
        languageRepository.save(language);
        List<Category> categories = this.prepareCategories(language, categoriesJustWithId);
        List<Long> categoriesId = new ArrayList<>();
        categories.forEach(c -> categoriesId.add(c.getId()));

        List<Category> categoriesDictionary = new ArrayList<>();
        List<Category> categoriesExercise = new ArrayList<>();
        categories.forEach(c -> {
            if(c.getMode() == Mode.DICTIONARY){
                categoriesDictionary.add(c);
            }
            else{
                categoriesExercise.add(c);
            }
        });

        boolean thisUnitDictionaryOn = false;
        boolean thisUnitExerciseOn = false;

        if(language.getCategorySettings().get(0).isThisUnitOn()) {
            if(language.getCategorySettings().get(0).getMode() == Mode.DICTIONARY){
                thisUnitDictionaryOn=true;
            }else{
                thisUnitExerciseOn=true;
            }
        }

        if(language.getCategorySettings().get(1).isThisUnitOn()) {
            if(language.getCategorySettings().get(1).getMode() == Mode.DICTIONARY){
                thisUnitDictionaryOn=true;
            }else{
                thisUnitExerciseOn=true;
            }
        }
        List<WordConfig> wordConfigs = new ArrayList<>();
        if(thisUnitDictionaryOn){
            wordConfigs.addAll(wordConfigRepository.findWordConfigsByThisUnitAndCategoriesIn(true, categoriesDictionary));
            wordConfigs.forEach(wc -> {
                wc.setToday(true);
                wc.setDone(0);
                wordConfigRepository.saveAndFlush(wc);
            });
            numberOfWords -= wordConfigs.size();
        }

        wordConfigs.clear();

        if(thisUnitExerciseOn){
            wordConfigs.addAll(wordConfigRepository.findWordConfigsByThisUnitAndCategoriesIn(true, categoriesExercise));
            wordConfigs.forEach(wc -> {
                wc.setToday(true);
                wc.setDone(0);
                wordConfigRepository.saveAndFlush(wc);
            });
            numberOfWords -= wordConfigs.size();
        }


        if(numberOfWords < 0){
            numberOfWords = 0;
        }
        List<WordConfig> wordConfigs1 = wordConfigRepository.findWordConfigsByCategoriesIdInIncludingDateTimeDone(categoriesId, numberOfWords);
        wordConfigs1.forEach(wc -> {
            wc.setToday(true);
            wc.setDone(0);
            wordConfigRepository.saveAndFlush(wc);
        });
        numberOfWords -= wordConfigs1.size();

        if(numberOfWords < 0){
            numberOfWords = 0;
        }

        List<WordConfig> wordConfigs2 = wordConfigRepository.findWordConfigsByAcceptedAndTodayAndDoneAndCategoriesIn(true, false, 0, categoriesId, numberOfWords);
        wordConfigs2.forEach(wc -> {
            wc.setToday(true);
            wc.setDone(0);
            wordConfigRepository.saveAndFlush(wc);
        });

    }

    private boolean processNoInterpreter(WordConfig wordConfig){
        /*
		processNo:
		0 - niepowtórzone jeszcze
		powtórzone już:
		1 - załaduj jeśli mineły 2 dni
		2 - załaduj jeśli mineło 4 dni
		3 - załaduj jeśli mineło 5 dni
		4 - załaduj jeśli mineło 6 dni
		5 - załaduj jeśli mineło 9 dni
		6 - załaduj jeśli mineło 10 dni
		7 - załaduj jeśli mineło 11 dni
		8 - załaduj jeśli mineło 12 dni
		9 - załaduj jeśli mineło 13 dni
		10 - załaduj jeśli mineło 14 dni
		11 - załaduj jeśli mineło 15 dni
		*/

        if(wordConfig.getDateTimeDone() == null){
            return false;
        }

        long daysBetween = ChronoUnit.DAYS.between(new Date(wordConfig.getDateTimeDone().getTime()).toInstant(), new Date().toInstant());

        if(wordConfig.getProcessNo() == 1 && daysBetween >= 2){
            wordConfig.setToday(true);
            wordConfig.setDone(0);
            return true;
        }
        else if(wordConfig.getProcessNo() == 2 && daysBetween >= 4){
            wordConfig.setToday(true);
            wordConfig.setDone(0);
            return true;
        }
        else if(wordConfig.getProcessNo() == 3 && daysBetween >= 5){
            wordConfig.setToday(true);
            wordConfig.setDone(0);
            return true;
        }
        else if(wordConfig.getProcessNo() == 4 && daysBetween >= 6){
            wordConfig.setToday(true);
            wordConfig.setDone(0);
            return true;
        }
        else if(wordConfig.getProcessNo() == 5 && daysBetween >= 9){
            wordConfig.setToday(true);
            wordConfig.setDone(0);
            return true;
        }
        else if(wordConfig.getProcessNo() == 6 && daysBetween >= 10){
            wordConfig.setToday(true);
            wordConfig.setDone(0);
            return true;
        }
        else if(wordConfig.getProcessNo() == 7 && daysBetween >= 11){
            wordConfig.setToday(true);
            wordConfig.setDone(0);
            return true;
        }
        else if(wordConfig.getProcessNo() == 8 && daysBetween >= 12){
            wordConfig.setToday(true);
            wordConfig.setDone(0);
            return true;
        }
        else if(wordConfig.getProcessNo() == 9 && daysBetween >= 13){
            wordConfig.setToday(true);
            wordConfig.setDone(0);
            return true;
        }
        else if(wordConfig.getProcessNo() == 10 && daysBetween >= 14){
            wordConfig.setToday(true);
            wordConfig.setDone(0);
            return true;
        }
        else if(wordConfig.getProcessNo() == 11 && daysBetween >= 15){
            wordConfig.setToday(true);
            wordConfig.setDone(0);
            return true;
        }

        return false;
    }

    public void resetRepeating(Language language){
        language.setStarted(false);
        this.languageRepository.save(language);
        List<Long> categoriesId = new ArrayList<>();
        language.getCategories().forEach(c -> categoriesId.add(c.getId()));
        wordConfigRepository.resetRepeating(categoriesId);
    }

    public WordConfig changeDone(WordConfig wordConfig, WordConfig wordConfigOld, Category category, Language language){
        wordConfigOld.setDone(wordConfig.getDone() < 0 || wordConfig.getDone() > 3 ? 0 : wordConfig.getDone());

        if(wordConfigOld.getDone() == 3){
            wordConfigOld.setRepeated(wordConfigOld.getRepeated() + 1);
            wordConfigOld.setProcessNo(wordConfigOld.getProcessNo() + 1);
            wordConfigOld.setDateTimeDone(new Timestamp(new Date().getTime()));

            language.getStatistics().removeIf(statistic -> statistic.getMode() != category.getMode());
            language.getStatistics().get(0).setDone(language.getStatistics().get(0).getDone()+1);
            statisticRepository.save(language.getStatistics().get(0));

            if(category.getMode() == Mode.DICTIONARY){
                language.getTasks().removeIf(task -> task.getTaskMode() != TaskMode.DICDO);
                language.getTasks().get(0).setDone(language.getTasks().get(0).getDone()+1);
            }
            else{
                language.getTasks().removeIf(task -> task.getTaskMode() != TaskMode.EXEDO);
                language.getTasks().get(0).setDone(language.getTasks().get(0).getDone()+1);
            }
            taskRepository.save(language.getTasks().get(0));

        }

        return wordConfigRepository.save(wordConfigOld);
    }


    public Iterable<Task> editTasks(List<Task> tasks, Language language){
        List<Task> currentTasks = language.getTasks();

        currentTasks.forEach(ct -> {
            int index = tasks.indexOf(ct);
            ct.setActive(tasks.get(index).isActive());
            ct.setMinNumber(tasks.get(index).getMinNumber());
            ct.setMaxNumber(tasks.get(index).getMaxNumber());
        });

        return taskRepository.saveAll(currentTasks);
    }

    public void reloadTasks(){
        Iterable<Account> accounts = accountService.getAllAccountsForReloadTasks();
        accounts.forEach(account -> {
            boolean addPoints = true;
            long pointsToAdd = 0;
            int numberOfTasks = 0;
            for (Language language : account.getLanguages()) {
                language.getTasks().removeIf(task -> !task.isActive());
                for (Task task : language.getTasks()) {
                    if (task.getDone() < task.getToDo()) {
                        addPoints = false;
                    }
                    if (task.getTaskMode() == TaskMode.DICDO || task.getTaskMode() == TaskMode.EXEDO) {
                        pointsToAdd += task.getDone();
                    }else if(task.getTaskMode() == TaskMode.DICADD){
                        pointsToAdd += 2*task.getDone();
                    }
                    else{
                        pointsToAdd += 3*task.getDone();
                    }

                    numberOfTasks++;
                    task.setDone(0);
                    if(task.getMaxNumber() - task.getMinNumber() <= 0){
                        task.setToDo(task.getMaxNumber() >= 0 ? task.getMaxNumber() : 0);
                    }
                    else {
                        task.setToDo(new Random().nextInt(task.getMaxNumber() - task.getMinNumber()) + task.getMinNumber());
                    }
                    taskRepository.save(task);
                }
            }

            if(addPoints && numberOfTasks > 0){
                account.setPoints(account.getPoints() + pointsToAdd + 50);
                accountRepository.save(account);
            }
        });

    }

}

