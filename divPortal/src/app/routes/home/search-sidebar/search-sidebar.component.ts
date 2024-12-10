import { Component, OnInit } from '@angular/core';
import TileLayer from 'ol/layer/Tile';
import VectorLayer from 'ol/layer/Vector';
import Cluster from 'ol/source/Cluster';
import TileWMS from 'ol/source/TileWMS';
import VectorSource from 'ol/source/Vector';
import { Fill, Icon, Stroke, Style, Text } from 'ol/style';
import CircleStyle from 'ol/style/Circle';
import GeoJSON from 'ol/format/GeoJSON.js';
import { DivFilterAndSearch } from 'src/app/shared/model/div-filter-and-search';
import { FilterSiteIDTO } from 'src/app/shared/model/filter-site-ib';
import { Habitat } from 'src/app/shared/model/habitat';
import { Layer } from 'src/app/shared/model/layer';
import { Site } from 'src/app/shared/model/site-db';
import { StandardObservation } from 'src/app/shared/model/standard-observation';
import { SharedService } from 'src/app/shared/service/shared.service';
import { LayersSidebarService } from '../layers-sidebar/layers-sidebar.service';
import { HomeService } from '../home/home.service';
import { OffsidebarService } from 'src/app/layout/offsidebar/offsidebar.service';
import { SettingsService } from 'src/app/core/settings/settings.service';
import { Subscription } from 'rxjs';
import { SearchSidebarService } from './search-sidebar.service';
import { Page } from 'src/app/shared/model/Page';
import { Dataset } from 'src/app/shared/model/dataset';
import { DatasetDetailsListService } from '../dataset-details-list/dataset-details-list.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-search-sidebar',
  templateUrl: './search-sidebar.component.html',
  styleUrls: ['./search-sidebar.component.scss']
})
export class SearchSidebarComponent implements OnInit {

  sitesFiltered: Site[];
  datasetsFiltered: Dataset[];

  showDivFilterAndSearch: boolean;

  siteLayer: Layer; 
  datasetSiteLayer: Layer; 

  showFilter: boolean;
  showSearch: boolean;

  divFilter: DivFilterAndSearch;
  searchQuery: string;

  pageForDatasetSite: Page;

  sites: string[];
  standardObservations: StandardObservation[];
  habitats: Habitat[];

  layerWithCodeSubscription: Subscription;
  paginationRefreshSubscription: Subscription;
  datasetSearchSubscription: Subscription;
  clearDatasetsAndSitesSubscription: Subscription;

  constructor(private sharedService: SharedService,
              private layersService: LayersSidebarService,
              private homeService: HomeService,
              private offsidebarService: OffsidebarService,
              private searchSidebarService: SearchSidebarService,
              private datasetDetailsListService: DatasetDetailsListService,
              private settings: SettingsService, 
              private toastrService: ToastrService,
              private translateService: TranslateService) { }

  ngOnInit(): void {
    this.divFilter = new DivFilterAndSearch();
    this.showDivFilterAndSearch = false;
    this.showFilter = false;
    this.showSearch = false;

    this.pageForDatasetSite = new Page();
    this.pageForDatasetSite.currentPage = 1;

    this.readData();

    this.initSubscriptions();

  }

  initSubscriptions() {
    this.paginationRefreshSubscription = this.datasetDetailsListService.paginationObservable.subscribe( event => {
      if (event && event.page) {
        this.pageForDatasetSite = event.page;
        this.filterAndSearchDataset(false, true);
      }
    });

    this.layerWithCodeSubscription = this.searchSidebarService.layerWithCode.subscribe( obj => {
      if (obj) {
        this.hideAndShowSitesForTab(obj);
      }
    });

    this.datasetSearchSubscription = this.searchSidebarService.datasetSearchObservable.subscribe( obj => {
      if (obj) {
        if (!this.pageForDatasetSite || !this.pageForDatasetSite.currentPage) {
          this.pageForDatasetSite = new Page();
          this.pageForDatasetSite.currentPage = 1;
        }
        this.divFilter = new DivFilterAndSearch();
        this.divFilter.siteIds = [obj.searchParam];
        this.filterAndSearchDataset();
      }
    });

    this.clearDatasetsAndSitesSubscription = this.offsidebarService.clearSitesAndDatasetsObservable.subscribe( obj => {
      this.searchReset();
      this.filterReset();
      this.filterAndSearch(null);
    });

  }

