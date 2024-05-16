import { Timeseries } from './../../../shared/model/timeseries-db';
import { Router } from '@angular/router'; 
import {  ToastrService } from 'ngx-toastr';
import { Station } from './../../../shared/model/station-db';
import { DiagramTimeseries } from './../../../shared/model/diagram-timeseries';

import { StationTimeseries } from './../../../shared/model/station-timeseries-db';
import { SharedService } from './../../../shared/service/shared.service';
import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { TimeseriesIDTO } from 'src/app/shared/model/timeseries-ib';

@Component({
  selector: 'app-station-details',
  templateUrl: './station-details.component.html',
  styleUrls: ['./station-details.component.scss']
})
export class StationDetailsComponent implements OnInit, OnChanges {

  @Input() stationId: number; 
  
  scrollbarOptions = {  theme: 'dark-thick', scrollButtons: { enable: true },  setHeight: '70vh'};

  stationTimeSeries: StationTimeseries;
  checkedTimeseries: boolean[][];
  selectAllTimeseries: boolean;

  constructor(private sharedService: SharedService,
              private toastr: ToastrService,
              private router: Router) { 
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.loadStation();
  }

  async loadStation() {
    // this.stationTimeSeries = undefined;
    const timeseriesDTO: TimeseriesIDTO = new TimeseriesIDTO();
    timeseriesDTO.stationId = this.stationId;
    // timeseriesDTO.phenomenLabels = this.usedPhenomenonsLabelEnFilter;

    const response = await this.sharedService.post('sos/loadTimeSeries', timeseriesDTO);
    this.stationTimeSeries = response.entity;

    this.checkedTimeseries = [];
    this.selectAllTimeseries = false;

    this.stationTimeSeries?.timeseriesPhenomenon?.forEach( (tsPh, i) => {
        this.checkedTimeseries[i] = [];
        tsPh.timeseries.forEach((ts,j) => {
              this.checkedTimeseries[i][j] = false;
        });
    });

  }

  btn_addTimeSeriesToGraph() {
    const newTimeseries: DiagramTimeseries[] = [];

    let timeSeries: Timeseries[] = [];
    this.stationTimeSeries.timeseriesPhenomenon.forEach( tsPh => {
            timeSeries = timeSeries.concat(tsPh.timeseries);
    });

    for (let i = 0; i < this.checkedTimeseries.length ; i++) {
        for (let j = 0; j < this.checkedTimeseries[i].length ; j++) {
            if (this.checkedTimeseries[i][j] === true) {
                const ts = new DiagramTimeseries();
                ts.timeseriesId = this.stationTimeSeries.timeseriesPhenomenon[i].timeseries[j].id;
                ts.firstTime = this.stationTimeSeries.timeseriesPhenomenon[i].timeseries[j].firstValueDate;
                ts.lastTime = this.stationTimeSeries.timeseriesPhenomenon[i].timeseries[j].lastValueDate;
                ts.firstValue = this.stationTimeSeries.timeseriesPhenomenon[i].timeseries[j].firstValue;
                ts.lastValue = this.stationTimeSeries.timeseriesPhenomenon[i].timeseries[j].lastValue;
                ts.procedure = this.stationTimeSeries.timeseriesPhenomenon[i].timeseries[j].procedure;
                ts.uom = this.stationTimeSeries.timeseriesPhenomenon[i].timeseries[j].uom;
                ts.phenomenon.id = this.stationTimeSeries.timeseriesPhenomenon[i].timeseries[j].phenomenon.id;
                ts.phenomenon.label = this.stationTimeSeries.timeseriesPhenomenon[i].timeseries[j].phenomenon.label;

                const station = new Station();
                station.id = this.stationTimeSeries.stationId;
                station.name = this.stationTimeSeries.title;
                station.point = this.stationTimeSeries.point;
                ts.station = station;

                newTimeseries.push(ts);
            }
        }
    }

    let allTimeSeries;
    let timeseries = sessionStorage.getItem('diagramTimeseries');
    if (timeseries) {
      allTimeSeries =  JSON.parse(timeseries);
    }

    if (allTimeSeries == null || allTimeSeries.length === 0) {
        allTimeSeries = newTimeseries;
    } else {
        newTimeseries.forEach( newTS => {
            if (!allTimeSeries.some( (ts) =>  ts.timeseriesId === newTS.timeseriesId ) ) {
                allTimeSeries.push(newTS);
            }
        });
    }

    sessionStorage.setItem('diagramTimeseries', JSON.stringify(allTimeSeries));

    if (allTimeSeries.length > 0) {
        this.router.navigate(['/diagram']);
    } else {
        this.toastr.error('You have selected no timeseries.');
    }

  }

  change_selectAllTimeseries() {
    for (let i = 0; i < this.checkedTimeseries.length ; i++) {
        for (let j = 0; j < this.checkedTimeseries[i].length ; j++) {
            if (this.selectAllTimeseries === false) {
                this.checkedTimeseries[i][j] = false;
            } else {
                this.checkedTimeseries[i][j] = true;
            }
        }
    }
  }

  clickOnTab(timeseriesPhenomenon) {
    timeseriesPhenomenon.selected = !timeseriesPhenomenon.selected;
  }

}
