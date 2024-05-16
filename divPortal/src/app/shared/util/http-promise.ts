import { httpCommonHeaders } from './../consts/http-headers.constant';
import { HttpClient } from '@angular/common/http';
export class HttpPromise {
	
	constructor(public http: HttpClient) {}

	get<T>(url: string): Promise<T> {
		return this.http.get<T>(url, httpCommonHeaders).toPromise<T>();
	}

	post<T>(url: string, data: any): Promise<T> {
		return this.http.post<T>(url, data, httpCommonHeaders).toPromise<T>();
	}

	post1<T>(url: string, data: T): Promise<T[]> {
		return this.http.post<T>(url, data, httpCommonHeaders).toPromise<any>();
	}

}
