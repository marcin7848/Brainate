import {Language} from "./Language";
import {TaskMode} from "./TaskMode";

export class Task {
  id: number;
  active: boolean;
  toDo: number;
  done: number;
  minNumber: number;
  maxNumber: number;
  taskMode: TaskMode;
  taskDate: Date;
  language: Language;

  constructor(active?: boolean, toDo?: number, done?: number, minNumber?: number, maxNumber?: number, taskMode?: TaskMode, taskDate?: Date, language?: Language) {
    this.active = active;
    this.toDo = toDo;
    this.done = done;
    this.minNumber = minNumber;
    this.maxNumber = maxNumber;
    this.taskMode = taskMode;
    this.taskDate = taskDate;
    this.language = language;
  }

}
