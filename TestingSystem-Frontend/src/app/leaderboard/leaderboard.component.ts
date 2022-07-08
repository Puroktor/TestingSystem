import {Component, OnInit} from '@angular/core';
import {Leaderboard} from "../entity/Leaderboard";
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";
import jwt_decode from "jwt-decode";
import {ActionsService} from "../service/actions.service";

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {

  leaderBoard?: Leaderboard;
  nickName: string | null = null;

  constructor(private actionService: ActionsService) {
  }

  ngOnInit(): void {
    let token = localStorage.getItem('access-jwt')
    if (token == null) {
      this.nickName = null;
    } else {
      let decoded: any = jwt_decode(token);
      this.nickName = decoded.sub;
    }
    this.getBoardFromServer();
  }

  private getBoardFromServer() {
    this.actionService.getLeaderboard()
      .subscribe({
        next: value => {
          value.userRecords.forEach((userRecord) => {
            userRecord.testToScoreMap = new Map(Object.entries(userRecord.testToScoreMap));
          })
          this.leaderBoard = value
        },
        error: (err: HttpErrorResponse) => Swal.fire(err.error.message)
      })
  }
}
