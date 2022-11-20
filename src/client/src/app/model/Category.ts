import {Language} from "./Language";
import {Mode} from "./Mode";
import {WordConfig} from "./WordConfig";

export class Category {
  id: number;
  name: string;
  mode: Mode;
  idParent: number;
  defaultCategory: boolean;
  language: Language;
  wordConfigs: WordConfig[];
  languageJson: Language;

  constructor(name?: string, mode?: Mode, idParent?: number, defaultCategory?: boolean, language?: Language,
              wordConfigs?: WordConfig[], languageJson?: Language) {
    this.name = name;
    this.mode = mode;
    this.idParent = idParent;
    this.defaultCategory = defaultCategory;
    this.language = language;
    this.wordConfigs = wordConfigs;
    this.languageJson = languageJson;
  }


}
