package com.brainate.service;

import com.brainate.domain.*;
import com.brainate.repository.HiddenWordRepository;
import com.brainate.repository.LanguageRepository;
import com.brainate.repository.WordConfigRepository;
import com.brainate.repository.WordRepository;
import com.google.cloud.translate.v3beta1.LocationName;
import com.google.cloud.translate.v3beta1.TranslateTextRequest;
import com.google.cloud.translate.v3beta1.TranslateTextResponse;
import com.google.cloud.translate.v3beta1.TranslationServiceClient;
import edu.stanford.nlp.simple.Sentence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
public class AutomaticAddingWordService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private WordConfigRepository wordConfigRepository;

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private HiddenWordRepository hiddenWordRepository;

    private List<String> lemmatization(String codeForTranslator, String text) {
        if (codeForTranslator.equals("en")) {
            List<String> unRemovableTokenLemma = new Sentence(text).lemmas();
            List<String> words = new ArrayList<>(unRemovableTokenLemma);
            words.removeIf(tl -> {
                List<String> notAllowed = Arrays.asList("'s", "'re", "'ve", "'ll");
                return notAllowed.contains(tl) || tl.matches("^[0-9]*$") ||
                        tl.matches("^[_/\\- ?,.!#$%^&*()=+\\[\\]{}:<>|'\"]*$");
            });

            Set<String> wordConfigsSet = new LinkedHashSet<>(words);
            words.clear();
            words.addAll(wordConfigsSet);

            return words;
        } else {
            return new ArrayList<>();
        }
    }

    private List<WordConfig> autoTranslate(String codeForTranslator, String text) {
        //remember to set Google Credentials if you want to auto translate
        List<String> wordsAfterLemma = this.lemmatization(codeForTranslator, text);
        if (wordsAfterLemma.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> words = new ArrayList<>();
        List<WordConfig> wordConfigs = new ArrayList<>();

        wordsAfterLemma.forEach(w -> {
            Word word = this.wordRepository.findWordByWordAndAnswerAndMode(w, true, Mode.DICTIONARY.name());
            if (word == null) {
                words.add(w);
            } else {
                WordConfig wordConfig = new WordConfig();
                wordConfig.setComment("");
                wordConfig.setMechanism(Mechanism.BASIC);
                List<Word> words1 = new ArrayList<>();
                WordConfig currentWordConfig = word.getWordConfig();

                currentWordConfig.getWords().forEach(cr -> {
                    words1.add(new Word(null, cr.getWord(), "", cr.getSeat(), cr.isToSpeech(), cr.isAnswer(), wordConfig));
                });
                wordConfig.setWords(words1);
                wordConfigs.add(wordConfig);
            }
        });

        List<LemmaWord> lemmaWords = new ArrayList<>();

        if (words.isEmpty()) {
            System.out.println(wordConfigs);
            return wordConfigs;
        }

        try (TranslationServiceClient translationServiceClient = TranslationServiceClient.create()) {

            LocationName locationName =
                    LocationName.newBuilder().setProject("striped-weaver-237318").setLocation("global").build();

            TranslateTextRequest translateTextRequest =
                    TranslateTextRequest.newBuilder()
                            .setParent(locationName.toString())
                            .setMimeType("text/plain")
                            .setSourceLanguageCode(codeForTranslator)
                            .setTargetLanguageCode("pl")
                            .addAllContents(words)
                            .build();

            TranslateTextResponse response = translationServiceClient.translateText(translateTextRequest);
            for (int i = 0; i < words.size(); i++) {
                lemmaWords.add(new LemmaWord(words.get(i).toLowerCase(), response.getTranslations(i).getTranslatedText().toLowerCase()));
            }

        } catch (Exception e) {
            throw new RuntimeException("Couldn't create client.", e);
        }


        lemmaWords.forEach(lw -> {
            WordConfig wordConfig = new WordConfig();
            wordConfig.setComment("");
            wordConfig.setMechanism(Mechanism.BASIC);
            List<Word> words2 = new ArrayList<>();
            words2.add(new Word(null, lw.getTranslate(), "", 0, false, false, null));
            words2.add(new Word(null, lw.getWord(), "", 1, true, true, null));
            wordConfig.setWords(words2);
            wordConfigs.add(wordConfig);
        });

        return wordConfigs;
    }

    public List<WordConfig> addWordsAutomatically(String codeForTranslator, String text, Language language, Category category) {
        List<WordConfig> wordConfigs = this.autoTranslate(codeForTranslator, text);
        wordConfigs.forEach(wc -> {
            categoryService.addNewWord(language, category, wc, false);
        });

        return wordConfigs;
    }

    public List<Language> getAllLanguages() {
        Account account = accountService.getLoggedAccount();
        List<Language> languages = languageRepository.findLanguagesByAccountNotAndHidden(account, false);

        List<HiddenWord> hiddenWords = account.getHiddenWords();

        for (int i = 0; i < languages.size(); i++) {
            HiddenWord hiddenWord = new HiddenWord(0, languages.get(i).getId(), 0, 0, account);
            if (hiddenWords.contains(hiddenWord)) {
                languages.get(i).setId(-1);
                continue;
            }

            for (int j = 0; j < languages.get(i).getCategories().size(); j++) {
                hiddenWord.setId_category(languages.get(i).getCategories().get(j).getId());
                hiddenWord.setId_wordConfig(0);
                if (hiddenWords.contains(hiddenWord)) {
                    languages.get(i).getCategories().get(j).setId(-1L);
                    continue;
                }

                List<WordConfig> wordConfigs = new ArrayList<>(wordConfigRepository.findWordConfigsExceptHiddenWords(hiddenWord.getId_language(), hiddenWord.getId_category(), account.getId(), true));
                wordConfigs.forEach(wc -> {
                    int numberOfWords = wc.getWords().size();
                    for (Word word : wc.getWords()) {
                        Word wordExists = wordRepository.findWordByWordAndIdAccount(word.getWord(), account.getId());
                        if(wordExists != null) {
                            numberOfWords--;
                        }
                    }

                    if(numberOfWords == 0){
                        wc.setAccepted(false);
                    }
                });
                languages.get(i).getCategories().get(j).setWordConfigs(wordConfigs);
            }

        }

        languages.removeIf(l -> l.getId() == -1L);
        languages.forEach(l -> {
            l.getCategories().removeIf(c -> c.getId() == -1L);
        });

        return languages;
    }

    public void hideWord(HiddenWord hiddenWord) {
        hiddenWord.setId(0);
        hiddenWordRepository.save(hiddenWord);
    }

    public List<HiddenWord> copyWords(Language language, Category category, List<HiddenWord> hiddenWords) {
        Account account = accountService.getLoggedAccount();
        hiddenWords.forEach(hw -> hw.setAccount(account));

        hiddenWords.forEach(hw -> {
            if (hw.getId_wordConfig() != 0) {
                WordConfig wordConfig = wordConfigRepository.findWordConfigById(hw.getId_wordConfig());
                WordConfig wordConfigCopy = new WordConfig();
                wordConfigCopy.setComment(wordConfig.getComment());
                wordConfigCopy.setMechanism(wordConfig.getMechanism());
                List<Word> words = new ArrayList<>();
                wordConfig.getWords().forEach(w -> {
                    Word word = new Word(null, w.getWord(), w.getBasicWord(), w.getSeat(), w.isToSpeech(), w.isAnswer(), null);
                    words.add(word);
                });
                wordConfigCopy.setWords(words);
                this.categoryService.addNewWord(language, category, wordConfigCopy, true);
                this.hideWord(hw);
            }
        });


        return hiddenWords;
    }

}
