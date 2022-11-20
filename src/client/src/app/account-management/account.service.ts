import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Account} from "../model/Account";

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  mainHttp: string = "/api/";

  constructor(private http: HttpClient) {
  }

  register(account: Account): Observable<Account> {
    return this.http.post<Account>(this.mainHttp + 'accounts/register', account);
  }

  public getLoginToken(username: string, password: string) {
    let headers = new HttpHeaders().set('Authorization', 'Basic ' + btoa("appBrainate-client:appBrainate-secret"));
    headers = headers.set('Content-Type', 'application/x-www-form-urlencoded');

    let body = "username=" + username + "&password=" + password + "&grant_type=password";

    return this.http.post<any>('/oauth/token', body, {headers: headers});
  }

  getLoggedAccount(): Observable<Account> {
    return this.http.get<Account>(this.mainHttp + 'accounts/getLoggedAccount');
  }

  getAllAccountsStatistics(): Observable<Account[]> {
    return this.http.get<Account[]>(this.mainHttp + 'accounts/getAllAccountsStatistics');
  }

}
