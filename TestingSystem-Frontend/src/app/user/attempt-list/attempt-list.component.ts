import {Component, Input, OnInit} from '@angular/core';
import {IDropdownSettings} from "ng-multiselect-dropdown";
import {AttemptResult} from "../../entity/AttemptResult";
import {HttpErrorResponse} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import Swal from "sweetalert2";
import {AttemptService} from "../../service/attempt.service";

@Component({
  selector: 'app-attempt-list',
  templateUrl: './attempt-list.component.html',
  styleUrls: ['./attempt-list.component.css']
})
export class AttemptListComponent implements OnInit {

  @Input() userId!: number;
  dropdownSettings: IDropdownSettings;
  attempts?: AttemptResult[];
  chosenAttempts: AttemptResult[] = [];
  tests: any[] = [];
  chosenTests: any[] = [];

  constructor(private attemptService: AttemptService) {
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
    this.getAttemptsResultsFromServer();
  }

  onFilterChange() {
    if (!this.attempts) {
      return;
    }
    this.chosenAttempts = [];
    for (let attempt of this.attempts) {
      if(this.chosenTests.find(test => test.id == attempt.testId)){
        this.chosenAttempts.push(attempt);
      }
    }
  }

  private getAttemptsResultsFromServer() {
    this.attemptService.getAttemptsResults(this.userId)
      .subscribe({
        next: value => {
          let testsSet = new Set<number>;
          for (let attempt of value) {
            if (!testsSet.has(attempt.testId)) {
              this.tests.push({id: attempt.testId, name: attempt.testName});
              testsSet.add(attempt.testId);
            }
          }
          this.attempts = value;
        },
        error: (err: HttpErrorResponse) => {
          if (err.status == 0) {
            setTimeout(() => this.getAttemptsResultsFromServer(), environment.retryDelay);
          } else {
            Swal.fire(err.error.message)
          }
        }
      })
  }
}
