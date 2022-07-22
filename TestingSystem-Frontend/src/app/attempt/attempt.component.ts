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
    this.getQueryParam()
      .subscribe({
        next: value => {
          if (value == null) {
            Swal.fire('No such attempt').then(() => this.goToLeaderboard());
            return;
          }
          let attemptId = +value[0];
          this.getAttemptFromServer(attemptId);
        }
      });
  }

  goToLeaderboard() {
    this.router.navigate(['/leaderboard']);
  }

  private getQueryParam() {
    return this.route.queryParamMap.pipe(
      map((params: ParamMap) => params.get('id')),
    );
  }

  private getAttemptFromServer(attemptId: number) {
    this.attemptService.getAttempt(attemptId, localStorage.getItem('access-jwt') ?? '')
      .subscribe({
        next: value => {
          value.answerToSubmittedValueMap = new Map(Object.entries(value.answerToSubmittedValueMap));
          this.attempt = value
        },
        error: (err: HttpErrorResponse) => {
          if (err.status == 0) {
            setTimeout(() => this.getAttemptFromServer(attemptId), environment.retryDelay);
          } else {
            Swal.fire(err.error.message)
          }
        }
      })
  }
}
