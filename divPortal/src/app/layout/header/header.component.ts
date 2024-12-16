import { OffsidebarService } from './../offsidebar/offsidebar.service';
import { SharedService } from './../../shared/service/shared.service';
import { HomeService } from './../../routes/home/home/home.service';
import { Component, OnInit, ViewChild, Injector } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
const screenfull = require('screenfull');

import { SettingsService } from '../../core/settings/settings.service';
import { MenuService } from '../../core/menu/menu.service';
import { ToastrService } from 'ngx-toastr';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

    menuItems: any[] = []; // for horizontal layout
 
    showOffsidebar: boolean;

    aboutHtml: string;
    termsAndCondHtml: string;

    pageHaveCollapsedSidebar: boolean;

    constructor(public menu: MenuService, 
                public settings: SettingsService, 
                public injector: Injector,
                private router: Router,
                private homeService: HomeService,
                private sharedService: SharedService,
                private offsidebarService: OffsidebarService,
                private toastrService: ToastrService) {
        this.menuItems = menu.getMenu().slice(0, 4);

        this.router.events.subscribe((val) => {
            if (val instanceof NavigationEnd) {
                if ( val.url.indexOf('diagram') > -1) {
                    this.settings.setLayoutSetting('offsidebarOpen', false);
                    this.showOffsidebar = false;
                } else if ( val.url.indexOf('home') > -1) {
                    this.showOffsidebar = true;
                }

                this.pageHaveCollapsedSidebar = val.url.indexOf('metadata-catalogue') == -1 ;
            }
        });
    }

    async ngOnInit() {
        const response1 = await this.sharedService.get('getHtml?partOfApp=about');
        this.aboutHtml = response1.entity.html;

        const response2 = await this.sharedService.get('getHtml?partOfApp=terms_and_condititions');
        this.termsAndCondHtml = response2.entity.html;

        this.toastrService.warning('You are using a development system of the emerging eLTER Research Infrastructure (RI). Please note that this service is still under construction and may not yet be fully functional.',
                                   'Warning', {
                                        disableTimeOut: true,
                                    });
    }

    toggleUserBlock(event) {
        event.preventDefault();
    }

    ngClassOffsidebar() {
        return (this.settings.getLayoutSetting('offsidebarOpen') ? 'icon-book-open' : 'icon-notebook') + ' text-primary-color-blue m-2';
    }

    toggleOffsidebar() {
        this.settings.toggleLayoutSetting('offsidebarOpen');
    }

    toggleCollapsedSideabar() {
        this.settings.toggleLayoutSetting('isCollapsed');

        this.homeService.repositionMap({
            action: 'repositionMap'
        });
    }

    isCollapsedText() {
        return this.settings.getLayoutSetting('isCollapsedText');
    }

    logoClick() {
        this.router.navigate(['home']);
    }

    show(clicked) {
        let state;
        if (clicked == 'about') {
            state = {
                action: 'showHTML',
                html: this.aboutHtml
            }
            this.showOInfoOnffsidebar(state);
        } else if (clicked == 'termsAndCondititions') {
            state = {
                action: 'showHTML',
                html: this.termsAndCondHtml
            }
            this.showOInfoOnffsidebar(state);
        } else if (clicked == 'map') {
            this.router.navigate(['/home']);
        } else if (clicked == 'metadata-catalogue') {
            this.router.navigate(['/metadata-catalogue']);
        }

       
    }

    async showOInfoOnffsidebar(state){
        await new Promise( resolve => setTimeout(resolve, 10) );
        this.offsidebarService.showHtml(state);
    }
}
