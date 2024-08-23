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
import { Site } from 'src/app/shared/model/site-db';

@Component({
    selector: 'app-offsidebar',
    templateUrl: './offsidebar.component.html',
    styleUrls: ['./offsidebar.component.scss']
})
export class OffsidebarComponent implements OnInit, OnDestroy {

    currentTheme: any;

    siteIds?: number[];
    sitesSubscription: Subscription;
    showSitesEvenIfEmpty: boolean;

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
        this.sitesSubscription = this.offsidebarService.currSites.subscribe( sites => {
            if (sites && sites.action == 'showSites') {
                this.showSites(sites.sites);
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
        delete this.siteIds;
        delete this.stationId;
        delete this.html;

        this.homeService.actionChanged({
            action: 'removeSitePolygon'
        });
    }

    showEbv(ebv) {
        this.ebvs = ebv;

        delete this.showHideLayers;
        delete this.siteIds;
        delete this.stationId;
        delete this.html;
        this.showSitesEvenIfEmpty = null;

        this.homeService.actionChanged({
            action: 'removeSitePolygon'
        });
    }

    showSites(siteIds) {
        this.siteIds = siteIds;
        this.showSitesEvenIfEmpty = true;
        if (this.siteIds && this.siteIds.length > 1) {
            this.offsidebarService.closeAllSiteDetails();
        }

        delete this.showHideLayers;
        delete this.ebvs;
        delete this.stationId;
        delete this.html;
    }

    showStation(station) {
        this.stationId = station;

        delete this.showHideLayers;
        delete this.siteIds;
        delete this.ebvs;
        delete this.html;
        this.showSitesEvenIfEmpty = null;
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
        delete this.siteIds;
        this.showSitesEvenIfEmpty = null;
    }

    setTheme() {
        this.themes.setTheme(this.currentTheme);
    }

   

    ngOnDestroy() {
        this.ebvSubscription.unsubscribe();
        this.sitesSubscription.unsubscribe();
        this.stationSubscription.unsubscribe();
        this.htmlSubscription.unsubscribe();
    }
}
