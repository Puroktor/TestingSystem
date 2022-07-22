import {Component, OnInit} from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";
import {LeaderboardPage} from "../entity/LeaderboardPage";
import {map} from "rxjs/operators";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {environment} from "../../environments/environment";
import {BoardTestRecord} from "../entity/BoardTestRecord";
import {IDropdownSettings} from "ng-multiselect-dropdown";
import {AttemptService} from "../service/attempt.service";
import {UserService} from "../service/user.service";

@Component({
  selector: 'app-leaderboard',
  templateUrl: './leaderboard.component.html',
  styleUrls: ['./leaderboard.component.css']
})
export class LeaderboardComponent implements OnInit {

  leaderboard?: LeaderboardPage;
  chosenTestRecords: BoardTestRecord[] = [];
  nickName: string | null = null;
  pageSize: number = 3;
  isTeacher: boolean = false;
  dropdownSettings: IDropdownSettings;

  constructor(private attemptService: AttemptService, private userService: UserService,
              private route: ActivatedRoute, private router: Router) {
    this.dropdownSettings = {
      singleSelection: false,
      idField: 'id',
      textField: 'name',
      selectAllText: 'Select All',
      unSelectAllText: 'Unselect All',
      allowSearchFilter: true
    };
  }

  ngOnInit(): void {
    if (this.userService.username.getValue() == null) {
      Swal.fire('Login to see this page!').then(() => this.router.navigate(['/login']));
      return;
    }
    this.nickName = this.userService.username.getValue();
    this.isTeacher = this.userService.authorities.getValue().includes('USER_EDIT');
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

  setFields() {
    if (!this.leaderboard) {
      return;
    }
    for (let record of this.chosenTestRecords) {
      record.passingScore = this.leaderboard.testRecords.find(test => test.id == record.id)?.passingScore ?? 0;
    }
  }

  private getBoardFromServer(pageNumb: number) {
    this.attemptService.getLeaderboard(pageNumb, this.pageSize)
      .subscribe({
        next: value => {
          value.userRecords.content.forEach(userRecord => {
            userRecord.testToScoreMap = new Map(Object.entries(userRecord.testToScoreMap));
          })
          this.leaderboard = value
        },
        error: (err: HttpErrorResponse) => {
          if (err.status == 0) {
            setTimeout(() => this.getBoardFromServer(pageNumb), environment.retryDelay);
          } else {
            Swal.fire(err.error.message);
          }
        }
      });
  }
}
