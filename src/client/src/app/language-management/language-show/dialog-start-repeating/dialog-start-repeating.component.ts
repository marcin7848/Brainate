import {Component, Inject, OnInit} from '@angular/core';
import {Category} from "../../../model/Category";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {LanguageService} from "../../language.service";
import {AppService} from "../../../app.service";

@Component({
  selector: 'app-dialog-start-repeating',
  templateUrl: './dialog-start-repeating.component.html',
  styleUrls: ['./dialog-start-repeating.component.scss']
})
export class DialogStartRepeatingComponent implements OnInit {

  numberOfWords: number = 1;
  submitted = false;

  constructor(public dialogRef: MatDialogRef<DialogStartRepeatingComponent>,
              @Inject(MAT_DIALOG_DATA) public data: DialogStartRepeatingData,
              private languageService: LanguageService,
              private appService: AppService) { }

  ngOnInit() {
  }

  onNoClick(): void {
    this.dialogRef.close({started: false});
  }

  start(){
    if(isNaN(Number(this.numberOfWords)) || Number(this.numberOfWords) <= 0){
      this.appService.openSnackBar("You have to provide number greater than 0!");
      return;
    }

    this.submitted = true;
    this.languageService.startRepeating(this.data.id, this.data.categoriesWithJustId, this.numberOfWords).subscribe(
      data => {
        this.dialogRef.close({started: true});
      },
      error => {
        this.appService.openSnackBar("Something went wrong! Try again!");
      });
  }


}


export class DialogStartRepeatingData{
  id: number;
  categoriesWithJustId: Category[];
}
