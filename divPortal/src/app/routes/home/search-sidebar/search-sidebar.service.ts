import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SearchSidebarService {

  private layerWithCodeBehaviorSubject = new BehaviorSubject<any>(null);
  layerWithCode = this.layerWithCodeBehaviorSubject.asObservable();

  private datasetSearchBehaviorSubject = new BehaviorSubject<any>(null);
  datasetSearchObservable = this.datasetSearchBehaviorSubject.asObservable();

  private layerSearchBehaviorSubject = new BehaviorSubject<any>(null);
  layerSearchObservable = this.layerSearchBehaviorSubject.asObservable();

  constructor() { }

  turnOnLayerWithCode(code) {
		this.layerWithCodeBehaviorSubject.next(code);
	}

  searchDatasets(obj) {
		this.datasetSearchBehaviorSubject.next(obj);
	}

  searchLayers(obj) {
		this.layerSearchBehaviorSubject.next(obj);
	}

}
