import {Component, OnInit} from '@angular/core';
import {LanguageService} from "../language.service";
import {AppService} from "../../app.service";
import {ActivatedRoute, Router} from "@angular/router";
import {AccountService} from "../../account-management/account.service";
import {Account} from "../../model/Account";
import {TaskMode} from "../../model/TaskMode";
import {Mode} from "../../model/Mode";

@Component({
  selector: 'app-tasks-view',
  templateUrl: './tasks-view.component.html',
  styleUrls: ['./tasks-view.component.scss']
})
export class TasksViewComponent implements OnInit {

  account: Account;
  level: Level;
  Math = Math;
  TaskMode = TaskMode;
  pointsToGet: number = 0;
  Mode = Mode;
  accounts: Account[];

  constructor(private languageService: LanguageService,
              private appService: AppService,
              private route: ActivatedRoute,
              private router: Router,
              private accountService: AccountService) {
  }

  ngOnInit() {
    this.appService.check();
    this.accountService.getLoggedAccount()
      .subscribe(
        data => {
          this.account = data;

          this.account.languages.sort((a, b) => a.id - b.id);
          this.account.languages.forEach(l => {
            l.tasks.sort((a, b) => a.id - b.id);
            l.statistics.sort((a, b) => a.id - b.id);
            l.tasks = l.tasks.filter(t => t.active);
          });

          this.pointsToGet = this.getPointsToGet(this.account).pointsToGet;

          this.level = this.calcLevel(this.account.points, 1);
        },
        error => {
        });

    this.accountService.getAllAccountsStatistics()
      .subscribe(
        data => {
          this.accounts = data;
          this.accounts.sort((a, b) => a.id - b.id);
          this.accounts.forEach(a => a.languages.sort((a, b) => a.id - b.id));
          this.accounts.forEach(a => a.languages.forEach(l => {
            l.tasks.sort((a, b) => a.id - b.id);
            l.statistics.sort((a, b) => a.id - b.id);
            l.tasks = l.tasks.filter(t => t.active);
          }));

        },
        error => {
        });
  }

  calcLevel(points: number, level: number):Level {
    let levelX100 = level * 100;
    let calcPoints = points - levelX100;
    if (calcPoints >= 0) {
      return this.calcLevel(calcPoints, level + 1);
    } else {
      let pointsResult = calcPoints + levelX100;
      let pointsToNextLevelResult = levelX100;
      return new Level(level, pointsResult, pointsToNextLevelResult);
    }
  }

  getPointsToGet(account: Account):TodayTasks{
    let done = true;
    let pointsToGet = 0;
    let numberOfTasks = 0;
    for (const l of account.languages) {
      for (const t of l.tasks) {
        numberOfTasks++;
        if(t.done < t.toDo){
          done = false;
        }
        if (t.taskMode == TaskMode.DICDO || t.taskMode == TaskMode.EXEDO) {
          pointsToGet += t.done;
        } else if (t.taskMode == TaskMode.DICADD) {
          pointsToGet += 2 * t.done;
        } else {
          pointsToGet += 3 * t.done;
        }
      }
    }

    return new TodayTasks(numberOfTasks > 0 ? done : false, numberOfTasks > 0 && done ? pointsToGet + 50 : pointsToGet);
  }



}

export class Level{
  level: number;
  points: number;
  pointsToNextLevel: number;

  constructor(level?:number, points?:number, pointsToNextLevel?:number){
    this.level = level;
    this.points = points;
    this.pointsToNextLevel = pointsToNextLevel;
  }
}

export class TodayTasks{
  done: boolean;
  pointsToGet: number;

  constructor(done?:boolean, pointsToGet?:number){
    this.done = done;
    this.pointsToGet = pointsToGet;
  }
}
