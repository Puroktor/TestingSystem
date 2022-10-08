import {Component, OnInit} from '@angular/core';
import {map} from "rxjs/operators";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import Swal from "sweetalert2";
import {HttpErrorResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {AttemptService} from "../service/attempt.service";
import {Attempt} from "../entity/Attempt";
import {UserService} from "../service/user.service";

@Component({
  selector: 'app-attempt',
  templateUrl: './attempt.component.html',
  styleUrls: ['./attempt.component.css']
})
export class AttemptComponent implements OnInit {

  attempt?: Attempt;

  constructor(private userService: UserService, private attemptService: AttemptService,
              private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    if (this.userService.username.getValue() == null) {
      Swal.fire('Login to see this page!').then(() => this.router.navigate(['/login']));
      return;
    }
    if (!this.userService.authorities.getValue().includes('USER_EDIT')) {
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
          let attemptId = +value;
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
    this.attemptService.getAttempt(attemptId)
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
