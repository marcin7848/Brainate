import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {LanguageService} from "../../../language.service";
import {AppService} from "../../../../app.service";

@Component({
  selector: 'app-dialog-word-delete',
  templateUrl: './dialog-word-delete.component.html',
  styleUrls: ['./dialog-word-delete.component.scss']
})
export class DialogWordDeleteComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<DialogWordDeleteComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogWordDeleteData,
              private languageService: LanguageService,
              private appService: AppService) { }

  ngOnInit() {
  }

  onNoClick(): void {
    this.dialogRef.close({deleted: false});
  }

  delete(justForThisCategory: boolean){
    this.languageService.deleteWord(this.data.idLanguage, this.data.idCategory, this.data.idWordConfig, justForThisCategory).subscribe(
      data => {
        this.dialogRef.close({deleted: true});
      },
      error => {
        this.appService.openSnackBar("Something went wrong!");
        this.dialogRef.close({deleted: false});
      });
  }

}

export class DialogWordDeleteData{
  idLanguage: number;
  idCategory: number;
  idWordConfig: number;
}

