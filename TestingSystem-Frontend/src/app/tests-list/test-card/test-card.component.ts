import {Component, Input, OnInit} from '@angular/core';
import {TestCard} from "../../entity/TestCard";
import {Router} from "@angular/router";

@Component({
  selector: 'app-test-card',
  templateUrl: './test-card.component.html',
  styleUrls: ['./test-card.component.css']
})
export class TestCardComponent implements OnInit {

  @Input() card!: TestCard;
  @Input() canEdit!: boolean;

  constructor(private router: Router) {
  }

  ngOnInit(): void {
  }

  goToTest() {
    this.router.navigate([`test`], {
      queryParams: {
        'id': this.card.id,
      }
    });
  }

}
