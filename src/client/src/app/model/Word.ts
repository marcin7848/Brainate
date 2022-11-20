import {WordConfig} from "./WordConfig";

export class Word {
  id: number;
  word: string;
  basicWord: string;
  seat: number;
  toSpeech: boolean;
  answer: boolean;
  wordConfig: WordConfig;
  correct: boolean;

  constructor(word?: string, basicWord?: string, seat?: number, toSpeech?: boolean, answer?: boolean,
              wordConfig?: WordConfig) {
    this.word = word;
    this.basicWord = basicWord;
    this.seat = seat;
    this.toSpeech = toSpeech;
    this.answer = answer;
    this.wordConfig = wordConfig;
  }
}
