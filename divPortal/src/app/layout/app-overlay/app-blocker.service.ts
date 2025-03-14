import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppBlockerService {

  private blockingActive = new BehaviorSubject<boolean>(false);
  blocking = this.blockingActive.asObservable();

  blockApp() {
    this.blockingActive.next(true);
  }

  unblockApp() {
    this.blockingActive.next(false);
  }

}
