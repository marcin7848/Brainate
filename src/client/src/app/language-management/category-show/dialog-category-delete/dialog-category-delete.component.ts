import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {LanguageService} from "../../language.service";
import {AppService} from "../../../app.service";

@Component({
  selector: 'app-dialog-category-delete',
  templateUrl: './dialog-category-delete.component.html',
  styleUrls: ['./dialog-category-delete.component.scss']
})
export class DialogCategoryDeleteComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<DialogCategoryDeleteComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogCategoryDeleteData,
              private languageService: LanguageService,
              private appService: AppService) { }

  ngOnInit() {
  }

  onNoClick(): void {
    this.dialogRef.close({deleted: false});
  }

  delete(){
    this.languageService.deleteCategory(this.data.idLanguage, this.data.idCategory).subscribe(
      data => {
        this.dialogRef.close({deleted: true});
      },
      error => {
        this.appService.openSnackBar("Something went wrong!");
        this.dialogRef.close({deleted: false});
      });
  }

}

export class DialogCategoryDeleteData{
  idLanguage: number;
  idCategory: number;
  name: string;
}
