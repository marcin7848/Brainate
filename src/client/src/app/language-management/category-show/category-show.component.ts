import {Component, OnInit} from '@angular/core';
import {AppService} from "../../app.service";
import {ActivatedRoute, Router} from "@angular/router";
import {LanguageService} from "../language.service";
import {Language} from "../../model/Language";
import {Category} from "../../model/Category";
import {MatDialog} from "@angular/material";
import {DialogCategoryDeleteComponent} from "./dialog-category-delete/dialog-category-delete.component";
import {DialogCategoryEditComponent} from "./dialog-category-edit/dialog-category-edit.component";

@Component({
  selector: 'app-category-show',
  templateUrl: './category-show.component.html',
  styleUrls: ['./category-show.component.scss']
})
export class CategoryShowComponent implements OnInit {

  language: Language;
  category: Category;

  constructor(private appService: AppService,
              private route: ActivatedRoute,
              private languageService: LanguageService,
              private router: Router,
              public dialog: MatDialog) {
  }

  ngOnInit() {
    this.appService.check();
    this.route.params.subscribe(params => {
      const id: number = +params['id'];
      const idCategory: number = +params['idCategory'];

      this.languageService.getLanguageWithCategoryAndWords(id, idCategory)
        .subscribe(
          data => {
            this.language = data;
            this.category = this.language.categories[0];
            this.language.categories.splice(0, 1);
          },
          error => {
            this.router.navigate(['/']);
          });

    });

  }

  showEditCategory() {
    let listOfChildrenCategories:Category[] = [];
    this.createListOfAllCategoryChildren(this.category, listOfChildrenCategories);
    let possibleParentCategories:Category[] = [];

    this.language.categories.forEach(c => {
      let exist:boolean = false;
      listOfChildrenCategories.forEach(chc => {
        if(c.id == chc.id || c.defaultCategory){
          exist = true;
        }
      });

      if(!exist){
        possibleParentCategories.push(c);
      }
    });

    const dialogRef = this.dialog.open(DialogCategoryEditComponent, {
      width: '280px',
      position: {
        top: '50px'
      },

      data: {idLanguage: this.language.id, category: this.category, possibleParent: possibleParentCategories}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result["edited"] == true) {
        this.ngOnInit();
      }
    });


  }

  showDeleteCategory() {
    const dialogRef = this.dialog.open(DialogCategoryDeleteComponent, {
      width: '280px',
      position: {
        top: '50px'
      },

      data: {idLanguage: this.language.id, idCategory: this.category.id, name: this.category.name}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result["deleted"] == true) {
        this.router.navigate(['/languages/'+this.language.id+'/show']);
      }
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

  createListOfAllCategoryChildren(category: Category, createdListOfCategories:Category[]): void{
    createdListOfCategories.push(category);
    this.language.categories.forEach(c => {
      if(c.idParent == category.id){
        this.createListOfAllCategoryChildren(c, createdListOfCategories);
      }
    });
  }


}
