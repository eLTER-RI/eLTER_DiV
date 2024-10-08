import { BehaviorSubject } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class HomeService {

	private baseMapBehaviorSubject = new BehaviorSubject<any>(null);
  	currBaseMap = this.baseMapBehaviorSubject.asObservable();

	private layersBehaviorSubject = new BehaviorSubject<any>(null);
  	layer = this.layersBehaviorSubject.asObservable();

	private markedLayerBehaviorSubject = new BehaviorSubject<any>(null);
	markedLayer = this.markedLayerBehaviorSubject.asObservable();

	private actionBehaviorSubject = new BehaviorSubject<any>(null);
	action = this.actionBehaviorSubject.asObservable();

	private repositionMapBehaviorSubject = new BehaviorSubject<any>(null);
	repositionMapObserbable = this.repositionMapBehaviorSubject.asObservable();

	private hideAllLayersBehaviorSubject = new BehaviorSubject<any>(undefined);
	hideAllLayersObservable = this.hideAllLayersBehaviorSubject.asObservable();

	private markSiteOnMapBehaviorSubject = new BehaviorSubject<any>(undefined);
	markSiteOnMapObservable = this.markSiteOnMapBehaviorSubject.asObservable();

  	constructor() { }

	changeBaseMap(basemap) {
		this.baseMapBehaviorSubject.next(basemap);
	}

	turnOnOffLayer(layer) {
		this.layersBehaviorSubject.next(layer);
	}

	markOneLayer(layer) {
		this.markedLayerBehaviorSubject.next(layer);
	}

	actionChanged(action) {
		this.actionBehaviorSubject.next(action);
	}

	repositionMap(action) {
		this.repositionMapBehaviorSubject.next(action);
	}

	hideAllLayers() {
		this.hideAllLayersBehaviorSubject.next({});
	}

	markSiteOnMap(siteId: number, markOnMap: boolean) {
		this.markSiteOnMapBehaviorSubject.next({siteId: siteId, markOnMap: markOnMap});
	}

}
