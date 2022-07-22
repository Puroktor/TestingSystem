import {Component, OnInit} from '@angular/core';
import jwt_decode from "jwt-decode";
import {UserService} from "../service/user.service";
import Swal from "sweetalert2";
import {HttpErrorResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  timer: ReturnType<typeof setTimeout> | null = null;

  constructor(private userService: UserService, private router: Router) {
  }

  ngOnInit(): void {
    let accessToken = localStorage.getItem('access-jwt');
    if (!accessToken) {
      return;
    }
    let decodedAccess: any = jwt_decode(accessToken);
    this.timer = setTimeout(() => this.refreshToken(), Math.max(decodedAccess.exp * 1000 - Date.now(), 0));
  }

  getNickname() {
    let accessToken = localStorage.getItem('access-jwt');
    return accessToken ? (jwt_decode(accessToken) as any).sub: null;
  }

  logout() {
    localStorage.removeItem('access-jwt');
    localStorage.removeItem('refresh-jwt');
    this.router.navigate(['/login']);
  }

  private refreshToken() {
    let refreshToken = localStorage.getItem('refresh-jwt');
    if (refreshToken == null) {
      this.logout();
      return;
    }
    let decodedRefresh: any = jwt_decode(refreshToken);
    if (decodedRefresh.exp * 1000 > Date.now()) {
      this.sendRequest(refreshToken);
    } else {
      this.logout();
    }
  }

  private sendRequest(refreshToken: string) {
    this.userService.refreshToken(refreshToken).subscribe({
      next: (response) => {
        localStorage.setItem('access-jwt', response.accessToken);
        localStorage.setItem('refresh-jwt', response.refreshToken);
        let decodedAccess: any = jwt_decode(response.accessToken);
        this.timer = setTimeout(() => this.refreshToken(), Math.max(decodedAccess.exp * 1000 - Date.now(), 0));
      }, error: (err: HttpErrorResponse) => {
        if (err.status == 0) {
          setTimeout(() => this.sendRequest(refreshToken), environment.retryDelay);
        } else {
          Swal.fire(err.error.message);
          this.logout();
        }
      }
    });
  }
}
