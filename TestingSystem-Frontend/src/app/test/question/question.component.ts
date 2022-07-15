import {Component, Input, OnInit} from '@angular/core';
import {Question} from "../../entity/Question";

@Component({
  selector: 'app-question',
  templateUrl: './question.component.html',
  styleUrls: ['./question.component.css']
})
export class QuestionComponent implements OnInit {

  @Input() question!: Question;
  @Input() index!: number;
  @Input() viewing!: boolean;
  @Input() submittedValues?: Map<string, boolean>;
  score: number = 0;

  constructor() {
  }

  ngOnInit(): void {
    if (this.submittedValues == null) {
      return;
    }
    let correct = 0, all = 0;
    for (let answer of this.question.answers) {
      let submitted = this.submittedValues.get(answer.id?.toString() ?? '');
      if (submitted) {
        correct += answer.isRight ? 1 : -1;
      }
      if (answer.isRight) {
        all++;
      }
      this.score = Math.max(correct / all * this.question.maxScore, 0);
    }
  }

}
