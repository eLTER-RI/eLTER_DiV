import { Router, NavigationEnd } from '@angular/router';
import { SettingsService } from './../../../core/settings/settings.service';
import { Subscription } from 'rxjs';
import { OffsidebarService } from './../../../layout/offsidebar/offsidebar.service';
import { Station } from './../../../shared/model/station-db';
import { FilterStationIDTO } from './../../../shared/model/filter-station-ib';
import { SosPath } from './../../../shared/model/sos-path-db';
import { Phenomenon } from './../../../shared/model/phenomenon-db';
import { SharedService } from './../../../shared/service/shared.service';
import { FilterSiteIDTO } from './../../../shared/model/filter-site-ib';
import { CountryDTO } from './../../../shared/model/country-db';
import { ActivityDTO } from './../../../shared/model/activity-db';
import { HomeService } from './../home/home.service';
import { LayersSidebarService } from './layers-sidebar.service';
import { Component, OnInit } from '@angular/core';
import { Layer } from 'src/app/shared/model/layer';
import TileLayer from 'ol/layer/Tile';
import TileWMS from 'ol/source/TileWMS';
import { Site } from 'src/app/shared/model/site-db';
import VectorSource from 'ol/source/Vector';
import VectorLayer from 'ol/layer/Vector';
import Cluster from 'ol/source/Cluster';
import { Circle as CircleStyle, Fill, Stroke, Style, Icon, Text } from 'ol/style';
import GeoJSON from 'ol/format/GeoJSON.js';
import { tmpdir } from 'os';

@Component({
  selector: 'app-layers-sidebar',
  templateUrl: './layers-sidebar.component.html',
  styleUrls: ['./layers-sidebar.component.scss']
})
export class LayersSidebarComponent implements OnInit {

  sites: Site[];
  siteShowFilter: boolean;
  siteFilterDTO: FilterSiteIDTO = new FilterSiteIDTO();
  titles: string[];
  countries: CountryDTO[];
  activities: ActivityDTO[];

  stations: Station[];
  stationShowFilter: boolean;
  phenomenons: Phenomenon[];
  stationFilterDTO: FilterStationIDTO = new FilterStationIDTO();
  usedPhenomenonsLabelEnFilter: string[] = [];
  sosPaths: SosPath[];

  allLayers: Layer[];

  showLayers: boolean;

  markedLayer?: Layer;

  showSiteFilter: boolean;

  addSelectedLayersSubscription: Subscription;
  hideAllLayersSubscription: Subscription;

  showLayerIcon: boolean[] = [];

  zIndexOffset = 1300;
  styleCache;

  constructor(private layersService: LayersSidebarService,
              private homeService: HomeService,
              private sharedService: SharedService,
              private offsidebarService: OffsidebarService,
              private settings: SettingsService,
              private router: Router) {
                this.turnOnLayerAfterRouting();
              }

  ngOnInit(): void {
    this.styleCache = {};

    this.readCodebook();

    this.initSubscriptions();
  }

  initSubscriptions() {
    this.addSelectedLayersSubscription = this.offsidebarService.selectedLayers.subscribe( state => {
      if (state && state.action == 'addSelectedLayers') {
        this.addSelectedLayers(state.layers);
      } 
    });

    this.hideAllLayersSubscription = this.homeService.hideAllLayersObservable.subscribe( obj => {
      this.allLayers?.forEach(lay => {
        if (lay.showMap) {
          this.btn_showOrHideLayersOnMap(lay);
        }
      });
    });
  }

  turnOnLayerAfterRouting() {
    this.router.events.subscribe((val) => {
      if (val instanceof NavigationEnd && val.url.indexOf('home') > -1) {
        this.allLayers?.forEach( layer => {
          if (layer.showMap) {
            this.addLayerAndWait(layer)
          }
        });
      } 
    });
  }

  async addLayerAndWait(layer) {
    await new Promise( resolve => setTimeout(resolve, 100) );
    const state = {
      layer: layer,
      action:  layer.showMap ? 'turnOn' : 'turnOff',
      bbox:  layer.showMap  ? layer.bbox : undefined
    }
    this.homeService.turnOnOffLayer(state);
    this.refreshZIndex();
  }

  async readSites() {
    const response  = await this.sharedService.post('site/filter', this.siteFilterDTO);
    this.sites = response.entity.sites;
  }

  async readStations() {
    const response = await this.sharedService.post('sos/filter', this.stationFilterDTO);
    this.stations = response.entity.stations;
  }

