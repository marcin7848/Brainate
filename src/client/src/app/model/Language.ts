import {DictionaryLanguage} from "./DictionaryLanguage";
import {Account} from "./Account";
import {CategorySetting} from "./CategorySetting";
import {Category} from "./Category";
import {Statistic} from "./Statistic";
import {Task} from "./Task";

export class Language {
  id: number;
  hidden: boolean;
  started: boolean;
  account: Account;
  dictionaryLanguage: DictionaryLanguage;
  categorySettings: CategorySetting[];
  categories: Category[];
  statistics: Statistic[];
  tasks: Task[];
  accountJson: Account;

  constructor(hidden?: boolean, started?: boolean, account?: Account, dictionaryLanguage?: DictionaryLanguage,
              categorySettings?: CategorySetting[], categories?: Category[], statistics?: Statistic[], tasks?:Task[], accountJson?: Account) {
    this.hidden = hidden;
    this.started = started;
    this.account = account;
    this.dictionaryLanguage = dictionaryLanguage;
    this.categorySettings = categorySettings;
    this.categories = categories;
    this.statistics = statistics;
    this.tasks = tasks;
    this.accountJson = accountJson;
  }

}
