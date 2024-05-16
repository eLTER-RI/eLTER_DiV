import { SiteDetails } from './../../../shared/model/site-details-response-db';
import { Site } from './../../../shared/model/site-db';
import { SharedService } from './../../../shared/service/shared.service';
import { Component, Input, OnInit, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { DarDB } from 'src/app/shared/model/dar-db';

@Component({
  selector: 'app-site-details',
  templateUrl: './site-details.component.html',
  styleUrls: ['./site-details.component.scss']
})
export class SiteDetailsComponent implements OnInit, OnChanges {

  @Input() siteId: number;

  siteDetails: SiteDetails;
  darsData: DarDB[];
  menuItem: string;
  submenuItem: string;

  
 scrollbarOptions = {  theme: 'dark-thick', scrollButtons: { enable: true },  setHeight: '80vh'};

  constructor(private sharedService: SharedService) { }

  ngOnChanges(changes: SimpleChanges): void {
    this.loadSites();

    this.menuItem = 'basic';
    this.submenuItem = '';
  }

  ngOnInit(): void {

  }

  async loadSites() {
    const response = await this.sharedService.get('site/getSiteDetails?siteId='+this.siteId);
    this.siteDetails = response.entity;

    const response2 = await this.sharedService.get('dar/get?deimsUUID='+this.siteDetails.id);
    this.darsData = response2.entity;
    console.log(this.darsData);
  }

  // async loadDar() {
  //   const response = await this.sharedService.get('dar/get?deimsUUID='+this.siteDetails);
  //   console.log(response.entity);
  // }

  clickOnTab(tabName: string) {
    if (tabName === this.menuItem) {
      this.menuItem = '';
    } else {
      this.menuItem = tabName;
    }

    if (this.menuItem === 'details') {
      this.submenuItem = 'environmentalCharacteris';
    } else if (this.menuItem === 'dar') {
      this.submenuItem = this.darsData[0].id;
    } else {
      this.submenuItem = '';
    }

    // if (this.menuItem === 'satellite') {
    //   this.mScrollbarService.scrollTo('#detailsTimeseriesScroll', 'bottom', this.scrollbarOptions);
    // }
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

  hasNonNullEmail(authors: any[]): boolean {
    return authors.some(author => author.email != null);
  }

}
