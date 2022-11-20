import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Account} from "./model/Account";
import {CookieService} from "ngx-cookie-service";
import {Router} from "@angular/router";
import {Observable} from "rxjs";
import {Language} from "./model/Language";
import {MatSnackBar} from "@angular/material";
import {SnackBarComponent} from "./snack-bar/snack-bar.component";

@Injectable({
  providedIn: 'root'
})
export class AppService {

  mainHttp: string = "/api/";
  treeCounter: boolean = false;

  constructor(private http: HttpClient, private cookieService: CookieService,
              private router: Router,
              private snackBar: MatSnackBar) {
  }

  check() {
    this.http.get<Account>(this.mainHttp + 'accounts/getLoggedAccount').subscribe(data => {
      },
      error => {
        this.cookieService.delete("Authorization", "/");
        this.router.navigate(['/accounts/login']);
      });
  }

  getAllLanguages(): Observable<Language[]> {
    return this.http.get<Language[]>(this.mainHttp + 'languages/getAll');
  }

  openSnackBar(text: string) {
    this.snackBar.openFromComponent(SnackBarComponent, {
      duration: 3 * 1000,
      panelClass: ['snackbar'],
      data: text
    });
  }

  getTreeCounter(): boolean {
    return this.treeCounter = !this.treeCounter;
  }
}
