import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {LoginService} from "./login.service";
import {LoginDto} from "./login.dto";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  username: string;
  password: string;
  showSpinner: any;

  constructor(private router: Router, private loginService: LoginService) {
  }

  ngOnInit() {
    let token = localStorage.getItem("jwttoken");
    if (token) {
      this.router.navigateByUrl("/user");
    }
  }

  login(): void {
    const loginDto = new LoginDto(this.username, this.password);
    this.loginService.login(loginDto).subscribe(data => {
      localStorage.setItem("jwttoken", data['jwttoken']);
      this.router.navigateByUrl("/user");
    });
  }

}
