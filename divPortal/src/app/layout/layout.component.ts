import { SharedService } from './../shared/service/shared.service';
import { Component, OnInit, ElementRef, ViewChild, Renderer2 } from '@angular/core';
import { Html } from '../shared/model/html-db';
import { Settings } from 'http2';
import { SettingsService } from '../core/settings/settings.service';

@Component({
    selector: 'app-layout',
    templateUrl: './layout.component.html',
    styleUrls: ['./layout.component.scss']
})
export class LayoutComponent implements OnInit {

    hoverFooter: boolean;

    footerHtml: Html;
    footerOnHoverHtml: Html;

    @ViewChild('offsidebarElement') offsidebarElement: ElementRef;


    constructor(private sharedService: SharedService,
        private settings: SettingsService) { 
    }

    clickOutsideOffsidebar() {
        if (this.settings.getLayoutSetting('offsidebarOpen')) {
            this.settings.toggleLayoutSetting('offsidebarOpen');
        }
    }

    async ngOnInit() {
        const response = await this.sharedService.get('getHtml?partOfApp=footer');
        this.footerHtml = response.entity;

        const response1 = await this.sharedService.get('getHtml?partOfApp=footer_hover');
        this.footerOnHoverHtml = response1.entity;
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

}
