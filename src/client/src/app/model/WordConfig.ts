import {Mechanism} from "./Mechanism";
import {Category} from "./Category";
import {Word} from "./Word";
import {Mode} from "./Mode";

export class WordConfig {
  id: number;
  done: number;
  today: boolean;
  repeated: number;
  thisUnit: boolean;
  dateTimeDone: Date;
  processNo: number;
  lastPerfectNum: number;
  lastPerfect: boolean;
  comment: string;
  mechanism: Mechanism;
  accepted: boolean;
  categories: Category[];
  words: Word[];
  categoryId: number;
  categoryMode: Mode;

  constructor(done?: number, today?: boolean, repeated?: number, thisUnit?: boolean, dateTimeDone?: Date,
              processNo?: number, lastPerfectNum?: number, lastPerfect?: boolean, comment?: string,
              mechanism?: Mechanism, accepted?: boolean, categories?: Category[], words?: Word[], categoryId?: number, categoryMode?: Mode) {
    this.done = done;
    this.today = today;
    this.repeated = repeated;
    this.thisUnit = thisUnit;
    this.dateTimeDone = dateTimeDone;
    this.processNo = processNo;
    this.lastPerfectNum = lastPerfectNum;
    this.lastPerfect = lastPerfect;
    this.comment = comment;
    this.mechanism = mechanism;
    this.accepted = accepted;
    this.categories = categories;
    this.words = words;
    this.categoryId = categoryId;
    this.categoryMode = categoryMode;
  }
}
