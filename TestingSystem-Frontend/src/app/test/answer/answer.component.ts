import {Component, Input, OnInit} from '@angular/core';
import {Answer} from "../../entity/Answer";

@Component({
  selector: 'app-answer',
  templateUrl: './answer.component.html',
  styleUrls: ['./answer.component.css']
})
export class AnswerComponent implements OnInit {

  @Input() answer!: Answer;
  @Input() index!: number;
  @Input() viewing! : boolean;
  @Input() submittedValue?: boolean;

  constructor() {
  }

  ngOnInit(): void {
  }

  onChecked(event: any) {
    this.answer.isRight = (event.target as HTMLInputElement).checked;
  }

}
