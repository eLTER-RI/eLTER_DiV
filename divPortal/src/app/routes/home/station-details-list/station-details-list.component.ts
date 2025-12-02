import { Component, Input, OnInit } from '@angular/core';

@Component({
    selector: 'app-station-details-list',
    templateUrl: './station-details-list.component.html',
    styleUrls: ['./station-details-list.component.scss'],
    standalone: false
})
export class StationDetailsListComponent implements OnInit {

  @Input() stationIds: number[];

  constructor() { }

  ngOnInit(): void {
  }

}
