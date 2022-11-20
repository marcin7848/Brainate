import { Component, OnInit } from '@angular/core';
import {Language} from "../../../model/Language";
import {Category} from "../../../model/Category";
import {Mode} from "../../../model/Mode";
import {AppService} from "../../../app.service";
import {ActivatedRoute, Router} from "@angular/router";
import {LanguageService} from "../../language.service";
import {HiddenWord} from "../../../model/HiddenWord";

@Component({
  selector: 'app-automatic-adding-words',
  templateUrl: './automatic-adding-words.component.html',
  styleUrls: ['./automatic-adding-words.component.scss']
})
export class AutomaticAddingWordsComponent implements OnInit {

  language: Language;
  category: Category;
  text: string;
  Mode = Mode;
  languages: Language[];
  submitted = false;
  loadingWords = true;

  constructor(private appService: AppService,
              private route: ActivatedRoute,
              private languageService: LanguageService,
              private router: Router) {
    this.text = "";
  }

  ngOnInit() {
    this.appService.check();
    this.route.params.subscribe(params => {
      const id: number = +params['id'];
      const idCategory: number = +params['idCategory'];

      this.languageService.getLanguageAndCategory(id, idCategory)
        .subscribe(
          data => {
            this.language = data;
            this.category = this.language.categories[0];
            this.language.categories.splice(0, 1);
          },
          error => {
            this.router.navigate(['/']);
          });

      this.languageService.getAllLanguagesAutomatic(id, idCategory)
        .subscribe(
          data => {
            this.languages = data;
            this.languages.forEach(l => {
              l.categories.forEach(c => {
                let wordConfigsToHide = c.wordConfigs.filter(wc => wc.accepted == false);
                wordConfigsToHide.forEach(wc => {
                  let hiddenWord: HiddenWord = new HiddenWord(l.id, c.id, wc.id);
                  this.languageService.hideWord(l.id, c.id, hiddenWord).subscribe(
                    data => {
                    },
                    error => {
                    });
                });

                c.wordConfigs = c.wordConfigs.filter(wc => wc.accepted == true);
                c.wordConfigs.sort((a,b) => a.id - b.id);
                c.wordConfigs.forEach(wc => {
                  wc.words.sort((a,b) => a.seat - b.seat);
                });
              });
            });

            this.loadingWords = false;
          },
          error => {
          });

    });
  }
  getParentCategoryName(category: Category): string {
    if (category) {
      if (category.idParent == 0) {
        return "-";
      }
      for (let cat of this.language.categories) {
        if (cat.id == category.idParent) {
          return cat.name;
        }
      }
    }
  }

  translateWords(){
    if(this.text.length == 0){
      this.appService.openSnackBar("You have to provide the text to translate!");
      return;
    }

    this.submitted = true;

    this.languageService.automaticAddingWords(this.language.id, this.category.id, this.text).subscribe(
      data => {
        this.router.navigate(['/languages/'+this.language.id+'/categories/'+this.category.id+'/show']);
      },
      error => {
        this.submitted = false;
        this.appService.openSnackBar(error["error"]["error"]);
      });

  }

  hideWord(languageIndex: number, categoryIndex: number, wordConfigIndex: number,
           id_language: number, id_category: number, id_wordConfig: number){

    if(wordConfigIndex != -1){
      this.languages[languageIndex].categories[categoryIndex].wordConfigs.splice(wordConfigIndex, 1);
    }
    else if(categoryIndex != -1){
      this.languages[languageIndex].categories.splice(categoryIndex, 1);
    }
    else{
      this.languages.splice(languageIndex, 1);
    }

    let hiddenWord: HiddenWord = new HiddenWord(id_language, id_category, id_wordConfig);

    this.languageService.hideWord(this.language.id, this.category.id, hiddenWord).subscribe(
      data => {
      },
      error => {
        this.appService.openSnackBar(error["error"]["error"]);
      });
  }

  copyWord(languageIndex: number, categoryIndex: number, wordConfigIndex: number,
           id_language: number, id_category: number, id_wordConfig: number){

    let hiddenWords:HiddenWord[] = [];

    if(wordConfigIndex != -1){
      let hiddenWord: HiddenWord = new HiddenWord(id_language, id_category, id_wordConfig);
      hiddenWords.push(hiddenWord);
      this.languages[languageIndex].categories[categoryIndex].wordConfigs.splice(wordConfigIndex, 1);
    }
    else {
      this.languages[languageIndex].categories[categoryIndex].wordConfigs.forEach(wc => {
          let hiddenWord: HiddenWord = new HiddenWord(id_language, id_category, wc.id);
          hiddenWords.push(hiddenWord);
        });

      this.languages[languageIndex].categories.splice(categoryIndex, 1);
    }

    this.languageService.copyWords(this.language.id, this.category.id, hiddenWords).subscribe(
      data => {
      },
      error => {
        this.appService.openSnackBar(error["error"]["error"]);
      });
  }

}