  async readData() {
    const response  = await this.sharedService.get('site/getAllTitles');
    this.sites = response.entity;

    const responseStandardObservation = await this.sharedService.get('standardObservation/getAll');
    this.standardObservations = responseStandardObservation.entity;

    const responseHabitat = await this.sharedService.get('habitat/getAll');
    this.habitats = responseHabitat.entity;

    const resp2 = await this.layersService.getLayers(['special'], 'sites');
    this.siteLayer = resp2.entity[0];
    this.siteLayer.searchFilterType = true;
    
    const resp3 = await this.layersService.getLayers(['special'], 'sitesDataset');
    this.datasetSiteLayer = resp3.entity[0];
    this.datasetSiteLayer.searchFilterType = true;
  }

  async filterAndSearch(type: string) {
    if (type == 'filter') {
      this.searchReset();
    } else if (type == 'search') {
      this.filterReset();
    }

    this.pageForDatasetSite.currentPage = 1;

    this.divFilter.siteIds = [];

    let searchEmpty = !type;
    this.filterAndSearchSite(searchEmpty);
    this.filterAndSearchDataset(searchEmpty);
  }

  // search site
  async filterAndSearchSite(searchEmpty: boolean = false) {
    if (!searchEmpty) {
      this.hideLayer(this.siteLayer);
    }

    const responseSites = await this.sharedService.post('site/filterAndSearch', this.divFilter)
    this.sitesFiltered = responseSites.entity.sites;

    let idSites = [];
    idSites = this.sitesFiltered?.map(site => site.id);

    this.offsidebarService.showSites({
      action: 'showSites',  
      sites: idSites,
    });

    if (this.sitesFiltered && this.sitesFiltered.length > 0) {
      this.createSitesLayer(this.siteLayer, this.sitesFiltered, 'site');
    } else {
      if (this.siteLayer && (this.offsidebarService.getLayerCodeForSidebar() == null || this.offsidebarService.getLayerCodeForSidebar() == 'search-sidebar')) {
        this.hideLayer(this.siteLayer);
      }
    }

    if (!searchEmpty) {
      this.offsidebarOpen();
    }
  }

  // search dataset invenio
  async filterAndSearchDataset(searchEmpty: boolean = false, paginationSearch: boolean = false) {
    let showLayer = this.offsidebarService.getLayerCodeForSidebar() == null || this.offsidebarService.getLayerCodeForSidebar() == 'search-sidebar';
    if (showLayer) {
      this.hideLayer(this.datasetSiteLayer);
    }

    this.divFilter.page = this.pageForDatasetSite.currentPage;
    const responseDatasetSites = await this.sharedService.post('dataset/filterAndSearch', this.divFilter)
    if (responseDatasetSites.status != 200) {
      if (!searchEmpty) {
        this.toastrService.error(this.translateService.instant('exception.' + responseDatasetSites.entity.status + ''), "Error"); 
      }
    } else {
      this.datasetsFiltered = responseDatasetSites.entity.datasets;

      if (responseDatasetSites.entity?.page) {
        this.pageForDatasetSite = responseDatasetSites.entity.page;
      } else {
        this.pageForDatasetSite = new Page();
      }
      
    }

    this.offsidebarService.showDatasets({
      action: 'showDatasets',  
      datasets: this.datasetsFiltered,
      page: this.pageForDatasetSite
    });

    if (this.datasetsFiltered && this.datasetsFiltered.length > 0) {
      let uniqueSites = new Set();

      this.datasetsFiltered.forEach(dataset => {
        dataset.sites.forEach(site => {
          // Use JSON.stringify to create a unique string representation of each site object
          uniqueSites.add(JSON.stringify(site));
        });
      });

      const datasetSitesFiltered = Array.from(uniqueSites).map((siteString: string) => JSON.parse(siteString));

      if (showLayer) {
        this.createSitesLayer(this.datasetSiteLayer, datasetSitesFiltered, 'sitesDataset', paginationSearch);
      }
    } else {
      if (this.datasetSiteLayer && showLayer) {
        this.hideLayer(this.datasetSiteLayer);
      }
    }
    if (!searchEmpty) {
      this.offsidebarOpen();
    } else {
      this.hideAllLayersFromHere();
    }
  }

  offsidebarOpen() {
    if (!this.settings.getLayoutSetting('offsidebarOpen')) {
        this.settings.toggleLayoutSetting('offsidebarOpen');
    }
  }

  hideAllLayersFromHere() {
    if (this.siteLayer) {
      this.siteLayer.showMap = false;
      const state = {
        layer: this.siteLayer,
        action: 'turnOff',
        bbox:  undefined
      }
      this.homeService.turnOnOffLayer(state);
    }
    if (this.datasetSiteLayer) {
      this.datasetSiteLayer.showMap = false;
      const state = {
        layer: this.datasetSiteLayer,
        action: 'turnOff',
        bbox:  undefined
      }
      this.homeService.turnOnOffLayer(state);
    }
  }

