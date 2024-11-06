import { Component, Input, OnInit } from '@angular/core';
import { PageChangedEvent } from 'ngx-bootstrap/pagination';
import { Subscription } from 'rxjs';
import { Page } from 'src/app/shared/model/Page';
import { Dataset } from 'src/app/shared/model/dataset';
import { DatasetDetailsListService } from './dataset-details-list.service';

@Component({
  selector: 'app-dataset-details-list',
  templateUrl: './dataset-details-list.component.html',
  styleUrls: ['./dataset-details-list.component.scss']
})
export class DatasetDetailsListComponent implements OnInit {

  @Input() datasets: Dataset[];
  @Input() page: Page;

  paginationRefreshSubscription: Subscription; 

  constructor(private datasetDetailsListService: DatasetDetailsListService) { }

  ngOnInit(): void {
  
  }

  pageChanged(event: PageChangedEvent): void {
    this.page.currentPage = event.page;
    this.datasets = null;
    this.datasetDetailsListService.paginationRefresh({page: this.page});
	}

}
