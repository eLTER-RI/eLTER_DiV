import { EbvDB } from './../../../shared/model/ebv-db';
import { EbvDetail } from './../../../shared/model/ebvdetail';
import { Component, Input, OnInit } from '@angular/core';
import { EbvDetailsService } from './ebv-details.service';

@Component({
  selector: 'app-ebv-details',
  templateUrl: './ebv-details.component.html',
  styleUrls: ['./ebv-details.component.scss']
})
export class EbvDetailsComponent implements OnInit {

  @Input() ebvs: EbvDB[];

  scrollbarOptions = {  theme: 'dark-thick', scrollButtons: { enable: true },  setHeight: '85vh'};

  
  openEbv: boolean[];
  ebvDetails: EbvDetail[];

  constructor(private ebvService: EbvDetailsService) { }

  ngOnInit(): void {
    this.openEbv = [];
    this.ebvDetails = [];
    this.resetOpenEbv();
  }

  resetOpenEbv() {
    for(let i = 0; i < this.ebvs?.length -1; i++ ) {
      this.openEbv[i] = false;
    }
  }

  async loadEbvDetails(index: number) {
    const response = await this.ebvService.getDetail(this.ebvs[index].ebvId);
    this.ebvDetails[index] = response.entity;
  }

  async clickOnTab(index: number) {
    this.openEbv[index] = !this.openEbv[index];

    if (this.openEbv[index] && 
      (this.ebvDetails[index] == null || this.ebvDetails[index] == undefined)) {
        this.loadEbvDetails(index);
    }
  }

}
