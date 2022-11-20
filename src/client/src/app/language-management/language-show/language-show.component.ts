import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormBuilder} from "@angular/forms";
import {AccountService} from "../../account-management/account.service";
import {CookieService} from "ngx-cookie-service";
import {ActivatedRoute, Router} from "@angular/router";
import {LanguageService} from "../language.service";
import {AppService} from "../../app.service";
import {Language} from "../../model/Language";
import {MatDialog} from "@angular/material";
import {DialogDeleteLanguageComponent} from "./dialog-delete-language/dialog-delete-language.component";
import {AppComponent} from "../../app.component";
import {Mode} from "../../model/Mode";
import {Method} from "../../model/Method";
import {SelectionModel} from "@angular/cdk/collections";
import {Category} from "../../model/Category";
import {DialogStartRepeatingComponent} from "./dialog-start-repeating/dialog-start-repeating.component";

interface Node {
  id: number;
  name: string;
  mode: Mode,
  language: Language;
  idParent: number;
  defaultCategory: boolean;
  children?: Node[];
}

@Component({
  selector: 'app-language-show',
  templateUrl: './language-show.component.html',
  styleUrls: ['./language-show.component.scss']
})
export class LanguageShowComponent implements OnInit {

  language: Language;
  Mode = Mode;
  Method = Method;
  methods = [];
  @Output() messageEventTree = new EventEmitter<Language>();
  checklistSelection = new SelectionModel<Node>(true);

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService,
              private cookieService: CookieService,
              private router: Router,
              private languageService: LanguageService,
              private appService: AppService,
              private route: ActivatedRoute,
              public dialog: MatDialog,
              private appComponent: AppComponent) {
  }

  ngOnInit() {
    this.appService.check();
    this.route.params.subscribe(params => {
      const id: number = +params['id'];

      this.methods = this.toArray(Method);

      this.languageService.getLanguageById(id)
        .subscribe(
          data => {
            this.language = data;

            this.messageEventTree.emit(this.language);
            this.language.categories.forEach(c => c.idParent = c.idParent == 0 ? null : c.idParent);
          },
          error => {
            this.router.navigate(['/']);
          });

    });
  }

  toArray(enumme) {
    return Object.keys(enumme)
      .map(key => {
        return {method: key, name: enumme[key]}
      });
  }

  showDeleteLanguage(): void {
    const dialogRef = this.dialog.open(DialogDeleteLanguageComponent, {
      width: '280px',
      position: {
        top: '50px'
      },

      data: {id: this.language.id, name: this.language.dictionaryLanguage.name}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result["deleted"] == true) {
        this.appComponent.ngOnInit();
        this.router.navigate(['/']);
      }
    });
  }

  editCategorySetting(indexCategorySetting: number) {
    this.languageService.editCategorySettings(this.language.id, this.language.categorySettings[indexCategorySetting].id,
      this.language.categorySettings[indexCategorySetting])
      .subscribe();
  }

  resetThisUnit() {
    if (this.checklistSelection.selected.length == 0) {
      this.appService.openSnackBar("You have to choose 1 category at least!");
      return;
    }

    let categories: Category[] = [];
    this.checklistSelection.selected.forEach(c => {
      let category: Category = new Category();
      category.id = c.id;
      categories.push(category);
    });

    this.languageService.resetThisUnit(this.language.id, categories).subscribe(
      result => {
        this.appService.openSnackBar("This Unit has been reset!");
      },
      error => {
        this.appService.openSnackBar(error["error"]["error"]);
      }
    );
  }

  resetWords() {
    if (this.checklistSelection.selected.length == 0) {
      this.appService.openSnackBar("You have to choose 1 category at least!");
      return;
    }

    let categories: Category[] = [];
    this.checklistSelection.selected.forEach(c => {
      let category: Category = new Category();
      category.id = c.id;
      categories.push(category);
    });

    this.languageService.resetWords(this.language.id, categories).subscribe(
      result => {
        this.appService.openSnackBar("Words have been reset!");
      },
      error => {
        this.appService.openSnackBar(error["error"]["error"]);
      }
    );
  }

  showStartRepeating(): void {
    if (this.checklistSelection.selected.length == 0) {
      this.appService.openSnackBar("You have to choose 1 category at least!");
      return;
    }

    let categories: Category[] = [];
    this.checklistSelection.selected.forEach(c => {
      let category: Category = new Category();
      category.id = c.id;
      categories.push(category);
    });

    const dialogRef = this.dialog.open(DialogStartRepeatingComponent, {
      width: '280px',
      position: {
        top: '50px'
      },

      data: {id: this.language.id, categoriesWithJustId: categories}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result["started"] == true) {
        this.router.navigate(['/languages/' + this.language.id + '/repeating']);
      }
    });
  }

  resetRepeating() {
    this.languageService.resetRepeating(this.language.id).subscribe(
      result => {
        this.ngOnInit();
      },
      error => {
        this.appService.openSnackBar(error["error"]["error"]);
      }
    );
  }


}
