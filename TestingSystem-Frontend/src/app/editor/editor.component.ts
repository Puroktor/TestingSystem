import {Component, Input, OnInit} from '@angular/core';
import {FullTest} from '../entity/FullTest';
import {map} from 'rxjs/operators';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import Swal from 'sweetalert2';
import {TestService} from '../service/test.service';
import jwt_decode from 'jwt-decode';
import {UserService} from '../service/user.service';

@Component({
  selector: 'app-editor',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.css']
})
export class EditorComponent implements OnInit {

  @Input() test?: FullTest;
  testId: number | null = null;
  hasSent: boolean = false;
  private userId!: number;

  constructor(private testService: TestService, private userService: UserService, private route: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit(): void {
    let token = localStorage.getItem('access-jwt')
    if (token == null) {
      this.goToHomePage();
      return;
    } else {
      let decoded: any = jwt_decode(token);
      if (!decoded.authorities.includes('USER_EDIT')) {
        this.goToHomePage();
        return;
      } else {
        this.userId = decoded.id;
      }
    }
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

  questionsCountChanged(event: any) {
    if (this.test != undefined) {
      this.test.testInfo.questionsCount = +((event.target as HTMLInputElement).value);
    }
  }

  nameChanged(event: any) {
    if (this.test != undefined) {
      this.test.testInfo.name = (event.target as HTMLInputElement).value;
    }
  }

  langChanged(event: any) {
    if (this.test != undefined) {
      this.test.testInfo.programmingLang = (event.target as HTMLInputElement).value;
    }
  }

  goToHomePage() {
    this.router.navigate(['/']);
  }

  newQuestion() {
    this.test?.questions.push({id: null, text: '', maxScore: 0, answers: []});
  }

  submit() {
    if (!this.validateTest()) {
      return;
    }
    if (this.test != null) {
      this.hasSent = true;
      if (this.testId == null) {
        this.testService.createTest(this.test, localStorage.getItem('access-jwt') ?? '').subscribe({
          next: () => this.goToHomePage(),
          error: (err: HttpErrorResponse) => Swal.fire(err.error.message).then(() => this.hasSent = false)
        });
      } else {
        this.testService.updateTest(this.testId, this.test, localStorage.getItem('access-jwt') ?? '')
          .subscribe({
            next: () => this.goToHomePage(),
            error: (err: HttpErrorResponse) => Swal.fire(err.error.message).then(() => this.hasSent = false)
          });
      }
    }
  }

  deleteTest() {
    if (this.testId != null) {
      this.hasSent = true;
      this.testService.deleteTest(this.testId, localStorage.getItem('access-jwt') ?? '').subscribe({
        next: () => this.goToHomePage(),
        error: (err: HttpErrorResponse) => Swal.fire(err.error.message).then(() => this.hasSent = false)
      });
    }
  }

  private validateTest(): boolean {
    if (this.test == null) {
      return false;
    } else if (this.test.testInfo.name.length == 0 || this.test.testInfo.name.length > 50) {
      Swal.fire('Test name must be between 1 and 50 characters');
      return false;
    } else if (this.test.testInfo.programmingLang.length == 0 || this.test.testInfo.programmingLang.length > 50) {
      Swal.fire('Programming language must be between 1 and 50 characters');
      return false;
    } else if (this.test.testInfo.questionsCount < 1 || this.test.testInfo.questionsCount > 50) {
      Swal.fire('Questions count must be between 1 and 50');
      return false;
    } else if (this.test.questions.length == 0) {
      Swal.fire('Enter at least one question');
      return false;
    } else {
      for (let question of this.test.questions) {
        if (question.maxScore < 1) {
          Swal.fire('Question score must be >= 1');
          return false;
        } else if (question.text.length == 0 || question.text.length > 200) {
          Swal.fire('Question must be between 1 and 200 characters');
          return false;
        } else if (question.answers.length == 0) {
          Swal.fire('Enter at least one answer');
          return false;
        } else {
          for (let answer of question.answers) {
            if (question.text.length == 0 || question.text.length > 100) {
              Swal.fire('Your answer must be between 1 and 100 characters');
              return false;
            }
          }
        }
      }
      return true;
    }
  }

  private getTestId() {
    return this.route.queryParamMap.pipe(
      map((params: ParamMap) => params.get('id')),
    );
  }

  private generateSampleTest() {
    this.test = {testInfo: {id: null, name: '', programmingLang: '', questionsCount: 0}, questions: []};
  }

  private getTestFromServer(value: number) {
    this.testService.getTest(value, localStorage.getItem('access-jwt') ?? '')
      .subscribe({
        next: value => this.test = value,
        error: (err: HttpErrorResponse) => {
          Swal.fire(err.error.message).then(() => this.goToHomePage())
        }
      })
  }
}
