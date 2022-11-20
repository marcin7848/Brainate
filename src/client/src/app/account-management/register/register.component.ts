import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AccountService} from "../account.service";
import {Account} from "../../model/Account";
import {Router} from "@angular/router";
import {CookieService} from "ngx-cookie-service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  registerForm: FormGroup;
  submitted: Boolean;
  errorMessage: string;
  registerOn = false;

  constructor(private formBuilder: FormBuilder,
              private accountService: AccountService,
              private cookieService: CookieService,
              private router: Router) {

    this.registerForm = this.formBuilder.group({
      username: ['', Validators.required],
      email: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    });

  }

  ngOnInit() {
    this.submitted = false;
    this.errorMessage = "";
    if(this.cookieService.get("Authorization")){
      this.router.navigate(['/']);
    }
  }

  get f() {
    return this.registerForm.controls;
  }

  register() {
    this.submitted = true;
    if (this.registerForm.invalid) {
      this.submitted = false;
      this.errorMessage = "Invalid form!";
      return;
    }

    let account: Account = new Account(this.f.username.value, this.f.email.value, this.f.password.value, 0);

    this.accountService.register(account).subscribe(
      result => {
        this.submitted = false;
        this.errorMessage = "Now you can log in!";
        this.router.navigate(['/accounts/login']);
      },
      error => {
        this.submitted = false;
        this.errorMessage = error["error"]["error"];
      }
    );

  }

}
