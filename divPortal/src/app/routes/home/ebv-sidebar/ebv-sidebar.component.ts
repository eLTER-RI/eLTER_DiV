import { SettingsService } from './../../../core/settings/settings.service';
import { EbvDB } from './../../../shared/model/ebv-db';
import { EbvDetail } from './../../../shared/model/ebvdetail';
import { OffsidebarService } from './../../../layout/offsidebar/offsidebar.service';
import { SpatialScope } from './../../../shared/model/spatial-scope';
import { EbvFilter } from './../../../shared/model/ebv-filter';
import { EbvSidebarService } from './ebv-sidebar.service';
import { EbvName } from './../../../shared/model/ebv-name';
import { EntityType } from './../../../shared/model/entity-type';
import { EbvClass } from './../../../shared/model/ebv-class';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-ebv-sidebar',
  templateUrl: './ebv-sidebar.component.html',
  styleUrls: ['./ebv-sidebar.component.scss']
})
export class EbvSidebarComponent implements OnInit, OnDestroy {

  showEbv: boolean;

  
  ebvFilter: EbvFilter;
  ebvs: EbvDB[];

  years: number[];
  ebvClasses: EbvClass[];
  spatialScopes: SpatialScope[];
  entityTypes: EntityType[];
  ebvNames: EbvName[];
  creators: string[];

  constructor(private ebvService: EbvSidebarService,
              private offsidebarService: OffsidebarService,
              public settings: SettingsService) { }

  ngOnInit(): void {
    this.ebvFilter = new EbvFilter();

    this.readCodebook();
    this.loadYerars();
  }

  ngOnDestroy() {

  }

  loadYerars() {
    const curretYear = new Date().getFullYear();
    this.years = [];
    for (let i = curretYear; i >= 2010; i--) {
      this.years.push(i);
    }
  }

  async readCodebook() {
    const response = await this.ebvService.getCodebook(['ebvClass', 'spatialScope', 'entityType', 'creator']);
    const ebvCoodebok = response.entity;
    this.ebvClasses = ebvCoodebok[`ebvClass`];
    this.spatialScopes = ebvCoodebok[`spatialScope`];
    this.entityTypes = ebvCoodebok[`entityType`];
    this.creators = ebvCoodebok[`creator`];
  }

  async btn_fiter() {
    const response = await this.ebvService.filter(this.ebvFilter);
    this.ebvs = response.entity;

    this.offsidebarService.showEbvs({
        ebvs: this.ebvs,
        action: 'showEBVs'
      });

    if (!this.settings.getLayoutSetting('offsidebarOpen')) {
      this.settings.toggleLayoutSetting('offsidebarOpen');
    }
  }

  async ebvClassChanged() {
    if (this.ebvFilter.ebvClass) {
      const response = await this.ebvService.getEbvName(this.ebvFilter.ebvClass.id);
      this.ebvNames = response.entity;
    }
  }


}
