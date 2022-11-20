import {Component, Inject, OnInit} from '@angular/core';
import {MAT_SNACK_BAR_DATA} from "@angular/material";

@Component({
  selector: 'app-snack-bar',
  templateUrl: './snack-bar.component.html',
  styleUrls: ['./snack-bar.component.scss']
})
export class SnackBarComponent implements OnInit {

  text: string;
  constructor(@Inject(MAT_SNACK_BAR_DATA) public data: string) {
    this.text = data;
  }

  ngOnInit() {
  }

}
