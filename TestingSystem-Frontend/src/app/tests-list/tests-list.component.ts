import {Component, OnInit} from '@angular/core';
import {Page} from "../entity/Page";
import {TestService} from "../service/test.service";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {map} from "rxjs/operators";
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";
import jwt_decode from "jwt-decode";

@Component({
  selector: 'app-tests-list',
  templateUrl: './tests-list.component.html',
  styleUrls: ['./tests-list.component.css']
})
export class TestsListComponent implements OnInit {

  pageSize: number = 5;
  pageNumb: number = 0;
  canEdit: boolean = false;
  lang: string = '';
  page?: Page;

  constructor(private testService: TestService, private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    let token = localStorage.getItem('access-jwt')
    if (token != null) {
      let decoded: any = jwt_decode(token);
      this.canEdit = decoded.authorities.includes('USER_EDIT');
    }
    this.getPageNumber()
      .subscribe({
        next: value => {
          this.pageNumb = value[0] == null ? 0 : +value[0];
          this.pageNumb = Math.max(this.pageNumb, 0);
          this.lang = value[1] ?? '';
          (document.getElementById('filterInput') as HTMLInputElement).value = this.lang;
          this.getPageFromServer(this.pageNumb, this.lang);
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
      queryParams: {page: this.pageNumb, filter: filterValue}
    });
  }
}
