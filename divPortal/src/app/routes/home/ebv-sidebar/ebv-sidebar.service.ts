import { Response } from '../../../shared/model/response-db';
import { HttpPromise } from '../../../shared/util/http-promise';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EbvSidebarService {

  url = `${environment.serverUrl}`;
  httpPromise: HttpPromise;

  constructor(http: HttpClient) {
		this.httpPromise = new HttpPromise(http);
	}

  getCodebook(layers: string[]): Promise<Response> {
    const params = layers.join();
		return this.httpPromise.get(`${this.url}ebv/get?codebook=` + params);
	}

  filter(filterDTO): Promise<Response> {
		return this.httpPromise.post(`${this.url}ebv/filter`, filterDTO);
	}

  getEbvName(ebvClassId): Promise<Response> {
		return this.httpPromise.get(`${this.url}ebv/getEbvName?ebvClassId=` + ebvClassId);
	}
}
