import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Language} from "../../../model/Language";
import {Category} from "../../../model/Category";
import {MatDialog, MatPaginator, MatSort, MatTableDataSource} from "@angular/material";
import {Mode} from "../../../model/Mode";
import {LanguageService} from "../../language.service";
import {AppService} from "../../../app.service";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {DialogWordDeleteComponent} from "./dialog-word-delete/dialog-word-delete.component";
import {Mechanism} from "../../../model/Mechanism";
import {WordConfig} from "../../../model/WordConfig";
import {Word} from "../../../model/Word";

class WordData {
  id: number;
  word: ShowWord[];
  comment: string;
  thisUnit: boolean;

 constructor(id?:number, word?:ShowWord[], comment?:string, thisUnit?:boolean){
   this.id = id;
   this.word = word;
   this.comment = comment;
   this.thisUnit = thisUnit;
 }

}

class ShowWord {
  word: string;
  answer: boolean;

  constructor(word?:string, answer?:boolean){
    this.word = word;
    this.answer = answer;
  }
}

class WordInput {
  word: string;
  basicWord: string;
  answer: boolean;
  seat: number;

  constructor(word?:string, basicWord?: string, answer?:boolean, seat?:number) {
    this.word = word;
    this.basicWord = basicWord;
    this.answer = answer;
    this.seat = seat;
  }

}

