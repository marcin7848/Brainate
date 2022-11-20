import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LanguageAddingComponent } from './language-adding/language-adding.component';
import {
  MatButtonModule,
  MatCardModule, MatCheckboxModule, MatDialogModule, MatDividerModule, MatExpansionModule,
  MatFormFieldModule, MatIconModule,
  MatInputModule, MatListModule, MatPaginatorModule,
  MatProgressSpinnerModule, MatSelectModule,
  MatSlideToggleModule, MatSnackBarModule, MatSortModule, MatTableModule, MatTooltipModule, MatTreeModule
} from "@angular/material";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RouterModule} from "@angular/router";
import { LanguageShowComponent } from './language-show/language-show.component';
import { LanguageEditComponent } from './language-edit/language-edit.component';
import { DialogDeleteLanguageComponent } from './language-show/dialog-delete-language/dialog-delete-language.component';
import {SnackBarComponent} from "../snack-bar/snack-bar.component";
import { CategoryTreeComponent } from './language-show/category-tree/category-tree.component';
import { CategoryShowComponent } from './category-show/category-show.component';
import { DialogCategoryDeleteComponent } from './category-show/dialog-category-delete/dialog-category-delete.component';
import { DialogCategoryEditComponent } from './category-show/dialog-category-edit/dialog-category-edit.component';
import { WordAddComponent } from './category-show/words-table/word-add/word-add.component';
import { DialogWordDeleteComponent } from './category-show/words-table/dialog-word-delete/dialog-word-delete.component';
import {WordsTableComponent} from "./category-show/words-table/words-table.component";
import {ClickStopPropagation} from "../ClickStopPropagation";
import { AutomaticAddingWordsComponent } from './category-show/automatic-adding-words/automatic-adding-words.component';
import { DialogStartRepeatingComponent } from './language-show/dialog-start-repeating/dialog-start-repeating.component';
import { RepeatingWordsComponent } from './language-show/repeating-words/repeating-words.component';
import { TasksEditComponent } from './tasks-edit/tasks-edit.component';
import { TasksViewComponent } from './tasks-view/tasks-view.component';

@NgModule({
  declarations: [LanguageAddingComponent, LanguageShowComponent, LanguageEditComponent, DialogDeleteLanguageComponent,
    CategoryTreeComponent, CategoryShowComponent, DialogCategoryDeleteComponent, DialogCategoryEditComponent, WordAddComponent, WordsTableComponent, DialogWordDeleteComponent,
    ClickStopPropagation,
    AutomaticAddingWordsComponent,
    DialogStartRepeatingComponent,
    RepeatingWordsComponent,
    TasksEditComponent,
    TasksViewComponent],
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSlideToggleModule,
    MatDialogModule,
    MatDividerModule,
    MatListModule,
    MatSelectModule,
    MatTreeModule,
    MatCheckboxModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatExpansionModule
  ],
  entryComponents: [DialogDeleteLanguageComponent, SnackBarComponent, DialogCategoryDeleteComponent,
    DialogCategoryEditComponent, DialogWordDeleteComponent, DialogStartRepeatingComponent]
})
export class LanguageManagementModule { }
