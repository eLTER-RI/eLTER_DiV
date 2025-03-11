import { SiteDetails } from './../../../shared/model/site-details-response-db';
import { SharedService } from './../../../shared/service/shared.service';
import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { OffsidebarService } from 'src/app/layout/offsidebar/offsidebar.service';
import { Subscription } from 'rxjs';
import { HomeService } from '../home/home.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';
import { SettingsService } from 'src/app/core/settings/settings.service';
import { DatasetDetailsListService } from '../dataset-details-list/dataset-details-list.service';

@Component({
  selector: 'app-site-details',
  templateUrl: './site-details.component.html',
  styleUrls: ['./site-details.component.scss']
})
export class SiteDetailsComponent implements OnInit, OnChanges {

  @Input() siteId: number;
  @Input() open: boolean;

  pinActive: boolean = false;

  siteDetails: SiteDetails;
  menuItem: string;
  submenuItem: string;

  siteOpenDetailsSubscription: Subscription;
  closeAllSiteDetailsSubscription: Subscription;
  pinClickedSubscription: Subscription;

  scrollbarOptions = {  theme: 'dark-thick', scrollButtons: { enable: true },  setHeight: '80vh'};

  constructor(private sharedService: SharedService,
              private offsidebarService: OffsidebarService,
              private homeService: HomeService,
              private toastrService: ToastrService,
              private translateService: TranslateService,
              private settings: SettingsService,
              private datasetDetailsListService: DatasetDetailsListService) { }

  ngOnChanges(changes: SimpleChanges): void {
    this.loadSites();

    this.menuItem = 'basic';
    this.submenuItem = '';
  }

  ngOnInit(): void {
    this.siteOpenDetailsSubscription = this.offsidebarService.currOpenSite.subscribe( site => {
      if (site && site.action == 'openSite') {
        if (site.sites[0] == this.siteId) {
          if (this.siteDetails) {
            this.siteDetails.open = true;
            this.open = true;
          }
        } else {
          if (this.siteDetails) {
            this.siteDetails.open = false;
            this.open = false;
          }
        }
      }
    });

    this.closeAllSiteDetailsSubscription = this.offsidebarService.closeAllSiteDetailsObservable.subscribe( obj => {
      if (this.siteDetails) {
        this.siteDetails.open = false;
      }
    });

    this.pinClickedSubscription = this.datasetDetailsListService.pinClickedtbservable.subscribe( obj => {
      if (obj?.type != 'site' || obj?.id != this.siteDetails?.id) {
        if (this.siteDetails) {
          this.pinActive = false;
        }
      }
    });
    
  }

  async loadSites() {
    const response = await this.sharedService.get('site/getSiteDetails?siteId='+this.siteId);
    if (response.status != 200) {
      this.toastrService.error(this.translateService.instant('exception.' + response.entity.status + ''), "Error"); 
    } else {
      this.siteDetails = response.entity;
      this.siteDetails.open = this.open;
    }
  }

  clickOnTab(tabName: string) {
    if (tabName === this.menuItem) {
      this.menuItem = '';
    } else {
      this.menuItem = tabName;
    }

    if (this.menuItem === 'details') {
      this.submenuItem = 'environmentalCharacteris';
    } else {
      this.submenuItem = '';
    }

    // if (this.menuItem === 'satellite') {
    //   this.mScrollbarService.scrollTo('#detailsTimeseriesScroll', 'bottom', this.scrollbarOptions);
    // }
  }

  openCloseSiteDetail() {
    this.siteDetails.open = !this.siteDetails.open;

    this.homeService.markSitesOnMap([this.siteId], this.siteDetails.open);
  }

  showOperationTab() {
    return  this.siteDetails.infrastructure.accessibleAllYear != null ||
            this.siteDetails.infrastructure.allPartsAccessible != null ||
            this.siteDetails.infrastructure.accessType != null ||
            this.siteDetails.infrastructure.operationPermanent != null ||
            this.siteDetails.infrastructure.notes != null ||
            this.siteDetails.infrastructure.operationSiteVisitInterval != null ||
            this.siteDetails.infrastructure.maintenanceInterval != null;
}

showEquipmentTab() {
  return  this.siteDetails.infrastructure.permanentPowerSupply != null ||
          this.siteDetails.infrastructure.notes != null ||
          this.siteDetails.infrastructure.collections != null;
}

showDesignTab() {
  return this.siteDetails.observationsDesign != null ||
          this.siteDetails.observationsScale != null ||
          this.siteDetails.experimentsDesign != null ||
          this.siteDetails.experimentsScale != null ||
          this.siteDetails.siteType != null;
}

showPolicyTab() {
  return this.siteDetails.infrastructure.policy.notes != null ||
          this.siteDetails.infrastructure.policy.url != null ||
          this.siteDetails.infrastructure.policy.rights;
}


  clickOnTabSubmenu(tabName: string) {
    if (tabName === this.submenuItem) {
      this.submenuItem = '';
    } else {
      this.submenuItem = tabName;
    }
  }

  pinClick() {
    this.pinActive = !this.pinActive;

    this.datasetDetailsListService.pinClicked({ type: 'site', id: this.siteDetails.id })

    if (this.pinActive) {
      this.homeService.actionChanged({
        action: 'removeSitePolygon'
      });
      this.homeService.markSitesOnMap([this.siteId], true);

      this.openOffsidebar();
    }
  }

  openOffsidebar(){
    if (this.settings.getLayoutSetting('offsidebarOpen')) {
        this.settings.toggleLayoutSetting('offsidebarOpen');
    }
  }

}
