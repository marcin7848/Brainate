import {Injectable} from "@angular/core";
import {tap} from "rxjs/operators";
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from "@angular/common/http";
import {Observable} from "rxjs";
import {CookieService} from 'ngx-cookie-service';
import {ActivatedRoute, Router} from "@angular/router";

@Injectable()
export class Interceptor implements HttpInterceptor {
  constructor(private cookieService: CookieService,
              private router: Router) {
  }

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {

    if (request.url != "/api/accounts/register" && request.url != "/oauth/token") {

      request = request.clone({
        headers: request.headers.append('Content-Type', 'application/json')
      });

      request = request.clone({
        headers: request.headers.append('Authorization', 'Bearer ' + this.cookieService.get("Authorization"))
      });
    }

    return next.handle(request).pipe(
      tap(
        event => {
        },
        error => {
          if (request.url != "/oauth/token") {
            if (error['status'] == 401 && error['error']['error'] != "invalid_grant") {
              this.cookieService.delete("Authorization", "/");
              this.router.navigate(['/accounts/login']);
            }
          }
        }
      )
    );
  }
}
