import {Component, Input, OnInit} from '@angular/core';
import {Question} from "../../entity/Question";
import {FullTest} from "../../entity/FullTest";

@Component({
  selector: 'app-editor-question',
  templateUrl: './editor-question.component.html',
  styleUrls: ['./editor-question.component.css']
})
export class EditorQuestionComponent implements OnInit {

  @Input() question!: Question;
  @Input() test!: FullTest;
  @Input() index!: number;

  constructor() {
  }

  ngOnInit(): void {
  }

  indexChanged(event: any) {
    this.question.questionTemplateIndex = +((event.target as HTMLInputElement).value) - 1;
  }

  newAnswer() {
    if (this.question.answers.length < 10) {
      this.question.answers.push({id: null, text: '', isRight: false});
    }
  }

  deleteQuestion() {
    this.test.questions.splice(this.test.questions.indexOf(this.question), 1);
  }
}
