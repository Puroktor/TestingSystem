import {Component, OnInit} from '@angular/core';
import {UserService} from '../service/user.service';
import {Router} from '@angular/router';
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";

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
    let year =null;
    let groupNumber =null;
    let violations = '';
    if (name.length == 0 || name.length > 100) {
      violations = violations = violations.concat('Full name must be 1-100 chars\n');
    }
    if (nickname.length == 0 || nickname.length > 50) {
      violations = violations.concat('Nickname must be 1-50 chars\n');
    }
    if (email.length == 0 || email.length > 320) {
      violations = violations.concat('Email must be 1-320 chars\n');
    }
    if (!email.match(/^(?=.{1,64}@)[A-Za-z0-9_-]+(\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[A-Za-z]{2,})$/)) {
      violations = violations.concat('Not valid email!\n');
    }
    if (password.length == 0 || password.length > 256) {
      violations = violations.concat('Password must be 1-256 chars\n');
    }
    if (university.length == 0 || university.length > 100) {
      violations = violations.concat('University must be 1-100 chars\n');
    }
    if (this.isVisible) {
      year = +((document.getElementById('year') as HTMLSelectElement).value.trim());
      groupNumber = +((document.getElementById('groupNumber') as HTMLInputElement).value.trim());
      if (isNaN(year)) {
        violations = violations.concat('Enter student year in numeric format\n');
      }
      if (year < 1 || year > 6) {
        violations = violations.concat('Student year must be 1 - 6\n');
      }
      if (isNaN(groupNumber)) {
        violations = violations.concat('Enter group number in numeric format\n');
      }
      if (groupNumber < 1) {
        violations = violations.concat('Group number must be >=1\n');
      }
    }
    if (violations.length != 0) {
      Swal.fire(violations);
    } else {
      this.sendRequest(name, nickname, email, password, this.isVisible ? 'STUDENT' : 'TEACHER', university,
        year, groupNumber);
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
