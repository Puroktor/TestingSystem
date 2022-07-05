import {Component, OnInit} from '@angular/core';

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

  getLocalStorageId() {
    return localStorage.getItem("id");
  }

  getLocalStorageNickname() {
    return localStorage.getItem("nickname");
  }
  logout() {
    localStorage.removeItem("id");
    localStorage.removeItem("nickname");
  }

}
