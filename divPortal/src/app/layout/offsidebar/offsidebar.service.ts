import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OffsidebarService {

  private layerCodeForSidebar = new BehaviorSubject<string>("");

  private ebvDetailsBehaviorSubject = new BehaviorSubject<any>(undefined);
  currEbvs = this.ebvDetailsBehaviorSubject.asObservable();

  private sitesDetailsBehaviorSubject = new BehaviorSubject<any>(undefined);
  currSites = this.sitesDetailsBehaviorSubject.asObservable();

  private siteOpenDetailsBehaviorSubject = new BehaviorSubject<any>(undefined);
  currOpenSite = this.siteOpenDetailsBehaviorSubject.asObservable();

  private showAllLayersBehaviorSubject = new BehaviorSubject<any>(undefined);
  showLayers = this.showAllLayersBehaviorSubject.asObservable();

  private addSelectedLayersBehaviorSubject = new BehaviorSubject<any>(undefined);
  selectedLayers = this.addSelectedLayersBehaviorSubject.asObservable();

  private htmlBehaviorSubject = new BehaviorSubject<any>(undefined);
  htmlObservable = this.htmlBehaviorSubject.asObservable();

  private closeAllSitesDetailsBehaviorSubject = new BehaviorSubject<any>(undefined);
  closeAllSiteDetailsObservable = this.closeAllSitesDetailsBehaviorSubject.asObservable();

  private closeAllDatasetDetailsBehaviorSubject = new Subject<any>();
  closeAllDatasetDetailsObservable = this.closeAllDatasetDetailsBehaviorSubject.asObservable();

  private datasetsBehaviorSubject = new BehaviorSubject<any>(undefined);
  currDatasets = this.datasetsBehaviorSubject.asObservable();

  private datasetOpenDetailsBehaviorSubject = new BehaviorSubject<any>(undefined);
  currOpenDataset = this.datasetOpenDetailsBehaviorSubject.asObservable();

  private clearSitesAndDatasetsBehaviorSubject = new BehaviorSubject<any>(null);
  clearSitesAndDatasetsObservable = this.clearSitesAndDatasetsBehaviorSubject.asObservable();

  private stationsDetailsBehaviorSubject = new BehaviorSubject<any>(undefined);
  currStations = this.stationsDetailsBehaviorSubject.asObservable();

  private closeAllStationDetailsBehaviorSubject = new BehaviorSubject<any>(undefined);
  closeAllStationDetailsObservable = this.closeAllStationDetailsBehaviorSubject.asObservable();

  private unselectDatastreamsBehaviorSubject = new BehaviorSubject<any>(undefined);
  unselectDatastreamsObservable = this.unselectDatastreamsBehaviorSubject.asObservable();

  private datasetsOrSitesLoadingBehaviorSubject = new BehaviorSubject<any>(undefined);
  datasetsOrSitesLoadingObservable = this.datasetsOrSitesLoadingBehaviorSubject.asObservable();
  
  private datasetLayersBehaviorSubject = new BehaviorSubject<any>(undefined);
  currDatasetLayers = this.datasetLayersBehaviorSubject.asObservable();

  constructor() { }

  setLayerCodeForSidebar(newValue: string) {
    this.layerCodeForSidebar.next(newValue);
  }

  getLayerCodeForSidebar(): string {
    return this.layerCodeForSidebar.getValue();
  }
  

  showEbvs(ebv) {
    this.ebvDetailsBehaviorSubject.next(ebv);
  }

  showSites(sites) {
    this.sitesDetailsBehaviorSubject.next(sites);
  }

  openSite(site) {
    this.siteOpenDetailsBehaviorSubject.next(site);
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

  closeAllSiteDetails() {
    this.closeAllSitesDetailsBehaviorSubject.next({});
  }

  showDatasets(datasets) {
    this.datasetsBehaviorSubject.next(datasets);
  }

  closeAllDatasetDetails() {
    this.closeAllDatasetDetailsBehaviorSubject.next({});
  }

  openDataset(dataset) {
    this.datasetOpenDetailsBehaviorSubject.next(dataset);
  }

  clearSitesAndDatasets() {
		this.clearSitesAndDatasetsBehaviorSubject.next({});
	}

  showStations(stations) {
    this.stationsDetailsBehaviorSubject.next(stations);
  }

  closeAllStationDetails() {
    this.closeAllStationDetailsBehaviorSubject.next({});
  }

  unselectDatastreams(datastreamIds) {
    this.unselectDatastreamsBehaviorSubject.next(datastreamIds);
  }

  datasetsOrSitesOffsidebarLoading(loading: any) {
    this.datasetsOrSitesLoadingBehaviorSubject.next(loading);
  }

  showDatasetLayers(datasetLayers) {
    this.datasetLayersBehaviorSubject.next(datasetLayers);
  }

}
