import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root',
})
export class ToastBlinkService {
  public triggerBlink(): void {
    const toastElement = document.querySelector('.blink-toast');

    if (toastElement) {
      toastElement.classList.add('blink');
      toastElement.classList.add('click-suggestion');
      setTimeout(() => {
        toastElement.classList.remove('blink');
        toastElement.classList.remove('click-suggestion');
      }, 1000);

    }
  }
}
