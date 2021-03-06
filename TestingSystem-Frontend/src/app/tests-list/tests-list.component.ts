import {Component, OnInit} from '@angular/core';
import {Page} from "../entity/Page";
import {TestService} from "../service/test.service";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {map} from "rxjs/operators";
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";
import {TestCard} from "../entity/TestCard";
import {environment} from "../../environments/environment";
import {UserService} from "../service/user.service";

@Component({
  selector: 'app-tests-list',
  templateUrl: './tests-list.component.html',
  styleUrls: ['./tests-list.component.css']
})
export class TestsListComponent implements OnInit {

  pageSize: number = 3;
  pageNumb: number = 0;
  canEdit: boolean = false;
  lang: string = '';
  page?: Page<TestCard>;

  constructor(private userService: UserService, private testService: TestService,
              private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    if (this.userService.username.getValue() == null) {
      Swal.fire('Login to see this page!').then(() => this.router.navigate(['/login']));
      return;
    }
    this.canEdit = this.userService.authorities.getValue().includes('USER_EDIT');
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
        error: (err: HttpErrorResponse) => {
          if (err.status == 0) {
            setTimeout(() => this.getPageFromServer(pageNumber, programmingLang), environment.retryDelay);
          } else {
            Swal.fire(err.error.message)
          }
        }
      })
  }

  filterPage() {
    let filterValue = (document.getElementById('filterInput') as HTMLInputElement).value;
    this.router.navigate(['/tests-list'], {
      queryParams: {page: this.pageNumb, filter: filterValue}
    });
  }
}
