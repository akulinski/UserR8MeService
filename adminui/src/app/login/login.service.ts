import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {LoginDto} from "./login.dto";

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private REST_API_SERVER = "http://localhost:8080/api";

  constructor(private httpClient: HttpClient) {
  }

  public login(loginDto: LoginDto) {
    return this.httpClient.post(this.REST_API_SERVER+"/auth", loginDto);
  }

}
