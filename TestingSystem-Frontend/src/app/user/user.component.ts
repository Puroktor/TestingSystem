import {Component, OnInit} from '@angular/core';
import Swal from "sweetalert2";
import {map} from "rxjs/operators";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {HttpErrorResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {UserService} from "../service/user.service";
import {User} from "../entity/User";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  user?: User;

  constructor(private userService: UserService, private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    if (this.userService.username.getValue() == null) {
      Swal.fire('Login to see this page!').then(() => this.router.navigate(['/login']));
      return;
    }
    if (!this.userService.authorities.getValue().includes('USER_EDIT')) {
      Swal.fire('Only teacher can browse this page!').then(() => this.goToLeaderboard());
      return;
    }
    this.getQueryParam()
      .subscribe({
        next: value => {
          if (value == null) {
            Swal.fire('No such user').then(() => this.goToLeaderboard());
            return;
          }
          let userId = +value[0];
          this.getUserFromServer(userId);
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

  private getUserFromServer(id: number) {
    this.userService.getUser(id)
      .subscribe({
        next: value => this.user = value,
        error: (err: HttpErrorResponse) => {
          if (err.status == 0) {
            setTimeout(() => this.getUserFromServer(id), environment.retryDelay);
          } else {
            Swal.fire(err.error.message)
          }
        }
      });
  }
}
