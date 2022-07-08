import {Component, OnInit} from '@angular/core';
import jwt_decode from "jwt-decode";
import {UserService} from "../service/user.service";
import Swal from "sweetalert2";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  nickname: string | null = null;
  timer: ReturnType<typeof setTimeout> | null = null;

  constructor(private userService: UserService) {
  }

  ngOnInit(): void {
    let accessToken = localStorage.getItem('access-jwt');
    if (accessToken == null) {
      return;
    }
    let decodedAccess: any = jwt_decode(accessToken);
    this.nickname = decodedAccess.sub;
    this.timer = setTimeout(() => this.refreshToken(), Math.max(decodedAccess.exp * 1000 - Date.now(), 0));
  }

  logout() {
    localStorage.removeItem('access-jwt');
    localStorage.removeItem('refresh-jwt');
    window.location.reload();
  }

  private refreshToken() {
    let refreshToken = localStorage.getItem('refresh-jwt');
    if (refreshToken == null) {
      this.logout();
      return;
    }
    let decodedRefresh: any = jwt_decode(refreshToken);
    if (decodedRefresh.exp * 1000 > Date.now()) {
      this.userService.refreshToken(refreshToken).subscribe({
        next: (response) => {
          localStorage.setItem('access-jwt', response.accessToken);
          localStorage.setItem('refresh-jwt', response.refreshToken);
          let decodedAccess: any = jwt_decode(response.accessToken);
          this.timer = setTimeout(() => this.refreshToken(), Math.max(decodedAccess.exp * 1000 - Date.now(), 0));
        }, error: (err: HttpErrorResponse) => {
          Swal.fire(err.error.message);
          this.logout();
        }
      });
    } else {
      this.logout();
    }
  }
}
