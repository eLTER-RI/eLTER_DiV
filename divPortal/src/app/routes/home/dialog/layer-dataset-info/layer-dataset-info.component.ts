import { Component, OnInit } from '@angular/core';
import { BsModalRef } from 'ngx-bootstrap/modal';
import { Subject } from 'rxjs';
import { Dataset } from 'src/app/shared/model/dataset';
import { Layer } from 'src/app/shared/model/layer';
import { SharedService } from 'src/app/shared/service/shared.service';

@Component({
    selector: 'app-layer-dataset-info',
    templateUrl: './layer-dataset-info.component.html',
    styleUrls: ['./layer-dataset-info.component.scss'],
    standalone: false
})
export class LayerDatasetInfoComponent implements OnInit {

  public onClose: Subject<string>;

  layer: Layer;

  dataset: Dataset = null;

  constructor(public modalRef: BsModalRef,
              private sharedService: SharedService) { }

  ngOnInit(): void {
    this.readDatasetForLayer(this.layer);
  
    this.onClose = new Subject();
  }

  async readDatasetForLayer(layer: Layer) {
    const response = this.sharedService.get("dataset/getForLayer?layerId=" + layer.id);
    
    this.dataset = (await response).entity;
  }

  close(result: string) {
    this.onClose.next(result);
    this.modalRef.hide();
  }

}
