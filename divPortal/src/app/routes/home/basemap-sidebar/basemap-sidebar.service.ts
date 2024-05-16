import { Response } from '../../../shared/model/response-db';
import { HttpPromise } from '../../../shared/util/http-promise';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BaseMapSideBarService {

  url = `${environment.serverUrl}`;
  httpPromise: HttpPromise;

  constructor(http: HttpClient) {
		this.httpPromise = new HttpPromise(http);
	}

  getBaselayers(baselayers: string[]): Promise<Response> {
    const params = baselayers.join();
		return this.httpPromise.get(`${this.url}layer/getAllLayers?layertype=` + params);
	}
}
