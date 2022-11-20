import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Language} from "../model/Language";
import {CategorySetting} from "../model/CategorySetting";
import {Category} from "../model/Category";
import {WordConfig} from "../model/WordConfig";
import {LemmaWord} from "../model/LemmaWord";
import {HiddenWord} from "../model/HiddenWord";
import {Task} from "../model/Task";

@Injectable({
  providedIn: 'root'
})
export class LanguageService {

  mainHttp: string = "/api/";

  constructor(private http: HttpClient) {
  }

  addLanguage(language: Language): Observable<Language> {
    return this.http.post<Language>(this.mainHttp + 'languages/add', language);
  }

  getLanguageById(id: number): Observable<Language> {
    return this.http.get<Language>(this.mainHttp + 'languages/' + id + '/get');
  }

  updateLanguage(id: number, language: Language): Observable<Language> {
    return this.http.patch<Language>(this.mainHttp + 'languages/' + id + '/edit', language);
  }

  deleteLanguage(id: number) {
    return this.http.delete(this.mainHttp + 'languages/' + id + '/delete');
  }

  editCategorySettings(idLanguage: number, idCategorySetting: number,
                       categorySetting: CategorySetting): Observable<CategorySetting> {
    return this.http.patch<CategorySetting>(this.mainHttp + 'languages/' + idLanguage + '/categorySettings/' +
      idCategorySetting + '/edit', categorySetting);
  }

  addNewCategory(idLanguage: number, category: Category): Observable<Category> {
    return this.http.post<Category>(this.mainHttp + 'languages/' + idLanguage + '/categories/add', category);
  }

  getLanguageWithCategoryAndWords(idLanguage: number, idCategory: number): Observable<Language> {
    return this.http.get<Language>(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/get');
  }

  editCategory(idLanguage: number, idCategory: number, category: Category): Observable<Category> {
    return this.http.patch<Category>(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/edit', category);
  }

  deleteCategory(idLanguage: number, idCategory: number) {
    return this.http.delete(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/delete');
  }

  addNewWord(idLanguage: number, idCategory: number, wordConfig: WordConfig): Observable<WordConfig> {
    return this.http.post<WordConfig>(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/words/add', wordConfig);
  }

  changeThisUnit(idLanguage: number, idCategory: number, idWordConfig: number): Observable<WordConfig> {
    return this.http.patch<WordConfig>(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/words/' + idWordConfig + '/changeThisUnit', {});
  }

  deleteWord(idLanguage: number, idCategory: number, idWordConfig: number, justForThisCategory: boolean){
    return this.http.delete(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/words/' + idWordConfig + '/delete/'+justForThisCategory);
  }

  editWord(idLanguage: number, idCategory: number, idWordConfig: number, wordConfig: WordConfig): Observable<WordConfig> {
    return this.http.patch<WordConfig>(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/words/' + idWordConfig + '/edit', wordConfig);
  }

  acceptWord(idLanguage: number, idCategory: number, idWordConfig: number): Observable<WordConfig> {
    return this.http.patch<WordConfig>(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/words/' + idWordConfig + '/accept', {});
  }

  automaticAddingWords(idLanguage: number, idCategory: number, text: string): Observable<WordConfig> {
    return this.http.post<WordConfig>(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/automatic/lemma', new LemmaWord(text, ""));
  }

  getAllLanguagesAutomatic(idLanguage: number, idCategory: number): Observable<Language[]> {
    return this.http.get<Language[]>(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/automatic/getAllLanguages');
  }

  hideWord(idLanguage: number, idCategory: number, hiddenWord: HiddenWord) {
    return this.http.post(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/automatic/hide', hiddenWord);
  }

  copyWords(idLanguage: number, idCategory: number, hiddenWords: HiddenWord[]) {
    return this.http.post(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/automatic/add', hiddenWords);
  }

  resetThisUnit(idLanguage: number, categoriesWithJustId: Category[]) {
    return this.http.patch(this.mainHttp + 'languages/' + idLanguage + '/resetThisUnit', categoriesWithJustId);
  }

  resetWords(idLanguage: number, categoriesWithJustId: Category[]) {
    return this.http.patch(this.mainHttp + 'languages/' + idLanguage + '/resetWords', categoriesWithJustId);
  }

  startRepeating(idLanguage: number, categoriesWithJustId: Category[], numberOfWords: number) {
    return this.http.post(this.mainHttp + 'languages/' + idLanguage + '/startRepeating/'+numberOfWords, categoriesWithJustId);
  }

  getLanguageByIdWithTodayWords(id: number): Observable<Language> {
    return this.http.get<Language>(this.mainHttp + 'languages/' + id + '/getTodayWords');
  }

  resetRepeating(idLanguage: number) {
    return this.http.patch(this.mainHttp + 'languages/' + idLanguage + '/resetRepeating', {});
  }

  changeDone(idLanguage: number, idCategory: number, wordConfig: WordConfig) {
    return this.http.patch(this.mainHttp + 'languages/' + idLanguage + '/categories/'+idCategory+'/changeDone', wordConfig);
  }

  editTasks(idLanguage: number, tasks: Task[]) {
    return this.http.patch(this.mainHttp + 'languages/' + idLanguage + '/editTasks', tasks);
  }

  searchWord(idLanguage: number, idCategory: number, text: string): Observable<WordConfig[]> {
    return this.http.post<WordConfig[]>(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/searchWord', {text: text});
  }

  getLanguageAndCategory(idLanguage: number, idCategory: number): Observable<Language> {
    return this.http.get<Language>(this.mainHttp + 'languages/' + idLanguage + '/categories/' + idCategory + '/automatic/getLanguageAndCategory');
  }

}
