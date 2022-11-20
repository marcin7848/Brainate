import {Language} from "./Language";

export class Account {
  id: number;
  username: string;
  password: string;
  email: string;
  points: number;
  tasksOldestDoneDate: Date;
  languages: Language[];

  constructor(username?: string, email?: string, password?: string, points?: number, tasksOldestDoneDate?: Date, languages?: Language[]) {
    this.username = username;
    this.password = password;
    this.email = email;
    this.points = points;
    this.tasksOldestDoneDate = tasksOldestDoneDate;
    this.languages = languages;
  }

}
