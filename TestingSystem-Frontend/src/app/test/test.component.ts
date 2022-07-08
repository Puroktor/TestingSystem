import {Component, Input, OnInit} from '@angular/core';
import {FullTest} from "../entity/FullTest";
import {TestService} from "../service/test.service";
import {map} from "rxjs/operators";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";
import {Answer} from "../entity/Answer";
import jwt_decode from 'jwt-decode';
import {UserService} from "../service/user.service";

@Component({
  selector: 'app-test',
  templateUrl: './test.component.html',
  styleUrls: ['./test.component.css']
})
export class TestComponent implements OnInit {

  @Input() test?: FullTest;
  hasSent: boolean = false;
  private userId!: number;

  constructor(private testService: TestService, private userService: UserService, private route: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit(): void {
    let token = localStorage.getItem('access-jwt');
    if (token == null) {
      this.goToHomePage();
      return;
    } else {
      let decoded: any = jwt_decode(token);
      if (!decoded.authorities.includes('USER_SUBMIT')) {
        this.goToHomePage();
        return;
      } else {
        this.userId = decoded.id;
      }
    }
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

  submit() {
    if (this.test != null) {
      this.hasSent = true;
      let answers: Answer[] = [];
      this.test.questions.forEach((question) =>
        question.answers.forEach((answer) => answers.push(answer))
      )
      this.userService.submitAttempt(answers, this.userId, localStorage.getItem('access-jwt') ?? '')
        .subscribe({
          next: () => this.goToHomePage(),
          error: (err: HttpErrorResponse) => Swal.fire(err.error.message).then(() => this.hasSent = false)
        })
    }
  }

  private getTestId() {
    return this.route.queryParamMap.pipe(
      map((params: ParamMap) => params.get('id')),
    );
  }

  private getTestFromServer(value: number) {
    this.testService.getShuffledTest(value, localStorage.getItem('access-jwt') ?? '')
      .subscribe({
        next: value => this.test = value,
        error: (err: HttpErrorResponse) => {
          Swal.fire(err.error.message).then(() => this.goToHomePage())
        }
      })
  }
}
