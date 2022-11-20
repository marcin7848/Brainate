import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {RegisterComponent} from "./account-management/register/register.component";
import {LoginComponent} from "./account-management/login/login.component";
import {LogoutComponent} from "./account-management/logout/logout.component";
import {LanguageAddingComponent} from "./language-management/language-adding/language-adding.component";
import {LanguageShowComponent} from "./language-management/language-show/language-show.component";
import {LanguageEditComponent} from "./language-management/language-edit/language-edit.component";
import {CategoryShowComponent} from "./language-management/category-show/category-show.component";
import {AutomaticAddingWordsComponent} from "./language-management/category-show/automatic-adding-words/automatic-adding-words.component";
import {RepeatingWordsComponent} from "./language-management/language-show/repeating-words/repeating-words.component";
import {TasksEditComponent} from "./language-management/tasks-edit/tasks-edit.component";
import {TasksViewComponent} from "./language-management/tasks-view/tasks-view.component";

const routes: Routes = [
  {
    path : '',
    component : TasksViewComponent
  },
  {
    path : 'accounts',
    children : [
      {
        path : 'register',
        component : RegisterComponent
      },
      {
        path : 'login',
        component : LoginComponent
      },
      {
        path : 'logout',
        component : LogoutComponent
      }
    ]
  },
  {
    path : 'languages',
    children : [
      {
        path : 'add',
        component : LanguageAddingComponent
      },
      {
        path : ':id',
        children : [
          {
            path : 'show',
            component : LanguageShowComponent
          },
          {
            path : 'edit',
            component : LanguageEditComponent
          },
          {
            path : 'categories',
            children : [
              {
                path : ':idCategory',
                children : [
                  {
                    path : 'show',
                    component : CategoryShowComponent
                  },
                  {
                    path : 'automatic',
                    children : [
                      {
                        path : 'show',
                        component : AutomaticAddingWordsComponent
                      }
                    ]
                  }
                ]
              }
            ]
          },
          {
            path : 'repeating',
            component : RepeatingWordsComponent
          },
          {
            path : 'tasksEdit',
            component : TasksEditComponent
          },
        ]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
