import { SharedService } from './../shared/service/shared.service';
import { Component, OnInit, ElementRef, ViewChild, Renderer2 } from '@angular/core';
import { Html } from '../shared/model/html-db';
import { Settings } from 'http2';
import { SettingsService } from '../core/settings/settings.service';
import { Subscription } from 'rxjs';
import { HomeService } from '../routes/home/home/home.service';
import { BsModalService } from 'ngx-bootstrap/modal';
import { ToastBlinkService } from '../shared/service/toast-blink.service';

@Component({
    selector: 'app-layout',
    templateUrl: './layout.component.html',
    styleUrls: ['./layout.component.scss'],
    standalone: false
})
export class LayoutComponent implements OnInit {

    hoverFooter: boolean;

    footerHtml: Html;
    footerOnHoverHtml: Html;

    @ViewChild('offsidebarElement') offsidebarElement: ElementRef;

    isLoading = true;
    dialogOpen = false;

    loadingSubscription: Subscription;


    constructor(private sharedService: SharedService,
        private settings: SettingsService,
        private homeService: HomeService,
        private modalService: BsModalService,
        private toastBlinkService: ToastBlinkService) { 
    }

    clickOutsideOffsidebar() {
        if (this.dialogOpen) {
            return;
        }
        
        if (this.settings.getLayoutSetting('offsidebarOpen')) {
            this.settings.toggleLayoutSetting('offsidebarOpen');
        }
    }

    async ngOnInit() {
        const response = await this.sharedService.get('getHtml?partOfApp=footer');
        this.footerHtml = response.entity;

        const response1 = await this.sharedService.get('getHtml?partOfApp=footer_hover');
        this.footerOnHoverHtml = response1.entity;

        this.initSubscriptions();
    }

    initSubscriptions() {
        this.loadingSubscription = this.homeService.loadingGlobalObservable.subscribe( obj => {
            if (obj != null) {
                this.isLoading = obj;
            }
        });

        this.modalService.onShown.subscribe(() => {
            this.dialogOpen = true;
        });

        this.modalService.onHidden.subscribe(() => {
            this.dialogOpen = false;
        });
    }

    getStyleForFooter() {
        if (this.hoverFooter) {
            return {
                'height': this.footerOnHoverHtml?.height,
                'background-color': 'white'
            };
        } else  {
            return {
                height: this.footerHtml?.height,
                'background-color': 'white'
            };
        }
    }

    mouseoverFooter() {
        this.hoverFooter = true;
    }

    mouseoutFooter() {
        this.hoverFooter = false;
    }

    preventClick(event: MouseEvent) {
        event.preventDefault();
        event.stopPropagation();
        this.toastBlinkService.triggerBlink();
    }

}
