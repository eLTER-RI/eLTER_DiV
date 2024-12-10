import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DatasetDetailsListService {

  private paginationRefreshSubject = new BehaviorSubject<any>(undefined);
  paginationObservable = this.paginationRefreshSubject.asObservable();

  private pinClickedSubject = new BehaviorSubject<any>(undefined);
  pinClickedtbservable = this.pinClickedSubject.asObservable();
 
  constructor() { }
  
  paginationRefresh(page) {
    this.paginationRefreshSubject.next(page);
  }

  pinClicked(obj) {
    this.pinClickedSubject.next(obj);
  }

}
