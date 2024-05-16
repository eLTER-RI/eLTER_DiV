import { HttpClient, HttpHeaders } from '@angular/common/http';
import { SharedService } from './../../../shared/service/shared.service';
import { BoundingBox } from './../../../shared/model/bounding-box-db';
import { Router } from '@angular/router';
import { OffsidebarService } from './../../../layout/offsidebar/offsidebar.service';
import { Layer } from './../../../shared/model/layer';
import { Subscription } from 'rxjs';
import { HomeService } from './home.service';
import { SettingsService } from './../../../core/settings/settings.service';
import { Component, HostListener, OnInit, OnDestroy, SecurityContext } from '@angular/core';
import { Map, Overlay, View } from 'ol';
import { Attribution, FullScreen, ScaleLine, defaults as defaultControls } from 'ol/control';
import TileLayer from 'ol/layer/Tile';
import { PaginationInstance } from 'ngx-pagination';
import OSM from 'ol/source/OSM';
import TileWMS from 'ol/source/TileWMS';
import XYZ from 'ol/source/XYZ';
import { boundingExtent } from 'ol/extent';
import VectorSource from 'ol/source/Vector';
import VectorLayer from 'ol/layer/Vector';
import GeoJSON from 'ol/format/GeoJSON.js';
import { Fill, Stroke, Style } from 'ol/style';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { unByKey } from 'ol/Observable';
import CircleStyle from 'ol/style/Circle';
import { LineString, Polygon } from 'ol/geom';
import Draw, { createBox, createRegularPolygon } from 'ol/interaction/Draw';
import { getArea, getLength } from 'ol/sphere';
import { cloneDeep } from 'lodash';

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {

    map: Map;
    view: View = new View({
        projection: 'EPSG:3857',
        zoom: 2
    });
    mapHeight: number;
    extent;

    layers: any[];
    basemaps: any[];

    
    basemapSubscription: Subscription;
    layersSubscription: Subscription;

    markedLayer: Layer;
    markedLayerSubscription: Subscription;
    sitePoygonLayer: any;

    actionSubscription: Subscription;

    repositionMapSubscription: Subscription;

    showBiggerZoom: boolean;

    urlMarkedLayerLegend;
    urlMarkedLayerLegendBiggerZoom;

    scrollbarOptionsLegend = { theme: 'dark-thick', scrollButtons: { enable: true }, scrollInertia: 0, mouseWheelPixels: 170};

    //layer with times
    interval: any;
    isIntervalOn: boolean = false;
    config: PaginationInstance = {
        id: 'custom',
        itemsPerPage: 1,
        currentPage: 0,
    };

    //measure and draw
    sketch;
	helpTooltipElement;
	helpTooltip;
    drawingTool = null;
	drawingSnap = false;
	drawLayer = null;
	cursorType: string;
	canGrab: boolean;
    measureTooltip;
	measureTooltipElement:any = null;
    drawMeasure: Draw;
	sourceMeasure = new VectorSource();
    saveMarkedLayerForMeasure;
    measureType;
    measureLayer: any = new VectorLayer({
		source: this.sourceMeasure,
		style: new Style({
			fill: new Fill({
				color: 'rgba(255, 255, 255, 0.2)',
			}),
			stroke: new Stroke({
				color: '#ffcc33',
				width: 2,
			}),
			image: new CircleStyle({
				radius: 7,
				fill: new Fill({
					color: '#ffcc33',
				}),
			}),
		}),
	});
    scaleType = false;
	control;




    constructor(private settings: SettingsService,
                private homeService: HomeService,
                private offsidebarService: OffsidebarService,
                private router: Router,
                private sharedService: SharedService,
                public sanitizer: DomSanitizer,
                public http: HttpClient) { }

    ngOnInit() {
        this.layers = [];
        this.basemaps = [];
   
        this.initSubscriptions();

        if (this.map == null || this.map == undefined) {
            this.initMap();
        }
    }

    initSubscriptions() {
        this.basemapSubscription = this.homeService.currBaseMap.subscribe( obj => {
            if (obj != null && obj.action == 'changeBaseMap') {
                this.changeBaseMap(obj.basemap);
            }
        });

        this.layersSubscription = this.homeService.layer.subscribe( obj => {
            if (obj != null) {
                this.turnOnOffLayer(obj.layer, obj.action, obj.bbox);
               
                this.removeSitePolygonLayer();
            }
        });

        this.markedLayerSubscription = this.homeService.markedLayer.subscribe( obj => {
            if (obj != null) {
                this.markedLayer = obj.layer;

                if (this.markedLayer?.geoUrlLegend) {
                    this.getLegendImage();
                } else {
                    delete this.urlMarkedLayerLegend;
                    delete this.urlMarkedLayerLegendBiggerZoom;
                }

                this.removeSitePolygonLayer();
            }
        });

        this.actionSubscription = this.homeService.action.subscribe( obj => {
            if (obj != null && obj.action == 'zindexChanged') {
                this.refreshZIndex(obj.layers);
            } else if (obj != null && obj.action == 'removeSitePolygon') {
                this.removeSitePolygonLayer();
            }
        });

        this.repositionMapSubscription = this.homeService.repositionMapObserbable.subscribe( obj => {
            if (obj != null && obj.action == 'repositionMap') {
                this.repositionMap();
            }
        });
    }

    initMap() {
		this.mapHeight = window.innerHeight - 115;

        let loc1 = [-3934190.42219, 11575763.64496];
        let loc2 = [4868077.67123,3900033.1164];

        //@ts-ignore
        this.extent = new boundingExtent([loc1, loc2]);

        this.map = new Map({
            layers: this.layers,
            target: 'map',
            view: this.view,
            controls: [this.scaleControl(), new Attribution({})]
        });

        this.map.on('singleclick', e => {
            if (this.markedLayer != null  && this.markedLayer.layerTile
                && this.markedLayer.layerType != 'special') {
              const url = this.markedLayer.layerTile.getSource().getFeatureInfoUrl(
                e.coordinate,
                this.map.getView().getResolution(),
                this.map.getView().getProjection(),
                {'INFO_FORMAT': 'text/html'}
              );

              if (url) {
                fetch(url)
                  .then((response) => response.text())
                  .then((html) => {
                    this.offsidebarService.showHtml({
                        action: 'showHTML',
                        html: html
                    });
                  });
              }
            } else if (this.markedLayer?.code == 'sites') {
                const url = this.markedLayer.layerTile.getSource().getFeatureInfoUrl(
                    e.coordinate,
                    this.map.getView().getResolution(),
                    this.map.getView().getProjection(),
                    {'INFO_FORMAT': 'application/json'}
                  );
    
                  if (url) {
                    fetch(url)
                      .then((response) => response.text())
                      .then((html) => {
                        const feature = JSON.parse(html);

                        if (feature.features[0]) {
                            let idStr = feature.features[0].id;
                            idStr = idStr.substring(6);
                            this.openSiteDetails(idStr);
                        }
                      });
                  }
            }

            this.map.forEachFeatureAtPixel(e.pixel, f => {
                //@ts-ignore
                const features = f.values_.features;
                
                let feature;
                if (features && features.length == 1 && this.markedLayer?.code == 'sites') {
                    feature = features[0].id_;
                } else {
                    feature = f.getId()?.toString();
                }
                if (feature) {
                    if (feature.indexOf('site_points') > -1 && this.markedLayer?.code == 'sites') {
                        const id = feature.substring(12);
                        this.openSiteDetails(id);
                    } else if (feature.indexOf('station') > -1 && this.markedLayer?.code == 'station') {
                        const id = feature.substring(8);
                        this.offsidebarService.showStation({
                            station: Number(id),
                            action: 'showStation'
                        });
                        this.offsidebarOpen();
                    }
                }
            });
        });

        this.map.on('moveend', e => {
            //@ts-ignore
            const newZoom: number = this.map.getView().getZoom();
            this.showBiggerZoom = newZoom >= 12;
            // const extent = this.map.getView().calculateExtent(this.map.getSize());

        });

        this.repositionMap();

        this.map.addLayer(this.measureLayer);
    }

    openSiteDetails(idStr: string) {
        this.removeSitePolygonLayer();                    

        const id = Number(idStr);
        this.offsidebarService.showSite({
            action: 'showSite',
            site: id
        });
        this.offsidebarOpen();

        this.showSitePolygon(id);
    }

    async showSitePolygon(id: number) {
        const response = await this.sharedService.get('site/getGeoJsonPolygon?id=' + id);
        const geoJSON = response.entity;

        this.sitePoygonLayer = new VectorLayer({
            source: new VectorSource({
                features: new GeoJSON().readFeatures(geoJSON, { 
                    dataProjection: 'EPSG:4326',
                    featureProjection:'EPSG:3857' }),
            }),
            style: new Style({
                fill: new Fill({
                    color: 'rgba(150, 206, 88, 0.8)'
                }),
                stroke: new Stroke({
                    color: 'rgba(150, 206, 88)',
                    width: 2
                })
            }),
            zIndex: -1
        });

        this.layers.push(this.sitePoygonLayer);
        this.map.addLayer(this.sitePoygonLayer);
    }

    removeSitePolygonLayer() {
        if (this.sitePoygonLayer) {
            this.layers.splice(this.sitePoygonLayer);
            this.map.removeLayer(this.sitePoygonLayer);
            delete this.sitePoygonLayer;
        }
    }

    offsidebarOpen() {
        if (!this.settings.getLayoutSetting('offsidebarOpen')) {
            this.settings.toggleLayoutSetting('offsidebarOpen');
        }
    }

    changeBaseMap(basemap) {
        if (this.map == null || this.map == undefined) {
            this.initMap();
        }
        this.map.removeLayer(this.basemaps[this.basemaps.length - 1]);
        
        var raster;
        if (basemap.name == 'Open Street Maps') {
            raster = new TileLayer({
                source: new OSM(),
                zIndex: -1,
                visible: true,
                // attributions: [bm.attribution],
                // cacheSize: 0,
            });

        } else {
            raster = new TileLayer({
                source: new XYZ({
                    url: basemap.geoUrlWms,
                    // attributions: [basemap.attribution]
                }),
                zIndex: -1,
                visible: true,
                // cacheSize: 0,

            });
        }

        this.basemaps.push(raster);
        this.map.addLayer(raster);
    } 

    turnOnOffLayer(layer: Layer, action: string, bbox: BoundingBox) {
        if (this.map == null || this.map == undefined) {
            this.initMap();
        }
        if (action == 'turnOn') {
            if (layer.layerTile != null && layer.layerTile != undefined && this.layers.indexOf(layer.layerTile) == -1) {
                this.layers.push(layer.layerTile);
                this.map.addLayer(layer.layerTile);
            }

            if (layer.layerTileBiggerZoom != null && layer.layerTileBiggerZoom != undefined  && this.layers.indexOf(layer.layerTileBiggerZoom) == -1) {
                this.layers.push(layer.layerTileBiggerZoom);
                this.map.addLayer(layer.layerTileBiggerZoom);
            }

            if (layer.layerVector != null && layer.layerVector != undefined  && this.layers.indexOf(layer.layerVector) == -1) {
                this.layers.push(layer.layerVector);
                this.map.addLayer(layer.layerVector);
            }
        } else if (action == 'turnOff') {
            if (layer.layerTile != null && layer.layerTile != undefined) {
                this.layers.splice(layer.layerTile);
                this.map.removeLayer(layer.layerTile);
            }

            if (layer.layerTileBiggerZoom != null && layer.layerTileBiggerZoom != undefined) {
                this.layers.splice(layer.layerTileBiggerZoom);
                this.map.removeLayer(layer.layerTileBiggerZoom);
            }
            
            if (layer.layerVector != null && layer.layerVector != undefined) {
                this.layers.splice(layer.layerVector);
                this.map.removeLayer(layer.layerVector);
            }
        } 

        if (bbox) {
            console.log(bbox)
            let loc1 = [bbox.minX, bbox.minY];
            let loc2 = [bbox.maxX, bbox.maxY];
            //@ts-ignore
            this.extent = new boundingExtent([loc1, loc2]);
            this.map.getView().fit(this.extent);
        }
    } 

    repositionMap(): void {
		window.scroll({
			top: 0,
			left: 0,
			behavior: 'smooth'
		});
		setTimeout(() => {
			this.invalidateMap();
		}, 200);
	}

    ngOnDestroy() {
        this.basemapSubscription.unsubscribe();
        this.layersSubscription.unsubscribe();
    }

    invalidateMap(): void {
		setTimeout(() => {
			this.map.updateSize();
			this.map.getView().fit(this.extent);

		}, 100);
	}

    refreshZIndex(selectedLayers: Layer[]): void {
		this.layers.forEach(layer => {
            selectedLayers.forEach( sLayer => {
                if (layer instanceof TileLayer && layer == sLayer.layerTile) {
                    layer.setZIndex(sLayer.layerTile.getZIndex());
                } else if (layer instanceof VectorLayer && layer == sLayer.layerVector) {
                    layer.setZIndex(sLayer.layerVector.getZIndex());
                }
            })
        
        })

	}

    setIntervalForLayerWithTime(operation) {
        if (this.markedLayer?.times != undefined && this.markedLayer?.times != null && this.markedLayer.times.length > 0) {
            if (operation == 'play') { 
                this.isIntervalOn = true;

                let i = 1;
                let j = 1;
            
                if (operation == 'play') {
                    i = this.config.currentPage;
                    j = this.config.currentPage;
                }
                this.interval = setInterval(() => {   
                    const layer = this.markedLayer;
                
                    if (this.layers.indexOf(this.markedLayer.layerTile) > -1) {
                        const zindex = this.markedLayer.layerTile.getZIndex();
                        this.map.removeLayer(this.markedLayer.layerTile)
                        this.layers.splice(this.markedLayer.layerTile, 1);
                        
                        this.markedLayer.layerTile = new TileLayer({
                            source: new TileWMS({
                                url: layer.geoUrlWms,
                                cacheSize: 20480,
                                params: {
                                    'LAYERS': layer.layerName,
                                    'TILED': true,
                                    'TIME':  this.markedLayer.times[j]
                                },
                                serverType: 'geoserver',
                                transition: 0,
                            }),
                            visible: true,
                        });

                        this.markedLayer.layerTile.setZIndex(zindex);
                        this.map.addLayer(this.markedLayer.layerTile);
                        this.layers.push(this.markedLayer.layerTile)
                    }
                
                    i++;
                    j = i % layer?.times?.length;
                    this.config.currentPage = j;
                }, 4000); //TODO 10k
            } else if (operation =='stop') {
                this.stopIntervalForLayerWithTime();
            } else if (operation == 'start') {
                this.config.currentPage = 1;
                this.stopIntervalForLayerWithTime();
            }
        }
    }

    timeOfLayerChanged(operation, position) {
        if (this.markedLayer?.times != undefined && this.markedLayer?.times != null && this.markedLayer.times.length > 0) {
            this.stopIntervalForLayerWithTime();

            let currentPage = this.config.currentPage;

            if (operation == 'move') {
                currentPage = position;
            } else if (operation == 'first') {
                currentPage = 0;
                this.config.currentPage = 1;
            } else if (operation == 'last') {
                currentPage = this.markedLayer.times.length;
                this.config.currentPage = this.markedLayer.times.length;
            }

            if (this.layers.indexOf(this.markedLayer.layerTile) > -1) {
                const zindex = this.markedLayer.layerTile.getZIndex();
                this.map.removeLayer(this.markedLayer.layerTile)
                this.layers.splice(this.markedLayer.layerTile, 1);
                
                this.markedLayer.layerTile = new TileLayer({
                    source: new TileWMS({
                        url: this.markedLayer.geoUrlWms,
                        cacheSize: 20480,
                        params: {
                            'LAYERS': this.markedLayer.layerName,
                            'TILED': true,
                            'TIME':  this.markedLayer.times[currentPage - 1]
                        },
                        serverType: 'geoserver',
                        transition: 0,
            
                    }),
                    visible: true,
                });

                this.markedLayer.layerTile.setZIndex(zindex);
                this.map.addLayer(this.markedLayer.layerTile);
                this.layers.push(this.markedLayer.layerTile);
            }
        }
    }

    stopIntervalForLayerWithTime() {
        clearInterval(this.interval);
        this.isIntervalOn = false; 
    }

    zoomIn() {
        var zoom = this.view.getZoom();
        //@ts-ignore
		this.view.setZoom(zoom + 0.5);
    }

    zoomOut() {
        var zoom = this.view.getZoom();
        //@ts-ignore
		this.view.setZoom(zoom - 0.5);
    }

    setCenterView() {
		this.map.getView().fit(this.extent);
	}

    measure(type) {
        this.measureType = type;
        console.log(this.measureType, "this.measureType")
        this.saveMarkedLayerForMeasure = cloneDeep(this.markedLayer);
        //@ts-ignore
        delete this.markedLayer;
		this.cursorType = 'none';
		this.canGrab = false;

		this.map.removeInteraction(this.drawMeasure);
		this.drawMeasure = new Draw({
			source: this.sourceMeasure,
			type: type,
			style: new Style({
				fill: new Fill({
					color: 'rgba(255, 255, 255, 0.2)',
				}),
				stroke: new Stroke({
					color: 'rgba(0, 0, 0, 0.5)',
					lineDash: [10, 10],
					width: 2,
				}),
				image: new CircleStyle({
					radius: 5,
					stroke: new Stroke({
						color: 'rgba(0, 0, 0, 0.7)',
					}),
					fill: new Fill({
						color: 'rgba(255, 255, 255, 0.2)',
					}),
				}),
			}),
		});
		this.map.addInteraction(this.drawMeasure);
		this.createMeasureTooltip();
		this.createHelpTooltip();

		let listener;
		this.drawMeasure.on('drawstart', evt => {
			this.sketch = evt.feature;
            //@ts-ignore
			let tooltipCoord = evt.coordinate;

			listener = this.sketch.getGeometry().on('change', evt => {
				const geom = evt.target;
				let output;
				if (geom instanceof Polygon) {
					output = this.formatArea(geom);
					tooltipCoord = geom.getInteriorPoint().getCoordinates();
				} else if (geom instanceof LineString) {
					output = this.formatLength(geom);
					tooltipCoord = geom.getLastCoordinate();
				}
				this.measureTooltipElement.innerHTML = output;
				this.measureTooltip.setPosition(tooltipCoord);
			});
		});

		this.drawMeasure.on('drawend', evt => {
			this.measureTooltipElement.className = 'ol-tooltip ol-tooltip-static';
			this.measureTooltip.setOffset([0, -7]);
			// unset sketch
			this.sketch = null;
			// unset tooltip so that a new one can be created
			this.measureTooltipElement = null;
			this.createMeasureTooltip();
			unByKey(listener);
		});
		//this.snapLeft();
	}

    formatArea(polygon) {
		const area = getArea(polygon);
		let output;
		output = Number((area / 10000).toFixed(4)) + ' ' + 'ha';
		return output;
	};

    formatLength(line) {
		const length = getLength(line);
		let output;
		output = Number(length.toFixed(4)) + ' ' + 'm';
		return output;
	};

    
	createHelpTooltip() {
		if (this.helpTooltipElement) {
			this.helpTooltipElement.parentNode.removeChild(this.helpTooltipElement);
		}
		this.helpTooltipElement = document.createElement('div');
		//this.helpTooltipElement.className = 'ol-tooltip';

		this.helpTooltip = new Overlay({
			//element: document.getElementById('ol-tooltip'),
			element: this.helpTooltipElement,

			offset: [15, 0],
			positioning: 'center-left',
		});
		this.map.addOverlay(this.helpTooltip);
	}


    measureCancel() {
        delete this.measureType;
        if (this.saveMarkedLayerForMeasure) {
            this.markedLayer= cloneDeep(this.saveMarkedLayerForMeasure);
            //@ts-ignore
            delete this.saveMarkedLayerForMeasure;
        }

		this.cursorType = 'grab';
		this.canGrab = true;

		this.map.getOverlays().getArray().slice(0).forEach(overlay => {
			this.map.removeOverlay(overlay);
		});

		this.map.removeInteraction(this.drawMeasure);
		this.map.removeOverlay(this.measureTooltip);
		this.helpTooltipElement.className = 'ol-tooltip-hidden';
		this.measureTooltipElement.innerHTML = '';

		var features = this.measureLayer.getSource().getFeatures();
		features.forEach((feature) => {
			this.measureLayer.getSource().removeFeature(feature);
		});
	}

    createMeasureTooltip() {
		if (this.measureTooltipElement) {
			this.measureTooltipElement.parentNode.removeChild(this.measureTooltipElement);
		}
		this.measureTooltipElement = document.createElement('div');
		this.measureTooltipElement.className = 'ol-tooltip ol-tooltip-measure';
		this.measureTooltip = new Overlay({
			element: this.measureTooltipElement,
			offset: [0, -15],
			positioning: 'bottom-center',
			stopEvent: false,
			insertFirst: false,
		});
		this.map.addOverlay(this.measureTooltip);
	}

    changeScale() {
		if (this.scaleType) {
			this.scaleType = false;
		} else {
			this.scaleType = true;
		}
		this.map.removeControl(this.control);
		this.map.addControl(this.scaleControl());
	}

    scaleControl() {
		if (this.scaleType) {
			this.control = new ScaleLine({
				units: 'metric',
			});
			return this.control;
		} else {
			this.control = new ScaleLine({
				units: 'metric',
				bar: true,
				steps: 8,
				text: false,
				minWidth: 140,
			});
		}
		return this.control;
	}

    btn_navigateToDiagram() {
        this.router.navigate(['diagram']);
    }

   
    getLegendImage() {
        let headers = new HttpHeaders();
        if (this.markedLayer.authPassword && this.markedLayer.authUsername ) {
            headers =headers.set('Authorization', 'Basic ' + btoa( this.markedLayer.authUsername+ ":" + this.markedLayer.authPassword));
        }

        this.http.get(this.markedLayer.geoUrlLegend, { headers, responseType: 'blob' }).subscribe(
            image => {
                this.urlMarkedLayerLegend = URL.createObjectURL(image);
            }
        );

        if (this.markedLayer.geoUrlLegendBiggerZoom) {
            this.http.get(this.markedLayer.geoUrlLegendBiggerZoom, { headers, responseType: 'blob' }).subscribe(
                image => {
                    this.urlMarkedLayerLegendBiggerZoom = URL.createObjectURL(image);
                }
            );
        }
    }

   
}
