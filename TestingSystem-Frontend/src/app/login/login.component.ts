import {Component, OnInit} from '@angular/core';
import {UserService} from "../service/user.service";
import Swal from "sweetalert2";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  hasSent: boolean = false;

  constructor(private userService: UserService, private router: Router) {
  }

  ngOnInit(): void {
    if (localStorage.getItem('access-jwt') != null) {
      this.router.navigate(['/']);
    }
  }

  submit() {
    let name = (document.getElementById('name') as HTMLInputElement).value.trim();
    let password = (document.getElementById('password') as HTMLInputElement).value.trim();
    let violations = '';
    if (name.length == 0 || name.length > 50) {
      violations = violations.concat('Nickname must be 1-50 chars\n');
    }
    if (password.length == 0 || password.length > 256) {
      violations = violations.concat('Password must be 1-256 chars\n');
    }
    if (violations.length != 0) {
      Swal.fire(violations);
    } else {
      this.hasSent = true;
      this.userService.loginUser({nickname: name, password: password})
        .subscribe({
          next: response => {
            localStorage.setItem('access-jwt', response.accessToken);
            localStorage.setItem('refresh-jwt', response.refreshToken);
            window.location.href = '/';
          },
          error: (err) => Swal.fire(err.error.message).then(() => this.hasSent = false)
        });
    }
  }
}
