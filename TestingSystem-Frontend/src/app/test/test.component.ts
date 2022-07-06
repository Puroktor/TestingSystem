import {Component, Input, OnInit} from '@angular/core';
import {FullTest} from "../entity/FullTest";
import {TestService} from "../test.service";
import {map} from "rxjs/operators";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";
import {UserService} from "../user.service";
import {Answer} from "../entity/Answer";

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.css']
})
export class TestComponent implements OnInit {

  @Input() test?: FullTest;
  userId!: number;
  buttonDisabled: boolean = false;

  constructor(private testService: TestService, private userService: UserService, private route: ActivatedRoute,
              private router: Router) {
    let userId = localStorage.getItem("id");
    if (userId == null) {
      this.goToHomePage();
    } else {
      this.userId = +userId;
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

  goToHomePage() {
    this.router.navigate(['/']);
  }

  getTestId() {
    return this.route.queryParamMap.pipe(
      map((params: ParamMap) => params.get('id')),
    );
  }

  getTestFromServer(value: number) {
    this.testService.getTest(value)
      .subscribe({
        next: value => this.test = value,
        error: (err: HttpErrorResponse) => Swal.fire(err.error.message).then(() => this.goToHomePage())
      })
  }

  submit() {
    if (this.test != null) {
      this.buttonDisabled = true;
      let answers: Answer[] = [];
      this.test.questionList.forEach((question) =>
        question.answers.forEach((answer) => answers.push(answer))
      )
      this.userService.submitAttempt(answers, this.userId)
        .subscribe({
          next: () => this.goToHomePage(),
          error: (err: HttpErrorResponse) => Swal.fire(err.error.message).then(() => this.buttonDisabled = false)
        })
    }
  }

}
