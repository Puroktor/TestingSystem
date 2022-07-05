import {Component, OnInit} from '@angular/core';
import {Page} from "../entity/Page";
import {TestService} from "../test.service";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
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

  constructor(private testService: TestService, private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit() {
    this.getPageNumber()
      .subscribe({
        next: value => {
          let pageNumb = value[0] == null ? 0 : +value[0];
          pageNumb = Math.max(pageNumb, 0);
          let lang = value[1] ?? '';
          (document.getElementById('filterInput') as HTMLInputElement).value = lang;
          this.getPageFromServer(pageNumb, lang);
        }
      });
  }

  getPageNumber() {
    return this.route.queryParamMap.pipe(
      map((params: ParamMap) => [params.get('page'), params.get('filter')]),
    );
  }

  getPageFromServer(pageNumber: number, programmingLang: string) {
    this.testService.fetchPage(programmingLang, pageNumber, this.pageSize)
      .subscribe({
        next: value => this.page = value,
        error: (err: HttpErrorResponse) => Swal.fire(err.error.message)
      })
  }

  filterPage() {
    let filterValue = (document.getElementById('filterInput') as HTMLInputElement).value;
    this.router.navigate([''], {
      queryParams: {page: this.page?.number, filter: filterValue}
    });
  }
}
