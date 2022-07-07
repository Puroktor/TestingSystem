import {Component, Input, OnInit} from '@angular/core';
import {FullTest} from "../entity/FullTest";
import {map} from "rxjs/operators";
import {ActivatedRoute, ParamMap, Router} from "@angular/router";
import {HttpErrorResponse} from "@angular/common/http";
import Swal from "sweetalert2";
import {TestService} from "../test.service";
import jwt_decode from "jwt-decode";

@Component({
  selector: 'app-editor',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.css']
})
export class EditorComponent implements OnInit {

  @Input() test?: FullTest;
  private userId!: number;
  private token!: string;
  testId: number | null = null;
  hasSent: boolean = false;

  constructor(private testService: TestService, private route: ActivatedRoute, private router: Router) {
    let token = localStorage.getItem("jwt")
    if (token == null) {
      this.goToHomePage();
    } else {
      let decoded: any = jwt_decode(token);
      if (!decoded.authorities.includes("USER_EDIT")) {
        this.goToHomePage();
      } else {
        this.token = token;
        this.userId = decoded.id;
      }
    }
  }

  ngOnInit(): void {
    this.getTestId().subscribe({
      next: value => {
        if (value == null) {
          this.generateSampleTest();
        } else {
          this.testId = +value;
          this.getTestFromServer(+value);
        }
      }
    });
  }

  generateSampleTest() {
    this.test = {testInfoDto: {id: null, name: "", programmingLang: "", questionsCount: 0}, questionList: []};
  }

  private getTestId() {
    return this.route.queryParamMap.pipe(
      map((params: ParamMap) => params.get('id')),
    );
  }

  private getTestFromServer(value: number) {
    this.testService.getTest(value, this.token)
      .subscribe({
        next: value => this.test = value,
        error: (err: HttpErrorResponse) => {
          Swal.fire(err.error.message).then(() => this.goToLoginPage())
        }
      })
  }

  questionsCountChanged(event: any) {
    if (this.test != undefined) {
      this.test.testInfoDto.questionsCount = +((event.target as HTMLInputElement).value);
    }
  }

  nameChanged(event: any) {
    if (this.test != undefined) {
      this.test.testInfoDto.name = (event.target as HTMLInputElement).value;
    }
  }

  langChanged(event: any) {
    if (this.test != undefined) {
      this.test.testInfoDto.programmingLang = (event.target as HTMLInputElement).value;
    }
  }

  private goToLoginPage() {
    this.router.navigate(['/login']);
  }

  goToHomePage() {
    this.router.navigate(['/']);
  }

  newQuestion() {
    this.test?.questionList.push({id: null, text: "", maxScore: 0, answers: []});
  }

  submit() {
    if (this.test != null) {
      this.hasSent = true;
      if (this.testId == null) {
        this.testService.createTest(this.test, this.token).subscribe({
          next: () => this.goToHomePage(),
          error: (err: HttpErrorResponse) => Swal.fire(err.error.message).then(() => this.hasSent = false)
        });
      } else {
        this.testService.updateTest(this.testId, this.test, this.token)
          .subscribe({
            next: () => this.goToHomePage(),
            error: (err: HttpErrorResponse) => Swal.fire(err.error.message).then(() => this.hasSent = false)
          });
      }
    }
  }

  deleteTest() {
    if(this.testId!=null){
      this.hasSent = true;
      this.testService.deleteTest(this.testId, this.token).subscribe({
        next: () => this.goToHomePage(),
        error: (err: HttpErrorResponse) => Swal.fire(err.error.message).then(() => this.hasSent = false)
      });
    }
  }
}
