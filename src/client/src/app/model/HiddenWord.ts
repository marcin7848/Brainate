import {Account} from "./Account";

export class HiddenWord {
  id: number;
  id_language: number;
  id_category: number;
  id_wordConfig: number;
  account: Account;

  constructor(id_language?: number, id_category?: number, id_wordConfig?: number, account?: Account) {
    this.id_language = id_language;
    this.id_category = id_category;
    this.id_wordConfig = id_wordConfig;
    this.account = account;
  }

}
