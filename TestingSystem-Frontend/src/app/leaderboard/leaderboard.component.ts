import {Component, OnInit} from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";
import jwt_decode from "jwt-decode";
import {UserService} from "../service/user.service";
import {LeaderboardPage} from "../entity/LeaderboardPage";
import {map} from "rxjs/operators";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {environment} from "../../environments/environment";
import {BoardTestRecord} from "../entity/BoardTestRecord";
import {IDropdownSettings} from "ng-multiselect-dropdown";

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
  dropdownSettings:IDropdownSettings;

  constructor(private userService: UserService, private route: ActivatedRoute, private router: Router) {
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
    let token = localStorage.getItem('access-jwt');
    if (token == null) {
      Swal.fire('Login to see this page!').then(() => this.router.navigate(['/login']));
      return;
    }
    let decoded: any = jwt_decode(token);
    this.nickName = decoded.sub;
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
    this.userService.getLeaderboard(pageNumb, this.pageSize, localStorage.getItem('access-jwt') ?? '')
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
      })
  }
}
