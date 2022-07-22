import {Component, OnDestroy, OnInit} from '@angular/core';
import {UserService} from "../service/user.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

  username: string | null = null;

  constructor(private userService: UserService, private router: Router) {
  }

  ngOnInit(): void {
    this.userService.username.subscribe({next: value => this.username = value});
  }

  ngOnDestroy(): void {
    this.userService.username.unsubscribe();
  }

  logout() {
    this.userService.logout();
    this.router.navigate(['/login']);
  }
}
