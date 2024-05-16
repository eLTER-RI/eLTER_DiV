import { CapabilitiesRequest } from './../../../shared/model/capabilities-request-db';
import { SettingsService } from './../../../core/settings/settings.service';
import { OffsidebarService } from './../../../layout/offsidebar/offsidebar.service';
import { SharedService } from './../../../shared/service/shared.service';
import { Layer } from 'src/app/shared/model/layer';
import { LayerGroup } from './../../../shared/model/layer-group';
import { Component, Input, OnInit } from '@angular/core';
import WMSCapabilities from 'ol/format/WMSCapabilities';
import _, { add } from 'lodash';
import { ToastrService } from 'ngx-toastr';
import * as uuid from 'uuid';

@Component({
  selector: 'app-show-layares-details',
  templateUrl: './show-layares-details.component.html',
  styleUrls: ['./show-layares-details.component.scss']
})
export class ShowLayaresDetailsComponent implements OnInit {

  @Input() selectedLayers: Layer[]; //selektovani iz sesije
  
  filter: string;

  layerGroups: LayerGroup[];
  layerGroupsOrig: LayerGroup[];
  layersToSave: Layer[];

  showAddNewLayer: boolean;
  capabilitiesRequest: CapabilitiesRequest;
  fromGetCapabilitiesLayers: Layer[];
  fromGetCapabilitiesLayersOrig: Layer[];
  capabilitiesLayersToSave: Layer[];

  myLayersSession: Layer[];

  showPassword: boolean;

  addAuthenticationGetCap: boolean;
  
 scrollbarOptions = {  theme: 'dark-thick', scrollButtons: { enable: true },  setHeight: '70vh'};

  constructor(private sharedService: SharedService,
              private offsidebarService: OffsidebarService,
              private settings: SettingsService,
              private toastrService: ToastrService) { }

  ngOnInit(): void {
    this.capabilitiesRequest = new CapabilitiesRequest();
    this.layersToSave = [];
    this.capabilitiesLayersToSave = [];

    this.loadLayerGroups();

    const layers = sessionStorage.getItem('mylayersFromCapabilities');
    if (layers !== null && layers != 'undefined' && layers != undefined) {
      this.myLayersSession = JSON.parse(layers);
    }
  }

  async loadLayerGroups() {
    const params = ['selection'].join();
    const response =  this.sharedService.get('layer/getLayerGroups?layertype=' + params);
    this.layerGroups = (await response).entity;
    
    const layers = sessionStorage.getItem('mylayersFromCapabilities');
    if (layers !== null && layers != 'undefined' && layers != undefined) {
      let layerGroupMyLayer = this.createMyLayers(JSON.parse(layers));
      this.layerGroups.push(layerGroupMyLayer);
    }

    this.layerGroupsOrig = [...this.layerGroups];

    
    if (this.selectedLayers != null) {
      this.layerGroups.forEach( lg => {
        lg.layers.forEach( layer => {
          const selected = this.selectedLayers.find( l => layer.layerType != 'mylayer' ? l.id == layer.id : layer.idHash == l.idHash );
          if (selected) {
            layer.selected = true;
            this.layersToSave.push(layer);
          } else {
            layer.selected = false;
          }
        });
      });
    }
  }

  createMyLayers(layers) {
      let layerGroupMyLayer = new LayerGroup();
      layerGroupMyLayer.iconClass = '';
      layerGroupMyLayer.name = 'My layers';
    
      layerGroupMyLayer.layers = JSON.parse(JSON.stringify(layers));

      return layerGroupMyLayer;
  }

  async openLayer(indexOfGroup: number) {
    this.layerGroups[indexOfGroup].opened = !this.layerGroups[indexOfGroup].opened;
  }

