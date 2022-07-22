import {Component, OnInit} from '@angular/core';
import {FullTest} from "../entity/FullTest";
import {TestService} from "../service/test.service";
import {map} from "rxjs/operators";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";
import {Answer} from "../entity/Answer";
import {environment} from "../../environments/environment";
import {AttemptService} from "../service/attempt.service";
import {AttemptResult} from "../entity/AttemptResult";
import {UserService} from "../service/user.service";

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.css']
})
export class TestComponent implements OnInit {

  test?: FullTest;
  result?: AttemptResult;
  hasSent: boolean = false;

  constructor(private testService: TestService, private attemptService: AttemptService, private userService: UserService,
              private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    if (this.userService.username.getValue() == null) {
      Swal.fire('Login to see this page!').then(() => this.router.navigate(['/login']));
      return;
    }
    if (!this.userService.authorities.getValue().includes('USER_SUBMIT')) {
      Swal.fire('You cannot browse this page').then(() => this.goToTestsPage());
      return;
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
    this.attemptService.submitAttempt(answers)
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
