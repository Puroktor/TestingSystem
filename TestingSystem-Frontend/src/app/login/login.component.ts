import {Component, OnInit} from '@angular/core';
import {UserAuthService} from "../service/user-auth.service";
import Swal from "sweetalert2";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  hasSent: boolean = false;

  constructor(private userService: UserAuthService, private router: Router) {
  }

  ngOnInit(): void {
    if (localStorage.getItem('access-jwt') != null) {
      this.router.navigate(['/']);
    }
  }

  submit() {
    let name = (document.getElementById('name') as HTMLInputElement).value.trim();
    let password = (document.getElementById('password') as HTMLInputElement).value.trim();
    if (name.length == 0 || name.length > 50) {
      Swal.fire('Your nickname must be between 1 and 50 characters');
    } else if (password.length == 0 || password.length > 256) {
      Swal.fire('Your password must be between 1 and 256 characters');
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
