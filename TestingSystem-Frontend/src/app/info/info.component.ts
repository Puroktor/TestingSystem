import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css']
})
export class InfoComponent implements OnInit {

  isLoggedIn: boolean = false;

  constructor() {
  }

  ngOnInit(): void {
    this.isLoggedIn = localStorage.getItem('access-jwt') == null;
  }

}
