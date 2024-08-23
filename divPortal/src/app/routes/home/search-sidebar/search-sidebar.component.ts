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

@Component({
  selector: 'app-search-sidebar',
  templateUrl: './search-sidebar.component.html',
  styleUrls: ['./search-sidebar.component.scss']
})
export class SearchSidebarComponent implements OnInit {

  sitesFiltered: Site[];

  showDivFilterAndSearch: boolean;

  siteLayer: Layer; 

  showFilter: boolean;
  showSearch: boolean;

  divFilter: DivFilterAndSearch;
  searchQuery: string;

  sites: string[];
  standardObservations: StandardObservation[];
  habitats: Habitat[];

  constructor(private sharedService: SharedService,
              private layersService: LayersSidebarService,
              private homeService: HomeService,
              private offsidebarService: OffsidebarService,
              private settings: SettingsService) { }

  ngOnInit(): void {
    this.divFilter = new DivFilterAndSearch();
    this.showDivFilterAndSearch = true;
    this.showFilter = false;
    this.showSearch = false;

    this.readData();
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
  }

  async filterAndSearch(type: string) {
    if (type == 'filter') {
      this.searchReset();
    } else if (type == 'search') {
      this.filterReset();
    }
    this.hideOtherLayers();

    const response = await this.sharedService.post('site/filterAndSearch', this.divFilter)
    this.sitesFiltered = response.entity.sites;

    let ids = [];
    ids = this.sitesFiltered?.map(site => site.id);

    this.offsidebarService.showSites({
      action: 'showSites',  
      sites: ids,
    });

    if (this.sitesFiltered && this.sitesFiltered.length > 0) {
      this.createSitesLayer(this.siteLayer);
    }
    
    this.offsidebarOpen();
  }

  offsidebarOpen() {
    if (!this.settings.getLayoutSetting('offsidebarOpen')) {
        this.settings.toggleLayoutSetting('offsidebarOpen');
    }
}

  async hideOtherLayers() { 
    this.siteLayer.showMap = false;
    const state = {
      layer: this.siteLayer,
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

  async createSitesLayer(layer: Layer) {
    const ids = this.sitesFiltered?.map(s => s.id);
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

    const styleCache = {};


		layer.layerVector = new VectorLayer({
			source: clusterSource,
			style: feature => {
				const size = feature.get('features').length;
				const features = feature.get('features');
				for (var i = 0; i < features.length; i++) {
						let style = styleCache[size];

						if (size > 1) {
							if (!style) { 
								style = new Style({
									image: new CircleStyle({
										radius: 10,
										stroke: new Stroke({
											color: '#fff',
										}),
										fill: new Fill({
											color: '#cc7000',
										}),
									}),
									text: new Text({
										text: size.toString(),
										fill: new Fill({
											color: '#fff',
										}),
									}),
								});
								styleCache[size] = style;
							}

							return style;
						} else { 
              let style = new Style({
                image: new Icon(({
                  anchor: [0.5, 1],
                  src: "assets/img/map/marker-orange.png"
                }))
              })

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

      this.homeService.turnOnOffLayer(state);

      this.homeService.markOneLayer({
        layer: layer
      });
  }



}
