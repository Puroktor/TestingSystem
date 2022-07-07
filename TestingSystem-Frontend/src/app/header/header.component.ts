import {Component, OnInit} from '@angular/core';
import jwt_decode from "jwt-decode";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor() {
  }

  ngOnInit(): void {
  }

  checkJwt() {
    return localStorage.getItem("jwt");
  }

  getLocalStorageNickname() {
    let token = localStorage.getItem("jwt");
    if (token == null) {
      return null;
    }
    let decoded: any = jwt_decode(token);
    if (decoded.exp * 1000 < Date.now()) {
      localStorage.removeItem("jwt");
    }
    return decoded.sub;
  }

  logout() {
    localStorage.removeItem("jwt");
  }

}
