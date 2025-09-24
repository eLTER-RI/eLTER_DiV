import { Injectable } from '@angular/core';
import { TimeIODatastream } from '../model/timeio-datastream';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TimeIOService {

  private selectedDatastreams: TimeIODatastream[] = [];
  private selectedDatastreamsSubject = new BehaviorSubject<TimeIODatastream[]>([]);

  selectedDatastreams$ = this.selectedDatastreamsSubject.asObservable();

  addDatastream(datastream: TimeIODatastream) {
    if (!this.selectedDatastreams.find(ds => ds.id === datastream.id)) {
      this.selectedDatastreams.push(datastream);
      this.selectedDatastreamsSubject.next(this.selectedDatastreams);
    }
  }

  removeDatastream(datastream: TimeIODatastream) {
    this.selectedDatastreams = this.selectedDatastreams.filter(ds => ds.id !== datastream.id);
    this.selectedDatastreamsSubject.next(this.selectedDatastreams);
  }

  removeAllDatastreams() {
    this.selectedDatastreams = [];
    this.selectedDatastreamsSubject.next(this.selectedDatastreams);
  }

  getSelectedDatastreams(): TimeIODatastream[] {
    return this.selectedDatastreams;
  }

  isSelected(datastream: TimeIODatastream): boolean {
    return this.selectedDatastreams.some(ds => ds.id === datastream.id);
  }

}
