import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../account.service";
import {CookieService} from "ngx-cookie-service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  submitted: Boolean;
  errorMessage: string;

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService,
              private cookieService: CookieService,
              private router: Router) {
    this.loginForm = this.formBuilder.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.submitted = false;
    this.errorMessage = "";
    if (this.cookieService.get("Authorization")) {
      this.router.navigate(['/']);
    }
  }

  get f() {
    return this.loginForm.controls;
  }

  login() {
    this.submitted = true;
    if (this.loginForm.invalid) {
      this.submitted = false;
      this.errorMessage = "Invalid form!";
      return;
    }

    this.accountService.getLoginToken(this.f.username.value, this.f.password.value)
      .subscribe(
        data => {
          this.cookieService.set("Authorization", data['access_token'], 365, '/');
          setTimeout(function(){location.href = '/';}, 100);
        },
        error => {
          this.errorMessage = error["error"]["error_description"];
          this.submitted = false;
        });
  }

}
