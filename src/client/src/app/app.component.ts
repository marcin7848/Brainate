import {Component, OnInit} from '@angular/core';
import * as $ from 'jquery';
import {AccountService} from "./account-management/account.service";
import {Account} from "./model/Account";
import {CookieService} from "ngx-cookie-service";
import {AppService} from "./app.service";
import {Language} from "./model/Language";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  loggedAccount: Account;
  languages: Language[];

  constructor(private accountService: AccountService,
              private cookieService: CookieService,
              private appService: AppService) {
    $(document).on('keypress', function (e) {
      if (e.which == 13) {
        $(".clickButton")[0].click();
      }
    });

    if (this.cookieService.get("Authorization")) {
      this.accountService.getLoggedAccount().subscribe(data => {
          this.loggedAccount = data;
        },
        error => {
        });
    }

  }

  ngOnInit() {
    if (this.cookieService.get("Authorization")) {
      this.appService.getAllLanguages()
        .subscribe(
          data => {
            this.languages = data;
          },
          error => {
          });


    }
  }
}
