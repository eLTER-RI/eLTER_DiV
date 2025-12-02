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
import { DatasetLayer } from 'src/app/shared/model/dataset-layer';

@Component({
    selector: 'app-offsidebar',
    templateUrl: './offsidebar.component.html',
    styleUrls: ['./offsidebar.component.scss'],
    standalone: false
})
export class OffsidebarComponent implements OnInit, OnDestroy {

    visibleTab: string;

    currentTheme: any;

    siteIds?: number[];
    sitesSubscription: Subscription;

    datasets: Dataset[];
    datasetSubscription: Subscription;
    datasetPage: Page;

    datasetLayers: DatasetLayer[];
    datasetLayerSubscription: Subscription;

    stationIds?: number[];
    stationSubscription: Subscription;

    ebvs?: EbvDB[];
    ebvSubscription: Subscription;

    html: any;
    htmlSubscription: Subscription;

    showHideLayers?: any[];
    showHideLayersSubscription: Subscription;

    tabNameSubscription: Subscription;

    datasetsLoadingSubscription: Subscription;

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
        this.datasetLayerSubscription = this.offsidebarService.currDatasetLayers.subscribe( datasetLayers => {
            if (datasetLayers && datasetLayers.action == 'showDatasetLayers') {
                this.showDatasetLayers(datasetLayers.datasetLayers);
            }
        });
        this.stationSubscription = this.offsidebarService.currStations.subscribe( stations => {
            if (stations && stations.action == 'showStations') {
                this.showStations(stations.stations);
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
        this.datasetsLoadingSubscription = this.offsidebarService.datasetsOrSitesLoadingObservable.subscribe(loading => {
            if (loading && loading.isLoading) {
                if (loading.type === 'dataset') {
                    this.datasetPage = null;
                } else if (loading.type === 'site') {
                    this.siteIds = null;
                }
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
        delete this.html;

        this.homeService.actionChanged({
            action: 'removeSitePolygon'
        });
    }

    showSites(siteIds: number[]) {
        this.siteIds = siteIds;
        if (this.siteIds && this.siteIds.length > 1) {
            this.offsidebarService.closeAllSiteDetails();
        }

        this.visibleTab = 'sites';

        delete this.showHideLayers;
        delete this.ebvs;
        delete this.html;
    }

    showDatasets(datasets) {
        this.datasets = datasets;
        if (this.datasets && this.datasets.length > 1) {
            this.offsidebarService.closeAllDatasetDetails();
        }

        delete this.showHideLayers;
        delete this.ebvs;
        delete this.html;
    }

    showDatasetLayers(datasetLayers) {
        this.datasetLayers = datasetLayers;
        if (this.datasetLayers && this.datasetLayers.length > 1) {
            // this.offsidebarService.closeAllDatasetLayerDetails(); TODO datasetLayer
        }

        delete this.showHideLayers;
        delete this.ebvs;
        delete this.html;
    }

    showStations(stations) {
        this.stationIds = stations;

        if (this.stationIds && this.stationIds.length > 1) {
            this.offsidebarService.closeAllStationDetails();
        }

        this.visibleTab = 'stations';

        delete this.showHideLayers;
        delete this.ebvs;
        delete this.html;
    }

    openOffsidebar(){
        if (!this.settings.getLayoutSetting('offsidebarOpen')) {
            this.settings.toggleLayoutSetting('offsidebarOpen');
        }
    }

    showHtml(info) {
        this.visibleTab = null;

        this.html = info;
        
        delete this.showHideLayers;
        this.openOffsidebar();

        delete this.ebvs;
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

    closeOffsidebar() {
        if (this.settings.getLayoutSetting('offsidebarOpen')) {
            this.settings.toggleLayoutSetting('offsidebarOpen');
        }
    }

    ngOnDestroy() {
        this.ebvSubscription.unsubscribe();
        this.sitesSubscription.unsubscribe();
        this.htmlSubscription.unsubscribe();
    }
}
