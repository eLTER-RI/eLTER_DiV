import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DatasetDetailsListService {

  private paginationRefreshSubject = new BehaviorSubject<any>(undefined);
  paginationObservable = this.paginationRefreshSubject.asObservable();
 
  constructor() { }
  
  paginationRefresh(page) {
    this.paginationRefreshSubject.next(page);
  }

}
