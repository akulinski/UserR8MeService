import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import UserDto = namespace.UserDto;
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private REST_API_SERVER = "http://localhost:8080/api";

  constructor(private http: HttpClient) {
  }

  public loadData(): Observable<UserDto[]>{
    return this.http.get<UserDto[]>(this.REST_API_SERVER+"/v1/user/all");
  }
}
