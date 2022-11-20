import {Component, OnInit} from '@angular/core';
import {AppService} from "../../app.service";
import {LanguageService} from "../language.service";
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Language} from "../../model/Language";
import {Account} from "../../model/Account";
import {DictionaryLanguage} from "../../model/DictionaryLanguage";
import {SpecialLetter} from "../../model/SpecialLetter";
import {AppComponent} from "../../app.component";
import {Router} from "@angular/router";

@Component({
  selector: 'app-language-adding',
  templateUrl: './language-adding.component.html',
  styleUrls: ['./language-adding.component.scss']
})
export class LanguageAddingComponent implements OnInit {

  languageForm: FormGroup;
  submitted: boolean;
  errorMessage: string;
  hidden: boolean;
  items: FormArray;

  constructor(private appService: AppService, private languageService: LanguageService,
              private formBuilder: FormBuilder,
              private appComponent: AppComponent,
              private router: Router) {
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

  add() {
    this.submitted = true;
    if (this.languageForm.invalid) {
      this.submitted = false;
      this.errorMessage = "Invalid form!";
      return;
    }

    let dictionaryLanguage: DictionaryLanguage = new DictionaryLanguage(this.f.name.value, this.f.languageShortcut.value,
      this.f.codeForTranslator.value, this.f.codeForSpeech.value, <SpecialLetter[]>this.f.items.value);
    let language: Language = new Language(this.hidden, false, new Account(), dictionaryLanguage);

    this.languageService.addLanguage(language)
      .subscribe(
        data => {
          this.appComponent.ngOnInit();
          this.router.navigate(['/']);
        },
        error => {
          this.errorMessage = error["error"]["error_description"];
          this.submitted = false;
        });
  }


}
