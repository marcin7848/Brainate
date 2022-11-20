import {SpecialLetter} from "./SpecialLetter";

export class DictionaryLanguage {
  id: number;
  name: string;
  languageShortcut: string;
  codeForTranslator: string;
  codeForSpeech: string;
  specialLetters: SpecialLetter[];

  constructor(name?: string, languageShortcut?: string, codeForTranslator?: string, codeForSpeech?: string,
              specialLetters?: SpecialLetter[]) {
    this.name = name;
    this.languageShortcut = languageShortcut;
    this.codeForTranslator = codeForTranslator;
    this.codeForSpeech = codeForSpeech;
    this.specialLetters = specialLetters;
  }

}
