import { Component, OnInit } from '@angular/core';
import {Page} from "../entity/Page";
import {TestService} from "../test.service";
import {ActivatedRoute, ParamMap} from "@angular/router";
import {map} from "rxjs/operators";
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";

@Component({
  selector: 'app-tests-list',
  templateUrl: './tests-list.component.html',
  styleUrls: ['./tests-list.component.css']
})
export class TestsListComponent implements OnInit {

  pageSize: number = 5;
  page?: Page;

  constructor(private testService: TestService, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.getPageNumber()
      .subscribe({next: value => this.getPageFromServer(value == null ? 0 : +value)});
  }

  getPageNumber() {
    return this.route.queryParamMap.pipe(
      map((params: ParamMap) => params.get('page')),
    );
  }

  getPageFromServer(pageNumber: number) {
    this.testService.fetchPage("", pageNumber, this.pageSize)
      .subscribe({
        next: value => this.page = value,
        error: (err: HttpErrorResponse) => Swal.fire(err.message)
      })
  }
}
