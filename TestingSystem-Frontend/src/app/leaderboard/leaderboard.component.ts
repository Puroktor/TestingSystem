import {Component, OnInit} from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";
import jwt_decode from "jwt-decode";
import {UserService} from "../service/user.service";
import {LeaderboardPage} from "../entity/LeaderboardPage";
import {map} from "rxjs/operators";
import {ActivatedRoute, ParamMap} from "@angular/router";

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {

  leaderboard?: LeaderboardPage;

  nickName: string | null = null;
  pageSize: number = 5;

  constructor(private userService: UserService, private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    let token = localStorage.getItem('access-jwt')
    if (token == null) {
      this.nickName = null;
    } else {
      let decoded: any = jwt_decode(token);
      this.nickName = decoded.sub;
    }
    this.getPageNumber()
      .subscribe({
        next: value => {
          let pageNumb = value == null ? 0 : +value;
          pageNumb = Math.max(pageNumb, 0);
          this.getBoardFromServer(pageNumb);
        }
      });
  }

  getPageNumber() {
    return this.route.queryParamMap.pipe(
      map((params: ParamMap) => params.get('page')),
    );
  }

  private getBoardFromServer(pageNumb: number) {
    this.userService.getLeaderboard(pageNumb, this.pageSize)
      .subscribe({
        next: value => {
          value.userRecords.content.forEach((userRecord) => {
            userRecord.testToScoreMap = new Map(Object.entries(userRecord.testToScoreMap));
          })
          this.leaderboard = value
        },
        error: (err: HttpErrorResponse) => Swal.fire(err.error.message)
      })
  }
}
