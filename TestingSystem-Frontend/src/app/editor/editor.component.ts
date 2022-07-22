import {Component, Input, OnInit} from '@angular/core';
import {FullTest} from '../entity/FullTest';
import {map} from 'rxjs/operators';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import Swal from 'sweetalert2';
import {TestService} from '../service/test.service';
import jwt_decode from 'jwt-decode';
import {UserService} from '../service/user.service';
import {environment} from "../../environments/environment";

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
      this.goToTestsPage();
      return;
    } else {
      let decoded: any = jwt_decode(token);
      if (!decoded.authorities.includes('USER_EDIT')) {
        Swal.fire('You cannot browse this page').then(() => this.goToTestsPage());
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

  testTypeChanged(event: any) {
    if (this.test != undefined) {
      this.test.testType = (event.target as HTMLSelectElement).value;
      if (this.test.testType == 'WITH_BANK') {
        this.test.questions.forEach(question => question.questionTemplateIndex = null)
      } else {
        this.test.questions.forEach(question => question.questionTemplateIndex = 0)
      }
    }
  }

  goToTestsPage() {
    this.router.navigate(['/tests-list']);
  }

  newQuestion() {
    this.test?.questions.push({
      id: null,
      text: '',
      questionTemplateIndex: this.test.testType == 'WITH_BANK' ? null : 0,
      maxScore: 0,
      answers: []
    });
  }

  submit() {
    if (!this.validateTest()) {
      return;
    }
    if (this.test != null) {
      if (this.testId == null) {
        this.createTest(this.test);
      } else {
        this.updateTest(this.testId, this.test);
      }
    }
  }

  private createTest(test: FullTest) {
    this.hasSent = true;
    this.testService.createTest(test).subscribe({
      next: () => this.goToTestsPage(),
      error: (err: HttpErrorResponse) => {
        if (err.status == 0) {
          setTimeout(() => this.createTest(test), environment.retryDelay);
        } else {
          Swal.fire(err.error.message).then(() => this.hasSent = false)
        }
      }
    });
  }

  private updateTest(testId: number, test: FullTest) {
    this.hasSent = true;
    this.testService.updateTest(testId, test)
      .subscribe({
        next: () => this.goToTestsPage(),
        error: (err: HttpErrorResponse) => {
          if (err.status == 0) {
            setTimeout(() => this.updateTest(testId, test), environment.retryDelay);
          } else {
            Swal.fire(err.error.message).then(() => this.hasSent = false)
          }
        }
      });
  }

  deleteTest() {
    if (this.testId != null) {
      this.hasSent = true;
      this.testService.deleteTest(this.testId).subscribe({
        next: () => this.goToTestsPage(),
        error: (err: HttpErrorResponse) => {
          if (err.status == 0) {
            setTimeout(() => this.deleteTest(), environment.retryDelay);
          } else {
            Swal.fire(err.error.message).then(() => this.hasSent = false)
          }
        }
      });
    }
  }

  private validateTest(): boolean {
    if (this.test == undefined) {
      return false;
    }
    let violations = new Set<string>;
    if (this.test.name.length == 0 || this.test.name.length > 50) {
      violations.add('Test name must be between 1-50 characters');
    }
    if (this.test.programmingLang.length == 0 || this.test.programmingLang.length > 50) {
      violations.add('Programming language must be 1-50 characters');
    }
    if (this.test.questionsCount < 1 || this.test.questionsCount > 50) {
      violations.add('Questions count must be 1-50');
    }
    if (this.test.passingScore < 1 || this.test.passingScore > 100) {
      violations.add('Passing score must be 1-100');
    }
    if (this.test.questions.length < this.test.questionsCount) {
      violations.add('Number of questions must be >= question count');
    }
    let questionTemplatesPresence = new Array(this.test.questionsCount).fill(false)
    for (let question of this.test.questions) {
      if (question.maxScore < 1) {
        violations.add('Question score must be >= 1');
      }
      if (question.text.length == 0 || question.text.length > 500) {
        violations.add('Question must be 1-500 characters');
      }
      if (this.test.testType == 'WITH_BANK') {
        if (question.questionTemplateIndex != null) {
          violations.add('Template index must be absent');
        }
      } else if (this.test.testType == 'WITH_QUESTION_OPTIONS') {
        if (question.questionTemplateIndex == null) {
          violations.add('Enter question template index');
        } else if (question.questionTemplateIndex >= this.test.questionsCount) {
          violations.add('Question template index must be <= question count');
        } else {
          questionTemplatesPresence[question.questionTemplateIndex] = true;
        }
      }
      if (question.answers.length == 0) {
        violations.add('Enter at least 1 answer');
      } else if (question.answers.length > 10) {
        violations.add('Answer count must be 1-10');
      }
      let hasRight = false;
      for (let answer of question.answers) {
        if (answer.text.length == 0 || answer.text.length > 200) {
          violations.add('Answer must be 1-200 characters');
          break;
        }
        hasRight = hasRight || answer.isRight;
      }
      if (!hasRight) {
        violations.add('Question must have at least 1 right answer');
      }
    }
    if (this.test.testType == 'WITH_QUESTION_OPTIONS') {
      for (let isPresent of questionTemplatesPresence) {
        if (!isPresent) {
          violations.add("All question templates must have at least 1 question option");
          break;
        }
      }
    }
    if (violations.size != 0) {
      let violationsStr = '';
      violations.forEach(value => {
        violationsStr = violationsStr.concat(value).concat('\n');
      });
      Swal.fire(violationsStr);
      return false;
    } else {
      return true;
    }
  }

  private getTestId() {
    return this.route.queryParamMap.pipe(
      map((params: ParamMap) => params.get('id')),
    );
  }

  private generateSampleTest() {
    this.test = {id: null, name: '', programmingLang: '', questionsCount: 0, passingScore:0, testType: "WITH_BANK", questions: []};
  }

  private getTestFromServer(value: number) {
    this.testService.getTest(value)
      .subscribe({
        next: value => this.test = value,
        error: (err: HttpErrorResponse) => {
          if (err.status == 0) {
            setTimeout(() => this.getTestFromServer(value), environment.retryDelay);
          } else {
            Swal.fire(err.error.message).then(() => this.goToTestsPage());
          }
        }
      })
  }
}