@Component({
  selector: 'app-words-table',
  templateUrl: './words-table.component.html',
  styleUrls: ['./words-table.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ]
})
export class WordsTableComponent implements OnInit {

  @Input() languageGetter: Language;
  @Input() categoryGetter: Category;

  language: Language;
  category: Category;

  displayedColumns: string[] = ['id', 'word', 'comment', 'thisUnit'];
  dataSource: MatTableDataSource<WordData>;
  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  expandedElement: WordData | null;

  editWordButtonText:string = "Show edit word";
  editWordShowActive:boolean = false;
  submitted = false;

  Mode = Mode;
  Mechanism = Mechanism;
  wordInputs: WordInput[];
  comment: string = "";
  mechanism: Mechanism = Mechanism.BASIC;

  elementCaretPos = null;

  constructor(private languageService: LanguageService,
              private appService: AppService,
              public dialog: MatDialog) {
  }

  ngOnInit() {
    if(!this.categoryGetter){
      setTimeout(() => this.ngOnInit(), 1);
    }
    else{
      this.language = Object.assign({}, this.languageGetter);
      this.category = Object.assign({}, this.categoryGetter);
      this.assignData();
    }
  }

  assignData(){
    this.category.wordConfigs.sort((a,b) => a.id - b.id);
    this.category.wordConfigs = this.category.wordConfigs.filter(wc => wc.accepted);
    this.dataSource = new MatTableDataSource(this.createWordTable());
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(filterValue: string) {
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  createWordTable() : WordData[] {
    let wordData: WordData[] = [];
    this.category.wordConfigs.forEach(wc => {
      wc.words.sort((a, b) => a.seat - b.seat);

      let showWords: ShowWord[] = [];
      if(this.category.mode == Mode.DICTIONARY){
        wc.words.forEach(w => {
          let wordToShow = new ShowWord(w.word, w.answer);
          showWords.push(wordToShow);
        });

        showWords = showWords.sort((a, b) => Number(a.answer)- Number(b.answer));
      }
      else {
        wc.words.forEach(w => {
          let wordToShow: ShowWord;
          if(!w.answer){
            wordToShow = new ShowWord(w.word, w.answer);
          }else{
            wordToShow = new ShowWord(w.word + " (" + w.basicWord + ")", w.answer);
          }
          showWords.push(wordToShow);
        });
      }


      let word : WordData = new WordData(wc.id, showWords, wc.comment, wc.thisUnit);
      wordData.push(word);
    });
    return wordData;
  }

  changeThisUnit(id: number){
    this.languageService.changeThisUnit(this.language.id, this.category.id, id).subscribe(
      data => {
      },
      error => {
        this.appService.openSnackBar(error["error"]["error"]);
      });
  }

  showDeleteWord(idWordConfig: number) {
    const dialogRef = this.dialog.open(DialogWordDeleteComponent, {
      width: '280px',
      position: {
        top: '50px'
      },

      data: {idLanguage: this.language.id, idCategory: this.category.id, idWordConfig: idWordConfig}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result["deleted"] == true) {
        this.category.wordConfigs.splice(this.category.wordConfigs.findIndex(wc => wc.id == idWordConfig), 1);
        this.submitted = true;
        this.assignData();
        this.submitted = false;
      }
    });
  }

  showEditWord(idWordConfig?: number, setToFalse?:boolean){
    this.editWordShowActive = setToFalse ? false : !this.editWordShowActive;
    if(this.editWordShowActive){
      this.editWordButtonText = "Hide edit word";
      this.prepareShowEdit(idWordConfig);
    }else{
      this.editWordButtonText = "Show edit word";
    }
  }

  prepareShowEdit(idWordConfig: number){
    this.wordInputs = [];
    let wordConfig: WordConfig = this.category.wordConfigs[this.category.wordConfigs.findIndex(wc => wc.id == idWordConfig)];
    this.mechanism = wordConfig.mechanism;
    this.comment = wordConfig.comment;

    wordConfig.words.forEach(w => {
      let wordInput = new WordInput(w.word, w.basicWord, w.answer, w.seat);
      this.wordInputs.push(wordInput);
    });

  }

  addNewInputWord(answer:boolean){
    let wordInput = new WordInput("", "", answer, this.wordInputs.length);
    this.wordInputs.push(wordInput);
  }

  removeInputWord(index: number){
    this.wordInputs.splice(index, 1);
    this.sortSeats();
  }

  changeSeatInputWord(oldSeat: number, newSeat: number){
    if(newSeat == -1 || newSeat == this.wordInputs.length){
      return;
    }

    this.wordInputs[oldSeat].seat = newSeat;
    this.wordInputs[newSeat].seat = oldSeat;
    this.wordInputs.sort((a,b) => a.seat - b.seat);
  }

  sortSeats(){
    let i = 0;
    this.wordInputs.forEach(wi => wi.seat = i++);
  }

  editWord(idWordConfig:number){
    let counterWords = 0;
    let counterAnswers = 0;
    let existsBasicAnswers = true;
    let validateForm = true;
    this.wordInputs.forEach(wi => {
      wi.answer ? counterAnswers+=1 : counterWords+=1;
      if(wi.answer && wi.basicWord.length == 0){
        existsBasicAnswers = false;
      }
      if(wi.word.length == 0){
        validateForm = false;
      }
    });
    if(counterWords == 0 || counterAnswers == 0){
      this.appService.openSnackBar("There has to be at least 1 word and 1 answer!");
      return;
    }

    if(!validateForm){
      this.appService.openSnackBar("You need to enter each word and answer!");
      return;
    }

    if(!existsBasicAnswers && this.category.mode == Mode.EXERCISE){
      this.appService.openSnackBar("You need to enter basic word for each answer!");
      return;
    }

    let index = this.category.wordConfigs.findIndex(wc => wc.id == idWordConfig);
    let wordConfig : WordConfig = this.category.wordConfigs[index];
    wordConfig.comment = this.comment;
    wordConfig.mechanism = this.mechanism;
    let words : Word[] = [];
    this.wordInputs.forEach(wi => {
      let word : Word = new Word(wi.word, wi.basicWord, wi.seat, true, wi.answer);
      words.push(word);
    });

    wordConfig.words = words;

    this.submitted = true;
    this.languageService.editWord(this.language.id, this.category.id, idWordConfig, wordConfig).subscribe(
      data => {
        this.category.wordConfigs.splice(index, 1);
        this.category.wordConfigs.push(data);
        this.showEditWord(0, true);
        this.assignData();
        this.submitted = false;

      },
      error => {
        this.submitted = false;
        this.appService.openSnackBar(error["error"]["error"]);
      });
  }

  findWord(text: string){
    if(text.length < 0){
      return;
    }

    this.category.wordConfigs = this.category.wordConfigs.filter(wc => !wc.accepted);

    this.languageService.searchWord(this.language.id, this.category.id, text).subscribe(
      data => {
        data.forEach(wc => this.category.wordConfigs.push(wc));
        this.category.wordConfigs = this.category.wordConfigs.filter((test, index, array) =>
          index === array.findIndex((findTest) => findTest.id === test.id));
        this.assignData();

      },
      error => {
        this.appService.openSnackBar(error["error"]["error"]);
      });

  }

  insertSpecialLetter(letter: string) {
    this.elementCaretPos.value += letter;
    this.elementCaretPos.focus();
  }

  getCaretPos(oField) {
    this.elementCaretPos = oField;
  }

}
