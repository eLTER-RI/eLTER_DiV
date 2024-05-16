import { Response } from '../../../shared/model/response-db';
import { HttpPromise } from '../../../shared/util/http-promise';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EbvDetailsService {

  url = `${environment.serverUrl}`;
  httpPromise: HttpPromise;

  constructor(http: HttpClient) {
		this.httpPromise = new HttpPromise(http);
	}

  getDetail(id): Promise<Response>  {
    return this.httpPromise.get(`${this.url}ebv/getDetail?id=` + id);
  }

}