  selectLayer(indexOfLayer: number, indexOfGroup: number, type: string, selectValue: boolean) {
    let clickedLayer;
    if (type == 'selection') {
      clickedLayer = this.layerGroups[indexOfGroup].layers[indexOfLayer];
    } else if (type == 'capabilities') {
      clickedLayer = this.fromGetCapabilitiesLayers[indexOfLayer];
    } 

    if (clickedLayer.selected != selectValue) {
      clickedLayer.selected = selectValue;

      if (clickedLayer.selected && type === 'capabilities') {
        this.capabilitiesLayersToSave.push(clickedLayer);
      } else if (!clickedLayer.selected && type === 'capabilities') {
        const indexToRemove = this.capabilitiesLayersToSave.findIndex( l =>  l.id ?  l.id == clickedLayer.id : l.idHash == clickedLayer.idHash);
        this.capabilitiesLayersToSave.splice(indexToRemove, 1);
      } else if (clickedLayer.selected && type == 'selection') {
        this.layersToSave.push(clickedLayer);
      } else if (!clickedLayer.selected && type == 'selection') {
        const indexToRemove = this.layersToSave.findIndex( l => l.id ?  l.id == clickedLayer.id : l.idHash == clickedLayer.idHash);
        this.layersToSave.splice(indexToRemove, 1);
      }
    }

  }

  
  onFilter(): void {  
    const search = this.filter.toLowerCase().trim();
    if (!search || search.length === 0) {
      console.log(this.layerGroupsOrig)
      this.layerGroups = [...this.layerGroupsOrig];

      if (this.fromGetCapabilitiesLayers) {
        this.fromGetCapabilitiesLayers = [...this.fromGetCapabilitiesLayersOrig];
      }
    } else {
      
      //filter for layers from database
      this.layerGroups = [];
      for (const lg of this.layerGroupsOrig) {
        for (const layer of lg.layers) {
          if (this.doesContains(layer, search)) {

            if (this.layerGroups.find(el => el.id == lg.id) == undefined) {
              const layerGroup = new LayerGroup();
              layerGroup.id = lg.id;
              layerGroup.iconClass = lg.iconClass;
              layerGroup.name = lg.name;
              layerGroup.layers = [];
              layerGroup.opened = true;
              layerGroup.layers.push(layer);
              this.layerGroups.push(layerGroup);
            } else {
              this.layerGroups.find(el => el.id == lg.id)?.layers.push(layer);
            }
          }
        }
      }

      //filter for layers from get capabilities link
      if (this.fromGetCapabilitiesLayers) {
        this.fromGetCapabilitiesLayers = [];
        for(const layer of this.fromGetCapabilitiesLayersOrig) {
          if (this.doesContains(layer, search)) {
            if (!this.showAddNewLayer) {
              this.showAddNewLayer = true;
            }

            this.fromGetCapabilitiesLayers.push(layer);
          }
        }
      }
    }
  }

  doesContains(layer: Layer, text: string): boolean {
    let result = false;

    if (layer.name.toLowerCase().indexOf(text) > -1) {
      result = true;
    }

    return result;
  }


 
  save() {
    if (this.capabilitiesLayersToSave && this.capabilitiesLayersToSave.length > 0) {
      if (!this.myLayersSession) {
        this.myLayersSession = [];
      }

      const capToSave = JSON.parse(JSON.stringify(this.capabilitiesLayersToSave));
      this.layersToSave = this.layersToSave.concat(capToSave); //ovo moram dodati tu da pamtim

      this.myLayersSession = this.myLayersSession.concat(capToSave);          //nemoguce da ima duplikate jer se dodaje prvi put

      if (this.filter) {
        this.layerGroups = [...this.layerGroupsOrig];
        this.filter = '';
      }

      const addMyLayers = this.layerGroups[this.layerGroups.length - 1]?.name != 'My layers';
      if (addMyLayers) {
        let layerGroupMyLayer = this.createMyLayers(this.myLayersSession);
        this.layerGroups.push(layerGroupMyLayer);
      } else {
        this.layerGroups[this.layerGroups.length - 1].layers = this.myLayersSession;
      }
  
      this.layerGroupsOrig = [...this.layerGroups];


      sessionStorage.setItem('mylayersFromCapabilities', JSON.stringify(this.myLayersSession));

      this.fromGetCapabilitiesLayers = [];
      this.capabilitiesLayersToSave = [];
      this.capabilitiesRequest = new CapabilitiesRequest();
    }

    this.offsidebarService.addSelectedLayers({
      action: 'addSelectedLayers',
      layers: this.layersToSave
    });
  }