  async hideLayer(layer: Layer) { 
    layer.showMap = false;
    const state = {
      layer: layer,
      action: 'turnOff',
      bbox:  undefined
    }

    this.homeService.turnOnOffLayer(state);
    this.homeService.hideAllLayers();
  }

  searchReset() {
    this.divFilter.searchText = '';
  }

  filterReset() {
    let tmpSearchText = this.divFilter.searchText;
    this.divFilter = new DivFilterAndSearch();
    this.divFilter.searchText = tmpSearchText;
  }

  hideAndShowSitesForTab(eventObj: any) {
    if (((eventObj.code == 'sites' && this.sitesFiltered?.length > 0) ||
        (eventObj.code == 'datasets' && this.datasetsFiltered?.length > 0)) &&
        (this.offsidebarService.getLayerCodeForSidebar() == 'search-sidebar' || !this.offsidebarService.getLayerCodeForSidebar())) {
      let layerToTurnOn, layerToTurnOff;
      if (eventObj.code == 'sites') {
        this.datasetSiteLayer.showMap = false; // TODO invenio mozda
        this.siteLayer.showMap = true;
        layerToTurnOn = this.siteLayer;
        layerToTurnOff = this.datasetSiteLayer;
      } else if (eventObj.code == 'datasets') {
        this.siteLayer.showMap = false; // TODO invenio mozda
        this.datasetSiteLayer.showMap = true;
        layerToTurnOn = this.datasetSiteLayer;
        layerToTurnOff = this.siteLayer;
      }

      const stateOff = {
        layer: layerToTurnOff,
        action: 'turnOff',
        bbox:  undefined
      }

      this.homeService.turnOnOffLayer(stateOff);

      this.homeService.hideAllLayers();
      const stateOn = {
        layer: layerToTurnOn,
        action: 'turnOn',
        bbox:  undefined
      }
      this.homeService.turnOnOffLayer(stateOn);

      layerToTurnOn.codeForSidebar = 'search-sidebar';

      this.homeService.markOneLayer({
        layer: layerToTurnOn
      });
    }
  }

  async createSitesLayer(layer: Layer, sites: Site[], type: string, showLayer: boolean = true) {
    const ids = sites?.map(s => s.id);
    const cql = 'id in (' + ids?.toString() + ')';
          
    layer.layerTile = new TileLayer({
      source: new TileWMS({
          url: layer.geoUrlWms,
          cacheSize: 20480,
          params: {
              'LAYERS': layer.layerName,
              'TILED': true,
              'CQL_FILTER': cql
          },
          serverType: 'geoserver',
          transition: 0,

      }),
      visible: true,
    });


    const vectorSource =   new VectorSource({
      format: new GeoJSON(),
      url: layer.geoUrlWfs + "&CQL_FILTER=" + cql,
    });

    layer.layerVector = new VectorSource({});
    const clusterSource = new Cluster({
			source: vectorSource,
			distance: 40,
		});

    // const styleCache = {};

    let circleStyleFillColor;
    let imageIcon;

    if (type == 'site') {
      circleStyleFillColor = '#cc7000';
      imageIcon = "assets/img/map/marker-orange.png";
    } else if (type == 'sitesDataset') {
      circleStyleFillColor = '#5aadc7';
      imageIcon = "assets/img/map/marker-blue.png";
    }

		layer.layerVector = new VectorLayer({ 
			source: clusterSource,
			style: feature => {
				const size = feature.get('features').length;
				const features = feature.get('features');
				for (var i = 0; i < features.length; i++) {
            let style;
						if (size > 1) {
								style = new Style({
									image: new CircleStyle({
										radius: 10,
										stroke: new Stroke({
											color: '#fff',
										}),
										fill: new Fill({
											color: circleStyleFillColor,
										}),
									}),
									text: new Text({
										text: size.toString(),
										fill: new Fill({
											color: '#fff',
										}),
									}),
                  zIndex: 10
								});

							return style;
						} else { 
              let style = new Style({
                image: new Icon(({
                  anchor: [0.5, 1],
                  src: imageIcon
                })), 
                zIndex: 20
              });

              return style;
					  }
				  };
			  },
		  });
      layer.showMap = true;

      const state = {
        layer: layer,
        action:  layer.showMap ? 'turnOn' : 'turnOff',
        bbox:  layer.showMap  ? layer.bbox : undefined
      }

      if (showLayer) {
        this.homeService.turnOnOffLayer(state);

        layer.codeForSidebar = 'search-sidebar';

        this.homeService.markOneLayer({
          layer: layer
        });
      }

  }

}
