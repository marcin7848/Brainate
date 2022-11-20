import {Language} from "./Language";
import {Mode} from "./Mode";
import {Method} from "./Method";

export class CategorySetting {
  id: number;
  mode: Mode;
  thisUnitOn: boolean;
  method: Method;
  language: Language;

  constructor(mode?: Mode, thisUnitOn?: boolean, method?: Method, language?: Language) {
    this.mode = mode;
    this.thisUnitOn = thisUnitOn;
    this.method = method;
    this.language = language;
  }


}
