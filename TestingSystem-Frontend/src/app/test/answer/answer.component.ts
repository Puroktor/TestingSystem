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

  constructor() { }

  ngOnInit(): void {
  }

  onChecked(event : any){
    this.answer.isSelected = (event.target as HTMLInputElement).checked;
  }

}
