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

  constructor() { }

  turnOnLayerWithCode(code) {
		this.layerWithCodeBehaviorSubject.next(code);
	}

  searchDatasets(obj) {
		this.datasetSearchBehaviorSubject.next(obj);
	}

}
