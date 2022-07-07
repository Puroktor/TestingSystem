import {Component, Input, OnInit} from '@angular/core';
import {TestCard} from "../../entity/TestCard";
import {Router} from "@angular/router";
import Swal from 'sweetalert2'

@Component({
  selector: 'app-test-card',
  templateUrl: './test-card.component.html',
  styleUrls: ['./test-card.component.css']
})
export class TestCardComponent implements OnInit {

  @Input() card!: TestCard

  constructor(private router: Router) {
  }

  ngOnInit(): void {
  }

  goToTest() {
    if (localStorage.getItem('jwt') == null) {
      Swal.fire('Login to see the test!');
    } else {
      this.router.navigate([`test`], {
        queryParams: {
          'id': this.card.id,
        }
      });
    }
  }

}