  async readCodebook() {
    await this.readSites();
    await this.readLayers();
    this.readSelectedLayers();
    this.readStations();

    const response = await this.sharedService.get('site/getAllCountries');
    this.countries = response.entity;

    const response1 = await this.sharedService.get('site/getAllActivities');
    this.activities = response1.entity;

    const response2 = await this.sharedService.get('site/getAllTitles');
    this.titles = response2.entity;

    const response3 = await this.sharedService.get('sos/getAllSosPath');
    this.sosPaths = response3.entity;

    const response4 = await this.sharedService.get('sos/allPhenomenons');
    this.phenomenons = response4.entity;

  }

  readSelectedLayers() {
    const layers = sessionStorage.getItem('selectedLayers');
   
    if (layers !== null && layers != 'undefined' && layers != undefined) {
      const selectedLayers = JSON.parse(layers);
      this.allLayers = this.allLayers.concat(selectedLayers);
    }
  }

  async readLayers() {
    const response = await this.layersService.getLayers(['global'], '');
    this.allLayers = response.entity;

    const resp3 = await this.layersService.getLayers(['special'], 'station');
    const stationLayer = resp3.entity[0];
    this.allLayers = [stationLayer].concat(this.allLayers);


    const resp2 = await this.layersService.getLayers(['special'], 'sites');
    const siteLayer = resp2.entity[0];
    this.btn_showOrHideLayersOnMap(siteLayer);
    this.allLayers = [siteLayer].concat(this.allLayers);
  }

  changeMarkedLayer(layer: Layer) {
    if (this.markedLayer === layer) {
      delete this.markedLayer;
    } else {
      this.markedLayer = layer;
      if (!layer.showMap) {
        this.btn_showOrHideLayersOnMap(layer);
      }
    }

    this.homeService.markOneLayer({
      layer: this.markedLayer
    });

  }

  btn_showOrHideLayersOnMap(layer: Layer) {
      layer.showMap = !layer.showMap;

      if (layer.showMap) {
        this.markedLayer = layer;
      } else {
        if (this.markedLayer == layer) {
          delete this.markedLayer;
        }
      }

      this.homeService.markOneLayer({
        layer: this.markedLayer
      });

      if (layer.layerTile == null || layer.layerTile == undefined) {
        if (layer.code == 'sites') {
          this.createSitesLayer(layer);
        } else if (layer.code == 'station' && (layer.layerVector == undefined || layer.layerVector == null)) {
          this.createStationsLayer(layer);
        } else {
          layer.layerTile = new TileLayer({
            source: new TileWMS({
                url: layer.geoUrlWms,
                cacheSize: 20480,
                params: {
                    'LAYERS': layer.layerName,
                    'TILED': true,
                },
                serverType: 'geoserver',
                transition: 0,
                tileLoadFunction: function(tile, src) {
                  var client = new XMLHttpRequest();

                  client.responseType = 'blob';
                  client.open('GET', src);
    
                  if (layer.authUsername && layer.authPassword) {
                    client.setRequestHeader('Authorization', 'Basic ' + btoa(layer.authUsername + ':' + layer.authPassword));
                  }

                  client.onload = function() {
                    //@ts-ignore
                    tile.getImage().src = URL.createObjectURL(client.response);
                  };
                  client.send();
                }
    
            }),
            visible: true,
          });

         if (layer.layerNameBiggerZoom) {
          layer.layerTileBiggerZoom = new TileLayer({
            source: new TileWMS({
                url: layer.geoUrlWms,
                cacheSize: 20480,
                params: {
                    'LAYERS': layer.layerNameBiggerZoom,
                    'TILED': true,
                },
                serverType: 'geoserver',
                transition: 0
            }),
            visible: true,
          });
         }
        } 
      }

      const state = {
        layer: layer,
        action:  layer.showMap ? 'turnOn' : 'turnOff',
        bbox:  layer.showMap  ? layer.bbox : undefined
      }

      this.homeService.turnOnOffLayer(state);
    
      this.refreshZIndex();
  }

  async createStationsLayer(layer: Layer) {
    if (!this.stations || this.stations.length == 0) {
      return;
    }

    const ids = this.stations.map(station => station.id);
    if (ids) {
      const cql = 'id in (' + ids.toString() + ')';

      const vectorLayer = new VectorLayer({
        source: new VectorSource({
          format: new GeoJSON(),
          url: layer.geoUrlWfs + "&CQL_FILTER=" + cql
        }),
        style: new Style({
                image: new Icon({
                  src: 'assets/img/map/marker-blue.png',
                }),
            })
      });
      layer.layerVector = vectorLayer;
    }
  }

