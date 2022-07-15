import {Component, OnInit} from '@angular/core';
import {map} from "rxjs/operators";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import Swal from "sweetalert2";
import jwt_decode from "jwt-decode";
import {HttpErrorResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {AttemptService} from "../service/attempt.service";
import {Attempt} from "../entity/Attempt";

@Component({
  selector: 'app-attempt',
  templateUrl: './attempt.component.html',
  styleUrls: ['./attempt.component.css']
})
export class AttemptComponent implements OnInit {

  userId: number = 0;
  testId: number = 0;
  attempt?: Attempt;

  constructor(private attemptService: AttemptService, private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    let token = localStorage.getItem('access-jwt')
    if (token == null) {
      Swal.fire('Login to see this page!').then(() => this.router.navigate(['/login']));
      return;
    }
    let decoded: any = jwt_decode(token);
    if (!decoded.authorities.includes('USER_EDIT')) {
      Swal.fire('You cannot browse this page!').then(() => this.goToLeaderboard());
      return;
    }
    this.getQueryParams()
      .subscribe({
        next: value => {
          if (value[0] == null || value[1] == null) {
            Swal.fire('!').then(() => this.goToLeaderboard());
            return;
          }
          let userId = +value[0];
          let testId = +value[1];
          this.getAttemptFromServer(userId, testId);
        }
      });
  }

  goToLeaderboard() {
    this.router.navigate(['/leaderboard']);
  }

  private getQueryParams() {
    return this.route.queryParamMap.pipe(
      map((params: ParamMap) => [params.get('user'), params.get('test')]),
    );
  }

  private getAttemptFromServer(userId: number, testId: number) {
    this.attemptService.getAttempt(userId, testId, localStorage.getItem('access-jwt') ?? '')
      .subscribe({
        next: value => {
          value.answerToSubmittedValueMap = new Map(Object.entries(value.answerToSubmittedValueMap));
          this.attempt = value
        },
        error: (err: HttpErrorResponse) => {
          if (err.status == 0) {
            setTimeout(() => this.getAttemptFromServer(userId, testId), environment.retryDelay);
          } else {
            Swal.fire(err.error.message)
          }
        }
      })
  }
}
