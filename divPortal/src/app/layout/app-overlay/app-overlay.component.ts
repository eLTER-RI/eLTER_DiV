import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { AppBlockerService } from './app-blocker.service';

@Component({
  selector: 'app-overlay',
  templateUrl: './app-overlay.component.html',
  styleUrls: ['./app-overlay.component.scss']
})
export class AppOverlayComponent implements OnInit, OnDestroy {

  isBlocking = false;
  private subscription: Subscription;

  constructor(private appBlockerService: AppBlockerService) {}

  ngOnInit() {
    this.subscription = this.appBlockerService.blocking.subscribe(
      blocking => this.isBlocking = blocking
    );
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  preventClick(event: MouseEvent) {
    event.preventDefault();
    event.stopPropagation();
  }

}
