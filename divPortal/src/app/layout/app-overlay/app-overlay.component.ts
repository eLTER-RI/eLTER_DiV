import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { AppBlockerService } from './app-blocker.service';
import { ToastBlinkService } from 'src/app/shared/service/toast-blink.service';

@Component({
    selector: 'app-overlay',
    templateUrl: './app-overlay.component.html',
    styleUrls: ['./app-overlay.component.scss'],
    standalone: false
})
export class AppOverlayComponent implements OnInit, OnDestroy {

  isBlocking = false;
  private subscription: Subscription;

  constructor(private appBlockerService: AppBlockerService, private toastBlinkService: ToastBlinkService) {}

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
    this.toastBlinkService.triggerBlink();
  }

}
