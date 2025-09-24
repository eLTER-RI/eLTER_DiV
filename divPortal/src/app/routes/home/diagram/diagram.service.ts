import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { environment } from 'src/environments/environment';
import { MeasurementRequest } from 'src/app/shared/model/measurement-ib';
import { MeasurementResponse } from 'src/app/shared/model/measurements-response-db';


@Injectable({
  providedIn: 'root'
})
export class DiagramService {

  private httpHeaders = new HttpHeaders({ 'Content-Type': 'application/json' });
  
  private diagramBehaviorEvent: Subject<any> = new Subject();
  private refreshEvent: Subject<any> = new Subject();

  constructor(private http: HttpClient) { }

  getDiagramBehaviorEvent() {
    return this.diagramBehaviorEvent;
  }

  getRefreshEvent() {
    return this.refreshEvent;
  }


  getMeasurements(measurementRequests: MeasurementRequest[]): Observable<MeasurementResponse> {
    const url = environment.serverUrl + 'sos/getMeasurements';
    return new Observable((o: any) => {
        this.http.post(url, measurementRequests, {headers : this.httpHeaders}).subscribe(
            (data: any) => {
                o.next(data);
                return o.complete();
            });
    });
  }

}
