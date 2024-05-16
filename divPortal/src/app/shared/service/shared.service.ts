import { HttpPromise } from './../util/http-promise';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Response } from '../model/response-db';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  url = `${environment.serverUrl}`;
  httpPromise: HttpPromise;

  constructor(http: HttpClient) {
		this.httpPromise = new HttpPromise(http);
	}

  get(urlPart: string): Promise<Response> {
		return this.httpPromise.get(`${this.url}` + urlPart);
	}

  post(urlPart: string, body: any): Promise<Response> {
		return this.httpPromise.post(`${this.url}` + urlPart, body);
	}
}
