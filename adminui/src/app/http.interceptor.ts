import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse} from '@angular/common/http';
import {Observable} from "rxjs";
import {tap} from "rxjs/operators";
import {Router} from "@angular/router";

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  constructor(private router: Router) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (localStorage.getItem("jwttoken")) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${localStorage.getItem("jwttoken")}`
        }
      });
    }
    return next.handle(request).pipe(tap(evt => {
      if (evt instanceof HttpResponse) {
        if (evt.status == 401) {
          this.router.navigateByUrl("/login");
        }
      }
    }));
  }
}
