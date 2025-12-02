import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { PageChangedEvent } from 'ngx-bootstrap/pagination';
import { Subscription } from 'rxjs';
import { OffsidebarService } from 'src/app/layout/offsidebar/offsidebar.service';
import { DatasetLayer } from 'src/app/shared/model/dataset-layer';
import { Page } from 'src/app/shared/model/Page';
import { DatasetLayerDetailsListService } from './dataset-layer-details-list.service';

@Component({
    selector: 'app-dataset-layer-details-list',
    templateUrl: './dataset-layer-details-list.component.html',
    styleUrls: ['./dataset-layer-details-list.component.scss'],
    standalone: false
})
export class DatasetLayerDetailsListComponent implements OnInit, OnChanges, OnDestroy {

  @Input() datasetLayers: DatasetLayer[];

  page: Page;

  datasetLayerCheckSubscription: Subscription;

  constructor(private offsidebarService: OffsidebarService,
              private datasetLayerDetailsListService: DatasetLayerDetailsListService
  ) { }

  ngOnInit(): void {
    this.initSubscriptions();
    this.paginationInit();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['datasetLayers'] && this.datasetLayers) {
      this.paginationInit();
    }
  }

  initSubscriptions() {
    this.datasetLayerCheckSubscription = this.datasetLayerDetailsListService.checkDatasetLayerEvent.subscribe(layer => { 
      if (layer !== null) {
        this.checkDatasetLayer();
      }
    });
  }

  checkDatasetLayer() {
    const allCheckedLayers = this.datasetLayers.filter(dl => dl.dataset.datasetLayerChecked === true).map(dl => dl.layer);
    this.offsidebarService.addSelectedLayers({
      action: 'addSelectedLayers',
      layers: allCheckedLayers,
      closeOffsidebar: false
    });
  }

  ngOnDestroy(): void {
    if (this.datasetLayerCheckSubscription) {
      this.datasetLayerCheckSubscription.unsubscribe();
    }
  }

  paginationInit() {
    this.page = new Page();
    this.page.size = 15;
    this.page.totalElements = this.datasetLayers?.length;
    this.page.totalPages = Math.floor(this.page.totalElements / this.page.size) + 1;
    this.page.currentPage = 1;
  }

  pageChanged(event: PageChangedEvent): void {
      this.page.currentPage = event.page;
  }

}
