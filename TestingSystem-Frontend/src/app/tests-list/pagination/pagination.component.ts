import {Component, Input, OnInit} from '@angular/core';
import {Page} from "../../entity/Page";


@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.css']
})
export class PaginationComponent implements OnInit {

  @Input() page?: Page

  constructor() {
  }

  ngOnInit(): void {
  }

}
