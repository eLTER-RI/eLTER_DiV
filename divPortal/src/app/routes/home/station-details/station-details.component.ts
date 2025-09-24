import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ToastrService } from 'ngx-toastr';
import { Subscription } from 'rxjs';
import { OffsidebarService } from 'src/app/layout/offsidebar/offsidebar.service';
import { TimeIODatastream } from 'src/app/shared/model/timeio-datastream';
import { TimeIOLocation } from 'src/app/shared/model/timeio-location';
import { TimeIOThing } from 'src/app/shared/model/timeio-thing';
import { SharedService } from 'src/app/shared/service/shared.service';
import { TimeIOService } from 'src/app/shared/service/time-io.service';
import { DiagramService } from '../diagram/diagram.service';

@Component({
  selector: 'app-station-details',
  templateUrl: './station-details.component.html',
  styleUrls: ['./station-details.component.scss']
})
export class StationDetailsComponent implements OnInit, OnChanges {

  @Input() stationId: number;
  @Input() open: boolean;

  stationDetails: TimeIOLocation;

  stationOpenDetailsSubscription: Subscription;
  closeAllStationDetailsSubscription: Subscription;
  unselectDatastreamsSubscription: Subscription;

  scrollbarOptions = {  theme: 'dark-thick', scrollButtons: { enable: true },  setHeight: '80vh'};

  constructor(private offsidebarService: OffsidebarService,
              private sharedService: SharedService,
              private toastrService: ToastrService,
              private translateService: TranslateService,
              private timeioService: TimeIOService,
              private router: Router,
              private diagramService: DiagramService
  ) { }

  ngOnChanges(changes: SimpleChanges): void {
      this.loadStations();
  }

  ngOnInit(): void {
    this.initSubscriptions();
  }

  initSubscriptions() {
    this.closeAllStationDetailsSubscription = this.offsidebarService.closeAllStationDetailsObservable.subscribe( obj => {
      if (this.stationDetails) {
        this.stationDetails.open = false;
      }
    });

    this.unselectDatastreamsSubscription = this.offsidebarService.unselectDatastreamsObservable.subscribe( datastreamIds => {
      if (datastreamIds && this.stationDetails?.things?.length > 0) {
        this.stationDetails.things.forEach(thing => {
          if (thing.fkDatastreams) {
            thing.fkDatastreams.forEach(datastream => {
              if (datastreamIds.includes(datastream.id)) {
                datastream.checked = false;
              }
            });
          }
        });
      }
    });
  }

  async loadStations() {
    const response = await this.sharedService.get('timeio/location/getLocationDetails?id=' + this.stationId);
    if (response.status != 200) {
      this.toastrService.error(this.translateService.instant('exception.' + response.entity.status + ''), "Error"); 
    } else {
      this.stationDetails = response.entity;
      this.stationDetails.open = this.open;
    }
  }

  checkForSelectedDatastreams() {
    if (this.stationDetails?.things?.length > 0) {
      this.stationDetails.things.forEach(thing => {
        if (thing.fkDatastreams) {
          thing.fkDatastreams.forEach(datastream => {
            if (this.timeioService.isSelected(datastream)) {
              datastream.checked = true;
            }
          });
        }
      });
    }
  }

  openCloseStationDetail() {
    this.stationDetails.open = !this.stationDetails.open;
  }

  async openCloseThing(thing: TimeIOThing) {
    thing.open = !thing.open;
    
    if (thing.open && !thing.fkDatastreams) {
      await this.loadThingDetails(thing);
    }
  }

  async loadThingDetails(thing: TimeIOThing) {
    const response = await this.sharedService.get('timeio/thing/getThingDetails?id=' + thing.id);
    thing.fkDatastreams = response.entity.fkDatastreams;
  }

  checkDatastream(datastream: TimeIODatastream, thing: TimeIOThing) {
    datastream.checked = !datastream.checked;

    if (datastream.checked) {
      datastream.fkThing = thing;
      this.timeioService.addDatastream(datastream);
    }
  }

  showTimeIOObservations() {
    const currentUrl = this.router.url;
    if (currentUrl === '/diagram') { // refresh
      this.diagramService.getRefreshEvent().next({});
    } else { // navigate
      this.router.navigate(['/diagram']);
    }
  }

}
