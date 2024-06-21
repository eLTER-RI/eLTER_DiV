import { Component, OnInit } from '@angular/core';
import { DivFilter } from 'src/app/shared/model/div-filter';
import { Habitat } from 'src/app/shared/model/habitat';
import { Site } from 'src/app/shared/model/site-db';
import { StandardObservation } from 'src/app/shared/model/standard-observation';

@Component({
  selector: 'app-search-sidebar',
  templateUrl: './search-sidebar.component.html',
  styleUrls: ['./search-sidebar.component.scss']
})
export class SearchSidebarComponent implements OnInit {

  showDivFilter: boolean;

  divFilter: DivFilter;

  sites: Site[];
  standardObservations: StandardObservation[];
  habitats: Habitat[];

  constructor() { }

  ngOnInit(): void {
    this.divFilter = new DivFilter();
    this.showDivFilter = true;
  }

  async btn_filter() {
    // const response = await this..filter(this.ebvFilter);
    // this.ebvs = response.entity;

    // this.offsidebarService.showEbvs({
    //     ebvs: this.ebvs,
    //     action: 'showEBVs'
    //   });

    // if (!this.settings.getLayoutSetting('offsidebarOpen')) {
    //   this.settings.toggleLayoutSetting('offsidebarOpen');
    // }
  }



}
