import { Response } from '../../../shared/model/response-db';
import { HttpPromise } from '../../../shared/util/http-promise';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LayersSidebarService {

  url = `${environment.serverUrl}`;
  httpPromise: HttpPromise;

  constructor(http: HttpClient) {
		this.httpPromise = new HttpPromise(http);
	}

  getLayers(layers: string[], code: string): Promise<Response> {
    var params = layers.join();
    if (code != null && code != '') {
      params += '&code=' + code;
    }
		return this.httpPromise.get(`${this.url}layer/getAllLayers?layertype=` + params);
	}

  getLayersByIds(ids: number[]): Promise<Response> {
    const params = ids.join();
		return this.httpPromise.get(`${this.url}layer/getAllLayers?ids=` + params);
	}
}
