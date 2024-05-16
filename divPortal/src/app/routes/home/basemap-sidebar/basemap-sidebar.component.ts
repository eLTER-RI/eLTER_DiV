import { Router, NavigationEnd } from '@angular/router';
import { HomeService } from './../home/home.service';
import { BaseMapSideBarService } from './basemap-sidebar.service';
import { Layer } from '../../../shared/model/layer';
import { Component, OnInit, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-basemap-sidebar',
  templateUrl: './basemap-sidebar.component.html',
  styleUrls: ['./basemap-sidebar.component.scss']
})
export class BaseMapSidebarComponent implements OnInit, OnDestroy {

  basemaps: Layer[] = [];
  chosenBaseMap: Layer;
  showBaseMaps: boolean;

  
  constructor(private basemapSrvice: BaseMapSideBarService,
              private homeService: HomeService,
              private router: Router) {
   
  }

  ngOnInit(): void {
    this.readBasemaps();
  }

  async readBasemaps() {
    const response = await this.basemapSrvice.getBaselayers(['base']);
    this.basemaps = response.entity;

    this.chooseBaseMap(this.basemaps[0]);

  }

  chooseBaseMap(basemap: Layer): void {
    const action = {
      action: 'changeBaseMap',
      basemap: basemap
    }
    this.homeService.changeBaseMap(action);
    
    this.chosenBaseMap = basemap;
  }

  ngOnDestroy() {
  }


}
