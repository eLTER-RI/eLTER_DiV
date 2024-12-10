import { HomeService } from './../../routes/home/home/home.service';
import { EbvDB } from './../../shared/model/ebv-db';
import { OffsidebarService } from './offsidebar.service';
import { Component, OnInit, OnDestroy } from '@angular/core';

import { SettingsService } from '../../core/settings/settings.service';
import { ThemesService } from '../../core/themes/themes.service';
import { Subscription } from 'rxjs';
import { DomSanitizer } from '@angular/platform-browser';
import { SearchSidebarService } from 'src/app/routes/home/search-sidebar/search-sidebar.service';
import { Dataset } from 'src/app/shared/model/dataset';
import { Page } from 'src/app/shared/model/Page';

@Component({
    selector: 'app-offsidebar',
    templateUrl: './offsidebar.component.html',
    styleUrls: ['./offsidebar.component.scss']
})
export class OffsidebarComponent implements OnInit, OnDestroy {

    visibleTab: string;

    currentTheme: any;

    siteIds?: number[];
    sitesSubscription: Subscription;

    datasets: Dataset[];
    datasetSubscription: Subscription;
    datasetPage: Page;

    ebvs?: EbvDB[];
    ebvSubscription: Subscription;

    stationId?: number;
    stationSubscription: Subscription;

    html: any;
    htmlSubscription: Subscription;

    showHideLayers?: any[];
    showHideLayersSubscription: Subscription;

    tabNameSubscription: Subscription;

    constructor(public themes: ThemesService, 
                public offsidebarService: OffsidebarService,
                public searchSidebarService: SearchSidebarService,
                private settings: SettingsService,
                public sanitizer: DomSanitizer,
                private homeService: HomeService) {
    }

    ngOnInit() { //ovde dodati da se skloni polygon
        this.visibleTab = 'sites';

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
        this.datasetSubscription = this.offsidebarService.currDatasets.subscribe( datasets => {
            if (datasets && datasets.action == 'showDatasets') {
                this.datasetPage = datasets.page;
                this.showDatasets(datasets.datasets);
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

        this.visibleTab = null;
        
        delete this.ebvs;
        delete this.siteIds;
        delete this.datasets;
        delete this.stationId;
        delete this.html;

        this.homeService.actionChanged({
            action: 'removeSitePolygon'
        });
    }

    showEbv(ebv) {
        this.ebvs = ebv;

        this.visibleTab = null;

        delete this.showHideLayers;
        delete this.siteIds;
        delete this.datasets;
        delete this.stationId;
        delete this.html;

        this.homeService.actionChanged({
            action: 'removeSitePolygon'
        });
    }

    showSites(siteIds) {
        this.siteIds = siteIds;
        if (this.siteIds && this.siteIds.length > 1) {
            this.offsidebarService.closeAllSiteDetails();
        }

        this.visibleTab = 'sites';

        delete this.showHideLayers;
        delete this.ebvs;
        delete this.stationId;
        delete this.html;
    }

    showDatasets(datasets) {
        this.datasets = datasets;
        if (this.datasets && this.datasets.length > 1) {
            this.offsidebarService.closeAllDatasetDetails();
        }

        delete this.showHideLayers;
        delete this.ebvs;
        delete this.stationId;
        delete this.html;
    }

    showStation(station) {
        this.stationId = station;

        this.visibleTab = null;

        delete this.showHideLayers;
        delete this.siteIds;
        delete this.datasets;
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
        delete this.siteIds;
    }

    setTheme() {
        this.themes.setTheme(this.currentTheme);
    }

    tabClicked(tabName: string) {
        let sameTabClicked = tabName == this.visibleTab;
        this.visibleTab = tabName;
        if (!sameTabClicked) {
            this.searchSidebarService.turnOnLayerWithCode({code: this.visibleTab});
        }
    }

    ngOnDestroy() {
        this.ebvSubscription.unsubscribe();
        this.sitesSubscription.unsubscribe();
        this.stationSubscription.unsubscribe();
        this.htmlSubscription.unsubscribe();
    }
}
