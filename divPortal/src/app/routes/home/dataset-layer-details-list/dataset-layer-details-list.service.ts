import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DatasetLayerDetailsListService {

  private checkDatasetLayerBehaviorSubject = new Subject<any>();
  checkDatasetLayerEvent = this.checkDatasetLayerBehaviorSubject.asObservable();

  constructor() { }

  checkDatasetLayer(layer: any) {
    this.checkDatasetLayerBehaviorSubject.next(layer);
  }

}