  getLayersFromUrl() {
    var parser = new WMSCapabilities();
    this.fromGetCapabilitiesLayers = [];
    const geoUrlWms = this.capabilitiesRequest.url.substring(0, this.capabilitiesRequest.url.indexOf('?') + 1);

    const position = this.capabilitiesRequest.url.indexOf('//') + 2;
    var url;

    const isHttpsAuth = this.capabilitiesRequest.url.indexOf('https') > -1 && this.addAuthenticationGetCap;

    if (this.addAuthenticationGetCap && !isHttpsAuth) {
      url = [this.capabilitiesRequest.url.slice(0, position),
                 this.capabilitiesRequest.authName + ':' + this.capabilitiesRequest.authPassword + '@' ,
                 this.capabilitiesRequest.url.slice(position)].join('');
    } else {
      url = this.capabilitiesRequest.url;
    }

    let headers = new Headers();
    if (isHttpsAuth && this.addAuthenticationGetCap) {
      headers.set('Authorization', 'Basic ' + btoa( this.capabilitiesRequest.authName + ":" + this.capabilitiesRequest.authPassword));
    }

    fetch(url, {method:'GET',
                headers: headers,
    }).then(function(response) {
        return response.text();
      }).then(text => {
        var result = parser.read(text);
        const layers = result.Capability.Layer.Layer;
        layers?.forEach(layer => {
          const capLayer = new Layer();
          capLayer.name = layer.Title;
          capLayer.layerType = 'mylayer';
          capLayer.layerName = layer.Name;
          capLayer.geoUrlWms = geoUrlWms;
          capLayer.selected = false;
          capLayer.idHash = uuid.v4();

          if (layer?.Dimension && layer?.Dimension?.length > 0 && layer?.Dimension[0]?.values) {
            capLayer.times = layer.Dimension[0].values.split(",");
          }

          if (layer.Style && layer?.Style?.length > 0 &&  layer?.Style[0]?.LegendURL && layer?.Style[0]?.LegendURL?.length > 0) {
            capLayer.geoUrlLegend = layer?.Style[0]?.LegendURL[0].OnlineResource;
          }

          if (isHttpsAuth && this.capabilitiesRequest.authPassword && this.capabilitiesRequest.authName) {
            capLayer.authPassword = this.capabilitiesRequest.authPassword;
            capLayer.authUsername = this.capabilitiesRequest.authName;
          }

          this.fromGetCapabilitiesLayers.push(capLayer);
        });

        this.fromGetCapabilitiesLayersOrig = [...this.fromGetCapabilitiesLayers];
      }).catch(error => {  
        this.toastrService.error('Please enter a valid URL or username/password.', 'Oops, something has gone wrong.'); 
      });
  }

  isDisabledFetch() {
    if (!this.capabilitiesRequest.url) {
      return true;
    }

    if (this.addAuthenticationGetCap) {
      if (!(this.capabilitiesRequest.authName && this.capabilitiesRequest.authPassword)) {
        return true;
      }
    }

    return false;
  }

  selectAll(selectAll: boolean, indexGroup: number, typeOfLayers: string) { 
    if (typeOfLayers == 'selection') {
      this.layerGroups[indexGroup].layers.forEach((layer, j) => {
        this.selectLayer(j, indexGroup, typeOfLayers, selectAll);
      });
    } else {
      this.fromGetCapabilitiesLayers.forEach((layer, j) => {
        this.selectLayer(j, indexGroup, typeOfLayers, selectAll);
      });
    }
  }

  getTypeForShowPassword(){
    if (this.showPassword) {
      return 'text';
    } else {
      return 'password';
    }
  }

  changeShowPassword() {
    this.showPassword = !this.showPassword;
  }

  addAuthenticationGetCapClicked() {
    if (!this.addAuthenticationGetCap) {
      delete this.capabilitiesRequest.authName;
      delete this.capabilitiesRequest.authPassword;
    }
  }

}
