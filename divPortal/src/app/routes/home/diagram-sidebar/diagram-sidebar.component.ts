import { Router, NavigationEnd } from '@angular/router';
import { DiagramService } from './../diagram/diagram.service';
import { Component, OnInit } from '@angular/core';
import { TimeIOService } from 'src/app/shared/service/time-io.service';
import { TimeIODatastream } from 'src/app/shared/model/timeio-datastream';
import { OffsidebarService } from 'src/app/layout/offsidebar/offsidebar.service';

@Component({
    selector: 'app-diagram-sidebar',
    templateUrl: './diagram-sidebar.component.html',
    styleUrls: ['./diagram-sidebar.component.scss'],
    standalone: false
})
export class DiagramSidebarComponent implements OnInit {

  datastreams: TimeIODatastream[];
  showValues: boolean[] = [];

  constructor(private diagramService: DiagramService,
              private router: Router,
              private timeioService: TimeIOService,
              private offsidebarService: OffsidebarService) {
    this.router.events.subscribe((val) => {
        if (val instanceof NavigationEnd && val.url.indexOf('diagram') > -1) {
          this.loadTimeseries();
        }
    });
  }

  ngOnInit(): void {
    this.loadTimeseries();

    this.initSubscriptions();
  }

  initSubscriptions() {
    this.timeioService.selectedDatastreams$.subscribe(selectedDs => {
      selectedDs.forEach(ds => {
        if (!this.datastreams.find(d => d.id === ds.id)) {
          this.datastreams.push(ds);
        }
      });
    });
  }

  loadTimeseries() {
    this.datastreams = this.timeioService.getSelectedDatastreams();
  }

  getFilteredDatastreams(): TimeIODatastream[] {
    return this.datastreams.filter(ds => ds.showed);
  }
  
  getStyleForDatastream(datastream: TimeIODatastream) {
    if (datastream.color != null) {
        const styles = {
            'border-style': 'solid',
            'border-color': datastream.color
        }
        return styles;
    }
    const styles = {
        'border': '1px solid',
        'border-color': '#ED9632'
    }
    return styles;
  }

  btn_selectOrDeselectDatastreams(datastream: TimeIODatastream, i: number) {
    const state = {
      obj: {
        datastreams: datastream,
        index: i
      },
      action: 'selectOrDeselectDatastreams'
    }
    this.diagramService.getDiagramBehaviorEvent().next(state);
  }


  btn_removeDatastreams(dsToRemove: TimeIODatastream, index, event) {
    event?.stopPropagation();

    dsToRemove.showed = false;
    dsToRemove.checked = false;
    dsToRemove.color = null;
    this.datastreams.splice(index, 1);

    this.timeioService.removeDatastream(dsToRemove);

    this.offsidebarService.unselectDatastreams([dsToRemove.id]);

    const state = {
      action: 'removeOneDatastream',
      obj: {
        dsToRemove: dsToRemove
      }
    }

    this.diagramService.getDiagramBehaviorEvent().next(state);
  }

  btn_clearAll() {
    let datastreamIds = this.datastreams.map(ds => ds.id);
    this.datastreams.forEach(ds => {ds.showed = false; ds.checked = false; ds.color = null;}); // Hide all datastreams

    this.timeioService.removeAllDatastreams();
    this.offsidebarService.unselectDatastreams(datastreamIds);

    this.datastreams = [];
    
    this.router.navigate(['/home']);
  }

}
