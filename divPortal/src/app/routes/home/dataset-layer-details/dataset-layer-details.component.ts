import { Component, Input, OnInit } from '@angular/core';
import { DatasetLayer } from 'src/app/shared/model/dataset-layer';

@Component({
  selector: 'app-dataset-layer-details',
  templateUrl: './dataset-layer-details.component.html',
  styleUrls: ['./dataset-layer-details.component.scss']
})
export class DatasetLayerDetailsComponent implements OnInit {

  @Input() datasetLayerDetail: DatasetLayer;
  @Input() open: boolean;


  constructor() { }

  ngOnInit(): void {
  }

}
