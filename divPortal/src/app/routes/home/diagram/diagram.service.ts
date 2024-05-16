import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { MeasurementRequest } from 'src/app/shared/model/measurement-ib';
import { MeasurementResponse } from 'src/app/shared/model/measurements-response-db';


@Injectable({
  providedIn: 'root'
})
export class DiagramService {

  private httpHeaders = new HttpHeaders({ 'Content-Type': 'application/json' });
  
  private diagramBehaviorSubject = new BehaviorSubject<any>({});
  currDiagram = this.diagramBehaviorSubject.asObservable();

  constructor(private http: HttpClient) { }

  diagramChanged(state: any) {
    this.diagramBehaviorSubject.next(state);
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
