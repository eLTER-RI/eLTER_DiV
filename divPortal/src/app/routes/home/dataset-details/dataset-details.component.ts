import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { Subscription } from 'rxjs';
import { OffsidebarService } from 'src/app/layout/offsidebar/offsidebar.service';
import { Dataset } from 'src/app/shared/model/dataset';
import { SharedService } from 'src/app/shared/service/shared.service';
import { HomeService } from '../home/home.service';
import { ToastrService } from 'ngx-toastr';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-dataset-details',
  templateUrl: './dataset-details.component.html',
  styleUrls: ['./dataset-details.component.scss']
})
export class DatasetDetailsComponent implements OnInit {

  @Input() datasetDetail: Dataset;
  @Input() open: boolean;

  menuItem: string;
  submenuItem: string;

  datasetOpenDetailsSubscription: Subscription;
  closeAllDatasetDetailsSubscription: Subscription;

  scrollbarOptions = {  theme: 'dark-thick', scrollButtons: { enable: true },  setHeight: '70vh'};

  constructor(private offsidebarService: OffsidebarService,
              private homeService: HomeService) { }

  ngOnChanges(changes: SimpleChanges): void {
    this.menuItem = 'basic';
    this.submenuItem = '';
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

    this.submenuItem = ''
  }

  clickOnTabSubmenu(tabName: string) {
    if (tabName === this.submenuItem) {
      this.submenuItem = '';
    } else {
      this.submenuItem = tabName;
    }
  }

}
