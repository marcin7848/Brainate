import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {LanguageService} from "../../language.service";

@Component({
  selector: 'app-dialog-delete-language',
  templateUrl: './dialog-delete-language.component.html',
  styleUrls: ['./dialog-delete-language.component.scss']
})
export class DialogDeleteLanguageComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<DialogDeleteLanguageComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogDeleteLanguageData,
              private languageService: LanguageService) { }

  ngOnInit() {
  }

  onNoClick(): void {
    this.dialogRef.close({deleted: false});
  }

  delete(){
    this.languageService.deleteLanguage(this.data.id).subscribe(
      data => {
        this.dialogRef.close({deleted: true});
      },
      error => {
        this.dialogRef.close({deleted: false});
      });
  }
}

export class DialogDeleteLanguageData{
  id: number;
  name: string;
}
