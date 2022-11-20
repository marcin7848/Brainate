import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AppService} from "../../app.service";
import {LanguageService} from "../language.service";
import {AppComponent} from "../../app.component";
import {ActivatedRoute, Router} from "@angular/router";
import {DictionaryLanguage} from "../../model/DictionaryLanguage";
import {SpecialLetter} from "../../model/SpecialLetter";
import {Language} from "../../model/Language";
import {Account} from "../../model/Account";
import {Location} from '@angular/common';

@Component({
  selector: 'app-language-edit',
  templateUrl: './language-edit.component.html',
  styleUrls: ['./language-edit.component.scss']
})
export class LanguageEditComponent implements OnInit {

  languageForm: FormGroup;
  submitted: boolean;
  errorMessage: string;
  hidden: boolean;
  items: FormArray;
  language: Language;

  constructor(private appService: AppService, private languageService: LanguageService,
              private formBuilder: FormBuilder,
              private appComponent: AppComponent,
              private router: Router,
              private route: ActivatedRoute,
              private location: Location) {
    this.languageForm = this.formBuilder.group({
      name: ['', Validators.required],
      languageShortcut: ['', Validators.required],
      codeForTranslator: [''],
      codeForSpeech: [''],
      items: this.formBuilder.array([])
    });
  }

  ngOnInit() {
    this.appService.check();
    this.submitted = false;
    this.errorMessage = "";

    this.route.params.subscribe(params => {
      const id: number = +params['id'];

      this.languageService.getLanguageById(id)
        .subscribe(
          data => {
            this.language = data;
            this.f.name.setValue(this.language.dictionaryLanguage.name);
            this.f.languageShortcut.setValue(this.language.dictionaryLanguage.languageShortcut);
            this.f.codeForTranslator.setValue(this.language.dictionaryLanguage.codeForTranslator);
            this.f.codeForSpeech.setValue(this.language.dictionaryLanguage.codeForSpeech);
            this.hidden = this.language.hidden;
            this.language.dictionaryLanguage.specialLetters.forEach(sl => {
              this.items = this.languageForm.get('items') as FormArray;
              this.items.push(this.formBuilder.group({
                letter: sl.letter
              }));
            });
          },
          error => {
            this.router.navigate(['/']);
          });
    });
  }

  get f() {
    return this.languageForm.controls;
  }

  createItem(): FormGroup {
    return this.formBuilder.group({
      letter: ''
    });
  }

  addItem(): void {
    this.items = this.languageForm.get('items') as FormArray;
    this.items.push(this.createItem());
  }

  removeItem(index: number){
    this.items.removeAt(index);
  }

  edit() {
    this.submitted = true;
    if (this.languageForm.invalid) {
      this.submitted = false;
      this.errorMessage = "Invalid form!";
      return;
    }

    let dictionaryLanguage: DictionaryLanguage = new DictionaryLanguage(this.f.name.value, this.f.languageShortcut.value,
      this.f.codeForTranslator.value, this.f.codeForSpeech.value, <SpecialLetter[]>this.f.items.value);
    let newLanguage: Language = new Language(this.hidden, this.language.started, new Account(), dictionaryLanguage);

    this.languageService.updateLanguage(this.language.id, newLanguage)
      .subscribe(
        data => {
          this.appComponent.ngOnInit();
          this.location.back();
        },
        error => {
          this.errorMessage = error["error"]["error_description"];
          this.submitted = false;
        });
  }

  back() {
    this.location.back();
  }

}
