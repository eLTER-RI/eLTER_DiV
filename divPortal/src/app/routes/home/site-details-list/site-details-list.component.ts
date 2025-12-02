import { Component, Input, OnInit } from '@angular/core';

@Component({
    selector: 'app-site-details-list',
    templateUrl: './site-details-list.component.html',
    styleUrls: ['./site-details-list.component.scss'],
    standalone: false
})
export class SiteDetailsListComponent implements OnInit {

  @Input() siteIds: number[];

  constructor() { }

  ngOnInit(): void {
  }

}