  async createSitesLayer(layer: Layer) {
    const ids = this.sites?.map( s => s.id);
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


  }


  showHideMoreLayers() {
    this.offsidebarService.showAllLayers({
      action: 'showHideMoreLayers',
      layers: this.allLayers
    });
  }

  async addSelectedLayers(layers) {
    const ids = layers.filter(l => l.id != undefined).map( l => l.id);
    const mylayers = JSON.parse(JSON.stringify(layers.filter( l => l.layerType == 'mylayer')));

    let newSelectedLayers = await this.loadRealLayers(ids);

    const allLayers = newSelectedLayers.concat(mylayers);
    sessionStorage.setItem('selectedLayers', JSON.stringify(allLayers));

    this.addListToAllLayers(allLayers);
    this.closeOffsidebar();
  }

  addListToAllLayers(layers: Layer[]) {
    if (layers && layers.length > 0) {
      this.allLayers.forEach(layer => {
        if (layer.layerType != 'special') {
          let curr = layers.find(l => l.id ?  l.id == layer.id : l.idHash == layer.idHash);
          if (!curr) {                                                
            if (layer.showMap && layer.layerType != 'special') {
              this.btn_showOrHideLayersOnMap(layer);
            } 
            layer.skipForDelete = true;
          } else {
            layer.skipForDelete = false;
          }
        }
      });
        
      layers.forEach( layer => {
        const curr = this.allLayers.find(l => l.id ?  l.id == layer.id : l.idHash == layer.idHash);
        if (!curr) {
          this.allLayers.push(layer);
        }
      })

      this.allLayers = this.allLayers.filter(l => l.layerType == 'special' || !l.skipForDelete); 
    } else {
      this.allLayers.forEach(layer => {
          if (layer.showMap && layer.layerType != 'special') {
            this.btn_showOrHideLayersOnMap(layer);
          }
      });
      this.allLayers = this.allLayers.filter(l => l.layerType == 'special');
    }

  }

  closeOffsidebar(){
    if (this.settings.getLayoutSetting('offsidebarOpen')) {
        this.settings.setLayoutSetting('offsidebarOpen', false);
      }
  }

  async loadRealLayers(ids:number[]) {
    if (!ids || ids[0] == undefined) {
      return [];
    } 
    const response = await this.layersService.getLayersByIds(ids);
    const newSelectedLayers = response.entity;

    return newSelectedLayers;
  }

  async btn_fiterSites(layer: Layer) {
    const state = {
      layer: layer,
      action: 'turnOff'
    }
    this.homeService.turnOnOffLayer(state);

    await this.readSites();
    
    await this.createSitesLayer(layer);
    
    const state2 = {
      layer:  layer,
      action: 'turnOn',
      bbox: layer.bbox
    }
    this.homeService.turnOnOffLayer(state2);

    layer.showMap = true;
  }

  async btn_fiterStations(layer: Layer) {
    const state = {
      layer:  layer,
      action: 'turnOff'
    }
    this.homeService.turnOnOffLayer(state);

    await this.readStations();

    await this.createStationsLayer(layer);

    const state2 = {
      layer:  layer,
      action: 'turnOn',
      bbox: layer.bbox
    }
    this.homeService.turnOnOffLayer(state2);

    layer.showMap = true;

  }

  dropSuccess(): void {
    this.refreshZIndex();
  }

  refreshZIndex(): void {
    const selectedLayers:Layer[] = [];
		const size = this.allLayers.length;
		let sizeMinus = 0;
		this.allLayers.forEach(l => {
			if  (l.layerVector != null && l.layerVector != undefined) {
				l.layerVector.setZIndex(this.zIndexOffset + size - sizeMinus);
				sizeMinus++;
			}
			if (l.layerTile != null && l.layerTile != undefined) {
				l.layerTile.setZIndex(this.zIndexOffset + size - sizeMinus);
				sizeMinus++;
			}

      selectedLayers.push(l);
		});

    this.homeService.actionChanged({
      action: 'zindexChanged',
      layers: selectedLayers
    })
	}

  getIconClassForMarkedLayer(layer) {
    if (this.markedLayer && this.markedLayer?.id) {
      return this.markedLayer?.id === layer?.id ? 'fa icon-check mt-1' : 'far fa-circle mt-1';
    } else if (this.markedLayer) {
      return this.markedLayer?.idHash === layer?.idHash ? 'fa icon-check mt-1' : 'far fa-circle mt-1';
    } else {
      return 'far fa-circle mt-1';
    }
  }



}
