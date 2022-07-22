import {Component, OnInit} from '@angular/core';
import {UserService} from "../service/user.service";

@Component({
  selector: 'app-info',
  templateUrl: './info.component.html',
  styleUrls: ['./info.component.css']
})
export class InfoComponent implements OnInit {

  isNotLoggedIn: boolean = this.userService.username.getValue() == null;

  constructor(private userService: UserService) {
  }

  ngOnInit(): void {
  }

}
