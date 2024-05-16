import { EbvDB } from './../../shared/model/ebv-db';
import { EbvDetail } from './../../shared/model/ebvdetail';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OffsidebarService {

  private ebvDetailsBehaviorSubject = new BehaviorSubject<any>(undefined);
  currEbvs = this.ebvDetailsBehaviorSubject.asObservable();

  private siteDetailsBehaviorSubject = new BehaviorSubject<any>(undefined);
  currSite = this.siteDetailsBehaviorSubject.asObservable();

  private stationDetailsBehaviorSubject = new BehaviorSubject<any>(undefined);
  currStation = this.stationDetailsBehaviorSubject.asObservable();

  private showAllLayersBehaviorSubject = new BehaviorSubject<any>(undefined);
  showLayers = this.showAllLayersBehaviorSubject.asObservable();

  private addSelectedLayersBehaviorSubject = new BehaviorSubject<any>(undefined);
  selectedLayers = this.addSelectedLayersBehaviorSubject.asObservable();

  private htmlBehaviorSubject = new BehaviorSubject<any>(undefined);
  htmlObservable = this.htmlBehaviorSubject.asObservable();

  constructor() { }

  showEbvs(ebv) {
    this.ebvDetailsBehaviorSubject.next(ebv);
  }

  showSite(site) {
    this.siteDetailsBehaviorSubject.next(site);
  }

  showStation(station) {
    this.stationDetailsBehaviorSubject.next(station);
  }

  showAllLayers(state) {
    this.showAllLayersBehaviorSubject.next(state);
  }

  addSelectedLayers(state) {
    this.addSelectedLayersBehaviorSubject.next(state);
  }

  showHtml(state) {
    this.htmlBehaviorSubject.next(state);
  }
}
