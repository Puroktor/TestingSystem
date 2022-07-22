import {Component, OnInit} from '@angular/core';
import {FullTest} from "../entity/FullTest";
import {TestService} from "../service/test.service";
import {map} from "rxjs/operators";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";
import {Answer} from "../entity/Answer";
import jwt_decode from 'jwt-decode';
import {environment} from "../../environments/environment";
import {AttemptService} from "../service/attempt.service";
import {AttemptResult} from "../entity/AttemptResult";

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.css']
})
export class TestComponent implements OnInit {

  test?: FullTest;
  result?: AttemptResult;
  hasSent: boolean = false;
  private userId!: number;

  constructor(private testService: TestService, private attemptService: AttemptService, private route: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit(): void {
    let token = localStorage.getItem('access-jwt');
    if (token == null) {
      Swal.fire('Login to see this page!').then(() => this.router.navigate(['/login']));
      return;
    } else {
      let decoded: any = jwt_decode(token);
      if (!decoded.authorities.includes('USER_SUBMIT')) {
        Swal.fire('You cannot browse this page').then(() => this.goToTestsPage());
        return;
      } else {
        this.userId = decoded.id;
      }
    }
    this.getTestId().subscribe({
      next: value => {
        if (value == null) {
          this.goToTestsPage();
        } else {
          this.getTestFromServer(+value);
        }
      }
    });
  }

  goToTestsPage() {
    this.router.navigate(['/tests-list']);
  }

  submit() {
    if (this.test != null) {
      let answers: Answer[] = [];
      this.test.questions.forEach((question) =>
        question.answers.forEach((answer) => answers.push(answer))
      );
      this.sendAttempt(answers);
    }
  }

  private sendAttempt(answers: Answer[]) {
    this.hasSent = true;
    this.attemptService.submitAttempt(answers, this.userId)
      .subscribe({
        next: (value) => {
          this.hasSent = false;
          this.result = value;
        },
        error: (err: HttpErrorResponse) => {
          if (err.status == 0) {
            setTimeout(() => this.sendAttempt(answers), environment.retryDelay);
          } else {
            Swal.fire(err.error.message).then(() => this.hasSent = false)
          }
        }
      });
  }

  private getTestId() {
    return this.route.queryParamMap.pipe(
      map((params: ParamMap) => params.get('id')),
    );
  }

  private getTestFromServer(value: number) {
    this.testService.getShuffledTest(value)
      .subscribe({
        next: value => this.test = value,
        error: (err: HttpErrorResponse) => {
          if (err.status == 0) {
            setTimeout(() => this.getTestFromServer(value), environment.retryDelay);
          } else {
            Swal.fire(err.error.message).then(() => this.goToTestsPage())
          }
        }
      })
  }
}
