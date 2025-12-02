import { Component, OnInit } from '@angular/core';

@Component({
    selector: 'app-metadata-catalogue',
    templateUrl: './metadata-catalogue.component.html',
    styleUrls: ['./metadata-catalogue.component.scss'],
    standalone: false
})
export class MetadataCatalogueComponent implements OnInit {

  isLoading: boolean = true;

   metadataURL = "https://div.elter-ri.eu/geonetwork/srv/eng/catalog.search#/search";

  divHeight;

  constructor() {
    setTimeout(() => {
      this.isLoading = false;
    }, 1500);
   }

  ngOnInit(): void {
    this.divHeight = window.innerHeight - 115;
  }

}
