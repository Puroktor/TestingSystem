import {Component, Input, OnInit} from '@angular/core';
import {Answer} from "../../entity/Answer";
import {Question} from "../../entity/Question";

@Component({
  selector: 'app-editor-answer',
  templateUrl: './editor-answer.component.html',
  styleUrls: ['./editor-answer.component.css']
})
export class EditorAnswerComponent implements OnInit {

  @Input() answer!: Answer;
  @Input() question!: Question;
  @Input() index!: number;

  constructor() {
  }

  ngOnInit(): void {
  }

  onChanged(event: any) {
    this.answer.text = (event.target as HTMLInputElement).value;
  }

  onChecked(event: any) {
    this.answer.isRight = (event.target as HTMLInputElement).checked;
  }

  delete() {
    this.question.answers.splice(this.question.answers.indexOf(this.answer), 1);
  }
}
