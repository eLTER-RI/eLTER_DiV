import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-metadata-catalogue',
  templateUrl: './metadata-catalogue.component.html',
  styleUrls: ['./metadata-catalogue.component.scss']
})
export class MetadataCatalogueComponent implements OnInit {

  isLoading: boolean = true;

   metadataURL = "https://dip.lter-europe.net/geonetwork/srv/eng/catalog.search#/search";
  // metadataURL = "https://www.youtube.com/embed/p485kUNpPvE&t=1s";

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
