import { HomeService } from './../../routes/home/home/home.service';
import { EbvDB } from './../../shared/model/ebv-db';
import { EbvDetail } from './../../shared/model/ebvdetail';
import { OffsidebarService } from './offsidebar.service';
import { Component, OnInit, OnDestroy, ElementRef } from '@angular/core';

import { SettingsService } from '../../core/settings/settings.service';
import { ThemesService } from '../../core/themes/themes.service';
import { TranslatorService } from '../../core/translator/translator.service';
import { Subscription } from 'rxjs';
import { DomSanitizer } from '@angular/platform-browser';
import { delay } from 'lodash';

@Component({
    selector: 'app-offsidebar',
    templateUrl: './offsidebar.component.html',
    styleUrls: ['./offsidebar.component.scss']
})
export class OffsidebarComponent implements OnInit, OnDestroy {

    currentTheme: any;

    siteId?: number;
    siteSubscription: Subscription;

    ebvs?: EbvDB[];
    ebvSubscription: Subscription;

    stationId?: number;
    stationSubscription: Subscription;

    html: any;
    htmlSubscription: Subscription;

    showHideLayers?: any[];
    showHideLayersSubscription: Subscription;


    constructor(public themes: ThemesService, 
                public offsidebarService: OffsidebarService,
                private settings: SettingsService,
                public sanitizer: DomSanitizer,
                private homeService: HomeService) {
    }

    ngOnInit() { //ovde dodati da se skloni polygon
        this.ebvSubscription = this.offsidebarService.currEbvs.subscribe( ebv => {
            if (ebv && ebv.action == 'showEBVs') {
                this.showEbv(ebv.ebvs);
            }
        });
        this.siteSubscription = this.offsidebarService.currSite.subscribe( site => {
            if (site && site.action == 'showSite') {
                this.showSite(site.site);
            }
        });
        this.stationSubscription = this.offsidebarService.currStation.subscribe( station => {
            if (station && station.action == 'showStation') {
                this.showStation(station.station);
            }
        });
        this.htmlSubscription = this.offsidebarService.htmlObservable.subscribe( info => {
            if (info && info.action == 'showHTML') {
                this.showHtml(info.html);
            }
        });
        this.showHideLayersSubscription = this.offsidebarService.showLayers.subscribe( state => {
            if (state && state.action == 'showHideMoreLayers') {
                this.showHideAllLayers(state.layers);
            }
        });

    }

    async showHideAllLayers(layers) {
        this.showHideLayers = layers;

        await new Promise( resolve => setTimeout(resolve, 10) );
        this.openOffsidebar();

        delete this.ebvs;
        delete this.siteId;
        delete this.stationId;
        delete this.html;

        this.homeService.actionChanged({
            action: 'removeSitePolygon'
        });
    }

    showEbv(ebv) {
        this.ebvs = ebv;

        delete this.showHideLayers;
        delete this.siteId;
        delete this.stationId;
        delete this.html;

        this.homeService.actionChanged({
            action: 'removeSitePolygon'
        });
    }

    showSite(site) {
        this.siteId = site;

        delete this.showHideLayers;
        delete this.ebvs;
        delete this.stationId;
        delete this.html;
    }

    showStation(station) {
        this.stationId = station;

        delete this.showHideLayers;
        delete this.siteId;
        delete this.ebvs;
        delete this.html;
    }

    openOffsidebar(){
        if (!this.settings.getLayoutSetting('offsidebarOpen')) {
            this.settings.toggleLayoutSetting('offsidebarOpen');
        }
    }

    showHtml(info) {
        this.html = info;
        
        delete this.showHideLayers;
        this.openOffsidebar();

        delete this.ebvs;
        delete this.stationId;
        delete this.siteId;
    }

    setTheme() {
        this.themes.setTheme(this.currentTheme);
    }

   

    ngOnDestroy() {
        this.ebvSubscription.unsubscribe();
        this.siteSubscription.unsubscribe();
        this.stationSubscription.unsubscribe();
        this.htmlSubscription.unsubscribe();
    }
}
