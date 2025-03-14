import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { Subscription } from 'rxjs';
import { OffsidebarService } from 'src/app/layout/offsidebar/offsidebar.service';
import { Dataset } from 'src/app/shared/model/dataset';
import { HomeService } from '../home/home.service';
import { SettingsService } from 'src/app/core/settings/settings.service';
import { DatasetDetailsListService } from '../dataset-details-list/dataset-details-list.service';

@Component({
  selector: 'app-dataset-details',
  templateUrl: './dataset-details.component.html',
  styleUrls: ['./dataset-details.component.scss']
})
export class DatasetDetailsComponent implements OnInit {

  @Input() datasetDetail: Dataset;
  @Input() open: boolean;

  pinActive: boolean = false;

  menuItem: string;
  submenuItem: string;
  subSubmenuItem: string;

  datasetOpenDetailsSubscription: Subscription;
  closeAllDatasetDetailsSubscription: Subscription;
  pinClickedSubscription: Subscription;

  scrollbarOptions = {  theme: 'dark-thick', scrollButtons: { enable: true },  setHeight: '70vh'};

  constructor(private offsidebarService: OffsidebarService,
              private homeService: HomeService,
              private settings: SettingsService,
              private datasetDetailsListService: DatasetDetailsListService) { }

  ngOnChanges(changes: SimpleChanges): void {
    this.menuItem = 'basic';
    this.submenuItem = '';
    this.subSubmenuItem = '';
  }

  ngOnInit(): void {
    this.initSubscriptions();
  }


  initSubscriptions() {
    this.datasetOpenDetailsSubscription = this.offsidebarService.currOpenDataset.subscribe( event => {
      if (event && event.action == 'openDataset') {
        let siteIds = this.datasetDetail.sites?.map(site => site.id);
        if (siteIds.includes(event.sites[0])) {
          if (this.datasetDetail) {
            this.datasetDetail.open = true;
          }
        } else {
          if (this.datasetDetail) {
            this.datasetDetail.open = false;
          }
        }
      }
    });

    this.closeAllDatasetDetailsSubscription = this.offsidebarService.closeAllDatasetDetailsObservable.subscribe( obj => {
      if (this.datasetDetail) {
        this.datasetDetail.open = false;
      }
    });

    this.pinClickedSubscription = this.datasetDetailsListService.pinClickedtbservable.subscribe( obj => {
      if (obj?.type != 'dataset' || obj?.id != this.datasetDetail.id) {
        this.pinActive = false;
      }
    });
  }

  openCloseDatasetDetail() {
    this.datasetDetail.open = !this.datasetDetail.open;

    let idSites = [];
    idSites = this.datasetDetail.sites?.map(site => site.id);

    this.homeService.markSitesOnMap(idSites, this.datasetDetail.open);
  }

  clickOnTab(tabName: string) {
    if (tabName === this.menuItem) {
      this.menuItem = '';
    } else {
      this.menuItem = tabName;
    }

    this.submenuItem = '';
    this.subSubmenuItem = '';
  }

  clickOnTabSubmenu(tabName: string) {
    if (tabName === this.submenuItem) {
      this.submenuItem = '';
    } else {
      this.submenuItem = tabName;
    }
  }

  clickOnTabSubSubmenu(tabName: string) {
    if (tabName === this.subSubmenuItem) {
      this.subSubmenuItem = '';
    } else {
      this.subSubmenuItem = tabName;
    }
  }


  pinClick() {
    this.pinActive = !this.pinActive;

    this.datasetDetailsListService.pinClicked({ type: 'dataset', id: this.datasetDetail.id });

    if (this.pinActive) {
      this.homeService.actionChanged({
        action: 'removeSitePolygon'
      });

      if (this.datasetDetail.sites?.length > 0) {
        let siteIds = this.datasetDetail.sites.map(site => site.id);
        this.homeService.markSitesOnMap(siteIds, true);
      }

      this.openOffsidebar();
    }
  }

  navigateToExternal(url: string) {
    if (url?.length > 0) {
      window.open(url, '_blank');
    }
  }

  openOffsidebar(){
    if (this.settings.getLayoutSetting('offsidebarOpen')) {
        this.settings.toggleLayoutSetting('offsidebarOpen');
    }
  }

}
