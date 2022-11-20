import {Component, OnInit} from '@angular/core';
import {Language} from "../../../model/Language";
import {ActivatedRoute, Router} from "@angular/router";
import {LanguageService} from "../../language.service";
import {Mode} from "../../../model/Mode";
import {Method} from "../../../model/Method";
import {WordConfig} from "../../../model/WordConfig";
import {Category} from "../../../model/Category";
import {Mechanism} from "../../../model/Mechanism";
import * as $ from 'jquery';
import Speech from 'speak-tts';
import SpeechToText from 'speech-to-text';
import {CookieService} from "ngx-cookie-service";

@Component({
  selector: 'app-repeating-words',
  templateUrl: './repeating-words.component.html',
  styleUrls: ['./repeating-words.component.scss']
})
export class RepeatingWordsComponent implements OnInit {

  language: Language;
  phase: boolean = false;
  countToWordsEnd: number = 0;
  textToRead: string = "";
  wordConfigs: WordConfig[] = [];
  Mode = Mode;
  Mechanism = Mechanism;
  Method = Method;
  currentMethod = Method.FIRSTTOSECOND;
  isFocusInput = false;
  focusInputIndex = 0;
  elementCaretPos = null;
  setDoneTo: number = 0;
  speech = new Speech();
  textToSpeech = false;
  listener;
  speechToText = false;

  constructor(private router: Router,
              private languageService: LanguageService,
              private route: ActivatedRoute,
              private cookieService: CookieService) {
  }

  ngOnInit() {
    if(this.cookieService.get("TextToSpeech") == "true"){
      this.textToSpeech = true;
    }
    if(this.cookieService.get("SpeechToText") == "true"){
      this.speechToText = true;
    }

    this.route.params.subscribe(params => {
      const id: number = +params['id'];

      this.languageService.getLanguageByIdWithTodayWords(id)
        .subscribe(
          data => {
            this.language = data;
            this.language.categorySettings = this.language.categorySettings.filter(cs => cs.mode == Mode.DICTIONARY);
            this.currentMethod = this.language.categorySettings[0].method;

            let listOfIdWordConfig: number[] = [];
            this.language.categories.forEach(c => c.wordConfigs.forEach(wc => {
                if (listOfIdWordConfig.indexOf(wc.id) == -1) {
                  let category: Category = new Category();
                  category.id = wc.categoryId;
                  category.mode = wc.categoryMode;
                  let categories: Category[] = [];
                  categories.push(category);
                  wc.categories = categories;
                  listOfIdWordConfig.push(wc.id);
                  this.wordConfigs.push(wc);
                }
              }
            ));
            this.countWordsToEnd();


            if(this.countToWordsEnd <= 0){
              this.languageService.resetRepeating(this.language.id).subscribe(
                result => {
                  this.router.navigate(['/languages/'+this.language.id+'/show']);
                },
                error => {
                  this.router.navigate(['/languages/'+this.language.id+'/show']);
                }
              );
            }

            this.shuffleArray(this.wordConfigs);

            let languageShortcut = this.language.dictionaryLanguage.codeForSpeech;
            let voice = undefined;
            if(languageShortcut == "en-US"){
              voice = "Google US English";
            }else if(languageShortcut == "en-UK"){
              voice = "Google UK English Female";
            }

            this.speech.init({
              'volume': 1,
              'lang': languageShortcut,
              'rate': 0.8,
              'pitch': 1,
              'voice': voice,
              'splitSentences': true,
              'listeners': {
                'onvoiceschanged': () => {
                }
              }
            });

            this.initializeSpeechToText();

          },
          error => {
            this.router.navigate(['/']);
          });

    });

  }

  countWordsToEnd() {
    let languageCopy: Language = Object.assign({}, this.language);

    let listOfIdWordConfig: number[] = [];

    languageCopy.categories.forEach(c => {
      c.wordConfigs.forEach(wc => {
        if (c.mode == Mode.EXERCISE && wc.done != 3 && listOfIdWordConfig.indexOf(wc.id) == -1) {
          listOfIdWordConfig.push(wc.id);
          this.countToWordsEnd += 1;
        } else if (c.mode == Mode.DICTIONARY) {
          if (wc.done == 0) {
            let languageToCategorySettings: Language = Object.assign({}, languageCopy);
            languageToCategorySettings.categorySettings = languageToCategorySettings.categorySettings.filter(cs => cs.mode == Mode.DICTIONARY);
            if (listOfIdWordConfig.indexOf(wc.id) == -1) {
              if (Method[languageToCategorySettings.categorySettings[0].method] == Method.BOTHSIDE) {
                listOfIdWordConfig.push(wc.id);
                this.countToWordsEnd += 2;
              } else {
                listOfIdWordConfig.push(wc.id);
                this.countToWordsEnd += 1;
              }
            }
          } else if ((wc.done == 1 || wc.done == 2) && listOfIdWordConfig.indexOf(wc.id) == -1) {
            listOfIdWordConfig.push(wc.id);
            this.countToWordsEnd += 1;
          }
        }
      });
    });
  }

  shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [array[i], array[j]] = [array[j], array[i]];
    }
  }

  focusInput(index: number): boolean {
    if (!this.isFocusInput) {
      this.isFocusInput = true;
      this.focusInputIndex = index;
    }
    if (this.focusInputIndex == index) {
      return true;
    }

    return false;

  }

  insertSpecialLetter(letter: string) {
    this.elementCaretPos.value += letter;
    this.elementCaretPos.focus();
  }

  getCaretPos(oField) {
    this.elementCaretPos = oField;
  }

  checkAnswer() {
    this.textToRead = "";
    this.phase = true;
    this.setDoneTo = 0;
    if (this.wordConfigs[0].categories[0].mode == Mode.DICTIONARY){
      for(let i = 0; i < this.wordConfigs[0].words.length; i++){
        if(this.wordConfigs[0].words[i].answer){
          this.textToRead += this.wordConfigs[0].words[i].word + ", ";
        }
      }

      let listOfAnswers: string[] = [];
      if (Method[this.currentMethod] == Method.FIRSTTOSECOND || (Method[this.currentMethod] == Method.BOTHSIDE && this.wordConfigs[0].done == 0)) {
        for(let i = 0; i < this.wordConfigs[0].words.length; i++){
          if($("#answerDIC_"+i).val() != undefined){
            listOfAnswers.push($("#answerDIC_"+i).val().toLowerCase());
          }
        }

        this.wordConfigs[0].words.forEach(w => {
          if(w.answer){
            let index = listOfAnswers.indexOf(w.word.toLowerCase());
            if(index != -1){
              w.correct = true;
              listOfAnswers.splice(index, 1);
            }
            else{
              w.correct = false;
            }
          }
        });

        if(listOfAnswers.length == 0){
          if (Method[this.currentMethod] == Method.FIRSTTOSECOND){
            this.setDoneTo = 3;
          }
          else if(Method[this.currentMethod] == Method.BOTHSIDE && this.wordConfigs[0].done == 0){
            this.setDoneTo = 1;
          }

          this.countToWordsEnd--;
        }


      } else if (Method[this.currentMethod] == Method.SECONDTOFIRST || (Method[this.currentMethod] == Method.BOTHSIDE && this.wordConfigs[0].done == 1)) {
        for(let i = 0; i < this.wordConfigs[0].words.length; i++){
          if($("#wordDIC_"+i).val() != undefined){
            listOfAnswers.push($("#wordDIC_"+i).val().toLowerCase());
          }
        }

        this.wordConfigs[0].words.forEach(w => {
          if(!w.answer){
            let index = listOfAnswers.indexOf(w.word.toLowerCase());
            if(index != -1){
              w.correct = true;
              listOfAnswers.splice(index, 1);
            }
            else{
              w.correct = false;
            }
          }
        });

        if(listOfAnswers.length == 0){
          this.setDoneTo = 3;
          this.countToWordsEnd--;

        }
      }
    }
    else if(this.wordConfigs[0].categories[0].mode == Mode.EXERCISE){
      for(let i = 0; i < this.wordConfigs[0].words.length; i++){
        this.textToRead += this.wordConfigs[0].words[i].word + " ";
        if(this.wordConfigs[0].words[i].answer && $("#answerEXE_"+i).val() != undefined && $("#answerEXE_"+i).val().toLowerCase() == this.wordConfigs[0].words[i].word.toLowerCase()){
          this.wordConfigs[0].words[i].correct = true;
        }
        else{
          this.wordConfigs[0].words[i].correct = false;
        }
      }

      let exeCorrect = true;
      this.wordConfigs[0].words.forEach(w => {
        if(w.answer && !w.correct){
          exeCorrect = false;
        }
      });

      if(exeCorrect){
        this.setDoneTo = 3;
        this.countToWordsEnd--;
      }
    }

    if(this.setDoneTo != 0){
      let wordConfig = new WordConfig();
      wordConfig.id =  this.wordConfigs[0].id;
      wordConfig.done = this.setDoneTo;

      this.languageService.changeDone(this.language.id, this.wordConfigs[0].categories[0].id, wordConfig)
        .subscribe(
          data => {
          },
          error => {
            this.router.navigate(['/languages/'+this.language.id+'/show']);
          });
    }

    this.readText();
  }

  nextWord(){
    if(this.countToWordsEnd <= 0){
        this.languageService.resetRepeating(this.language.id).subscribe(
          result => {
            this.router.navigate(['/languages/'+this.language.id+'/show']);
          },
          error => {
            this.router.navigate(['/languages/'+this.language.id+'/show']);
          }
        );
    }

    if(this.setDoneTo > 0) {
      this.wordConfigs[0].done = this.setDoneTo;
      this.wordConfigs = this.wordConfigs.filter(wc => wc.done != 3);
    }
    this.shuffleArray(this.wordConfigs);
    this.setDoneTo = 0;
    this.phase = false;
  }

  readText(){
    if(!this.speech.hasBrowserSupport()) {
      return;
    }
    if(!this.textToSpeech){
      return;
    }

    this.speech.speak({
      text: this.textToRead,
    }).then(() => {
    }).catch(() => {
    })

  }

  changeTextToSpeech(){
    this.textToSpeech = !this.textToSpeech;
    this.cookieService.set("TextToSpeech", String(this.textToSpeech), 365, '/');
  }

  changeSpeechToText(){
    this.speechToText = !this.speechToText;
    this.cookieService.set("SpeechToText", String(this.speechToText), 365, '/');
  }

  initializeSpeechToText(){
    const onAnythingSaid = () => {
    };

    const onEndEvent = () => {
    };

    const onFinalised = text => {
      this.insertSpeechText(text);
      this.listener.stopListening();
    };

    try {
      this.listener = new SpeechToText(onFinalised, onEndEvent, onAnythingSaid, this.language.dictionaryLanguage.codeForSpeech);
    } catch (error) {
    }
  }

  insertSpeechText(text: string) {
    this.elementCaretPos.value += text;
    this.elementCaretPos.focus();
  }

  startListening(){
    if(!this.speechToText){
      return;
    }
    this.listener.startListening();
  }

}
