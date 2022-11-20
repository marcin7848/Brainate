import { Component, OnInit } from '@angular/core';
import {Language} from "../../model/Language";
import {LanguageService} from "../language.service";
import {AppService} from "../../app.service";
import {Method} from "../../model/Method";
import {ActivatedRoute, Router} from "@angular/router";
import {TaskMode} from "../../model/TaskMode";

@Component({
  selector: 'app-tasks-edit',
  templateUrl: './tasks-edit.component.html',
  styleUrls: ['./tasks-edit.component.scss']
})
export class TasksEditComponent implements OnInit {

  language: Language;
  TaskMode = TaskMode;

  constructor(private languageService: LanguageService,
              private appService: AppService,
              private route: ActivatedRoute,
              private router: Router) { }

  ngOnInit() {
    this.appService.check();
    this.route.params.subscribe(params => {
      const id: number = +params['id'];

      this.languageService.getLanguageById(id)
        .subscribe(
          data => {
            this.language = data;
            this.language.tasks.sort((a, b) => a.id - b.id);
          },
          error => {
            this.router.navigate(['/']);
          });

    });
  }

  editTasks(){
    for(let task of this.language.tasks){
      if(isNaN(Number(task.minNumber)) || isNaN(Number(task.maxNumber)) || Number(task.minNumber) < 0 || Number(task.minNumber) > Number(task.maxNumber)){
        this.appService.openSnackBar("Min number have to be greater or equal 0 and max number has to be equal or greater min number!");
        return;
      }
    }
    this.languageService.editTasks(this.language.id, this.language.tasks)
      .subscribe(
        data => {
          this.router.navigate(['/languages/'+this.language.id+'/show']);
        },
        error => {
          this.router.navigate(['/']);
        });


  }
}
