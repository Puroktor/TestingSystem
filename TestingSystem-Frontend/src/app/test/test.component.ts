import {Component, Input, OnInit} from '@angular/core';
import {FullTest} from "../entity/FullTest";
import {TestService} from "../test.service";
import {map} from "rxjs/operators";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";
import {UserService} from "../user.service";
import {Answer} from "../entity/Answer";
import jwt_decode from 'jwt-decode';

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.css']
})
export class TestComponent implements OnInit {

  @Input() test?: FullTest;
  private userId!: number;
  private token!: string;
  buttonDisabled: boolean = false;

  constructor(private testService: TestService, private userService: UserService, private route: ActivatedRoute,
              private router: Router) {
    let token = localStorage.getItem("jwt")
    if (token == null) {
      this.goToLoginPage();
    } else {
      let decoded: any = jwt_decode(token);
      if (!decoded.authorities.includes("USER_SUBMIT")) {
        this.goToHomePage();
      } else {
        this.token = token;
        this.userId = decoded.id;
      }
    }
  }

  ngOnInit(): void {
    this.getTestId().subscribe({
      next: value => {
        if (value == null) {
          this.goToHomePage();
        } else {
          this.getTestFromServer(+value);
        }
      }
    });
  }

  private goToLoginPage() {
    this.router.navigate(['/login']);
  }

  goToHomePage() {
    this.router.navigate(['/']);
  }

  private getTestId() {
    return this.route.queryParamMap.pipe(
      map((params: ParamMap) => params.get('id')),
    );
  }

  private getTestFromServer(value: number) {
    this.testService.getShuffledTest(value, this.token)
      .subscribe({
        next: value => this.test = value,
        error: (err: HttpErrorResponse) => {
          Swal.fire(err.error.message).then(() => this.goToLoginPage())
        }
      })
  }

  submit() {
    if (this.test != null) {
      this.buttonDisabled = true;
      let answers: Answer[] = [];
      this.test.questionList.forEach((question) =>
        question.answers.forEach((answer) => answers.push(answer))
      )
      this.userService.submitAttempt(answers, this.userId, this.token)
        .subscribe({
          next: () => this.goToHomePage(),
          error: (err: HttpErrorResponse) => Swal.fire(err.error.message).then(() => this.goToLoginPage())
        })
    }
  }
}
