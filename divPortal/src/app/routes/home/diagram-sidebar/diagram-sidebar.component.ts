import { Router, NavigationEnd } from '@angular/router';
import { DiagramService } from './../diagram/diagram.service';
import { DiagramTimeseries } from './../../../shared/model/diagram-timeseries';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-diagram-sidebar',
  templateUrl: './diagram-sidebar.component.html',
  styleUrls: ['./diagram-sidebar.component.scss']
})
export class DiagramSidebarComponent implements OnInit {

  timeseries: DiagramTimeseries[];
  showValues: boolean[] = [];

  scrollbarOptionsTimeseries = {  theme: 'light-thick', scrollButtons: { enable: true },  setHeight: '95vh'};

  private colors: string[] = ['#0879C0', '#96CE58', '#003972', '#BADFFD',
  '#0879C0', '#7b00ff', '#42f5e3', '#917af5',
  '#f7528c', '#f27900', '#eef200', '#f552f7'];


  constructor(private diagramService: DiagramService,
              private router: Router) {
    this.router.events.subscribe((val) => {
        if (val instanceof NavigationEnd && val.url.indexOf('diagram') > -1) {
          this.loadTimeseries();
        }
    });
  }

  ngOnInit(): void {
    this.loadTimeseries();
  }

  loadTimeseries() {
    const timeseries = sessionStorage.getItem('diagramTimeseries');

    if (timeseries != null && timeseries != undefined && timeseries != 'undefined') {
      this.timeseries = JSON.parse(timeseries);
    }
  }

  btn_showStationOnMap(ts) {

  }

  getStyleForTimeseries(timeseries: DiagramTimeseries) {
    if (timeseries.color != null) {
        const styles = {
            'border-style': 'solid',
            'border-color': timeseries.color
        }
        return styles;
    }
    const styles = {
        'border': '1px solid',
        'border-color': '#ED9632'
    }
    return styles;
  }

  btn_selectOrDeselectTimeseries(timeseries: DiagramTimeseries, i: number) {
    const state = {
      obj: {
        timeseries: timeseries,
        index: i
      },
      action: 'selectOrDeselectTimeseries'
    }

    this.diagramService.diagramChanged(state);
  }


  btn_removeTimeseries(tsToRemove: DiagramTimeseries, index) {
    this.timeseries.splice(index, 1);

    sessionStorage.setItem('diagramTimeseries', JSON.stringify(this.timeseries));

    const state = {
      action: 'removeOneTimeserie',
      obj: {
        tsToRemove: tsToRemove,
        index: index
      }
    }

    this.diagramService.diagramChanged(state);
  }

  btn_clearAll() {
    this.timeseries = [];

    sessionStorage.setItem('diagramTimeseries', JSON.stringify(this.timeseries));
    this.router.navigate(['/home']);
  }

}
