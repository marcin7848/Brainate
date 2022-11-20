import {Component, Inject, OnInit} from '@angular/core';
import {Category} from "../../../model/Category";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {LanguageService} from "../../language.service";
import {AppService} from "../../../app.service";

@Component({
  selector: 'app-dialog-category-edit',
  templateUrl: './dialog-category-edit.component.html',
  styleUrls: ['./dialog-category-edit.component.scss']
})
export class DialogCategoryEditComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<DialogCategoryEditComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogCategoryEditData,
              private languageService: LanguageService,
              private appService: AppService) { }

  ngOnInit() {
  }

  onNoClick(): void {
    this.dialogRef.close({edited: false});
  }

  edit(name: string, idParent: number){
    if(name == ""){
      this.appService.openSnackBar("Name cannot be empty!");
      return;
    }
    this.data.category.name = name;
    this.data.category.idParent = idParent;

    this.languageService.editCategory(this.data.idLanguage, this.data.category.id, this.data.category).subscribe(
      data => {
        this.dialogRef.close({edited: true});
      },
      error => {
        this.appService.openSnackBar(error["error"]["error"]);
        this.dialogRef.close({edited: false});
      });
  }
}

export class DialogCategoryEditData{
  idLanguage: number;
  category: Category;
  possibleParent: Category[];
}
