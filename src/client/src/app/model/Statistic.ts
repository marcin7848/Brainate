import {Language} from "./Language";
import {Mode} from "./Mode";

export class Statistic {
  id: number;
  mode: Mode;
  added: number;
  done: number;
  language: Language;

  constructor(mode?: Mode, added?: number, done?: number, language?: Language) {
    this.mode = mode;
    this.added = added;
    this.done = done;
    this.language = language;
  }

}
