import {Component, OnInit} from '@angular/core';
import {UserService} from '../user.service';
import {Router} from '@angular/router';
import Swal from 'sweetalert2';
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  hasSent: boolean = false;
  isVisible: boolean = true;

  constructor(private userService: UserService, private router: Router) {
  }

  ngOnInit(): void {
  }

  changeRole(event: any) {
    this.isVisible = (event.target as HTMLInputElement).value == '1';
  }

  submit() {
    let name = (document.getElementById('name') as HTMLInputElement).value.trim();
    let nickname = (document.getElementById('nickname') as HTMLInputElement).value.trim();
    let email = (document.getElementById('email') as HTMLInputElement).value.trim();
    let password = (document.getElementById('password') as HTMLInputElement).value.trim();
    let university = (document.getElementById('university') as HTMLInputElement).value.trim();
    if (name.length == 0 || name.length > 100) {
      Swal.fire('Your full name must be between 1 and 100 characters');
    } else if (nickname.length == 0 || nickname.length > 50) {
      Swal.fire('Your nickname must be between 1 and 50 characters');
    } else if (email.length == 0 || email.length > 320) {
      Swal.fire('Email must be between 1 and 320 characters');
    } else if (!email.match(/^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$/)) {
      Swal.fire('Not valid email!');
    } else if (password.length == 0 || password.length > 256) {
      Swal.fire('Your password must be between 1 and 256 characters');
    } else if (university.length == 0 || university.length > 100) {
      Swal.fire('University name must be between 1 and 100 characters');
    } else {
      if (this.isVisible) {
        let year = +((document.getElementById('year') as HTMLSelectElement).value.trim());
        console.log(year);
        let groupNumber = +((document.getElementById('groupNumber') as HTMLInputElement).value.trim());
        if (isNaN(year)) {
          Swal.fire('Enter student year in numeric format');
        } else if (year < 1 || year > 6) {
          Swal.fire('Student year must be between 1 and 6');
        } else if (isNaN(groupNumber)) {
          Swal.fire('Enter group number in numeric format');
        } else if (groupNumber < 1) {
          Swal.fire('Group number must be >=1');
        } else {
          this.sendRequest(name, nickname, email, password, "STUDENT", university, year, groupNumber);
        }
      } else {
        this.sendRequest(name, nickname, email, password, "TEACHER", university, null, null);
      }
    }
  }

  private sendRequest(name: string, nickname: string, email: string, password: string, role: string, university: string,
                      year: number | null, groupNumber: number | null) {
    this.hasSent = true;
    this.userService.createUser({
      name: name, nickname: nickname, email: email, password: password, role: role,
      university: university, year: year, groupNumber: groupNumber, id: null
    }).subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (err: HttpErrorResponse) => Swal.fire(err.error.message).then(() => this.hasSent = false)
    });
  }

}
