import {Component, OnInit} from '@angular/core';
import {UserService} from "../user.service";
import {Leaderboard} from "../entity/Leaderboard";
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {

  leaderBoard?: Leaderboard;
  nickName: string | null = null;

  constructor(private userService: UserService) {
  }

  ngOnInit(): void {
    this.nickName = localStorage.getItem("nickname");
    this.getBoardFromServer();
  }

  getBoardFromServer() {
    this.userService.getLeaderboard()
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
