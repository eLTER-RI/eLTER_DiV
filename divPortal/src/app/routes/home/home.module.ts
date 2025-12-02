import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { SharedModule } from './../../shared/shared.module';
import { NgModule } from '@angular/core';
import { HomeComponent } from './home/home.component';
import { RouterModule } from '@angular/router';
import { HomeSidebarComponent } from './home-sidebar/home-sidebar.component';
import { BaseMapSidebarComponent } from './basemap-sidebar/basemap-sidebar.component';
import { LayersSidebarComponent } from './layers-sidebar/layers-sidebar.component';
import { EbvSidebarComponent } from './ebv-sidebar/ebv-sidebar.component';
import { DatePipe, SlicePipe } from '@angular/common';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { EbvDetailsComponent } from './ebv-details/ebv-details.component';
import { SiteDetailsComponent } from './site-details/site-details.component';
import { DiagramComponent } from './diagram/diagram.component';
import { NgxDaterangepickerMd } from 'ngx-daterangepicker-material';
import { NgxPaginationModule } from 'ngx-pagination';
import { ShowLayaresDetailsComponent } from './show-layares-details/show-layares-details.component';
import { MetadataCatalogueComponent } from './metadata-catalogue/metadata-catalogue.component';
import { SearchSidebarComponent } from './search-sidebar/search-sidebar.component';
import { SiteDetailsListComponent } from './site-details-list/site-details-list.component';
import { DatasetDetailsListComponent } from './dataset-details-list/dataset-details-list.component';
import { DatasetDetailsComponent } from './dataset-details/dataset-details.component';
import { DiagramSidebarComponent } from './diagram-sidebar/diagram-sidebar.component';
import { StationDetailsListComponent } from './station-details-list/station-details-list.component';
import { StationDetailsComponent } from './station-details/station-details.component';
import { DatasetLayerDetailsListComponent } from './dataset-layer-details-list/dataset-layer-details-list.component';
import { DatasetLayerDetailsComponent } from './dataset-layer-details/dataset-layer-details.component';
import { LayerDatasetInfoComponent } from './dialog/layer-dataset-info/layer-dataset-info.component';


@NgModule({
    imports: [
        SharedModule,
        BsDatepickerModule.forRoot(),
        NgxDaterangepickerMd.forRoot(),
        NgxPaginationModule,
        TooltipModule,
        RouterModule
    ],
    declarations: [
        HomeComponent, 
        HomeSidebarComponent, 
        BaseMapSidebarComponent, 
        LayersSidebarComponent, 
        EbvSidebarComponent, 
        EbvDetailsComponent, 
        SiteDetailsComponent, 
        DiagramComponent, 
        DiagramSidebarComponent, 
        ShowLayaresDetailsComponent, 
        MetadataCatalogueComponent, 
        SearchSidebarComponent, 
        SiteDetailsListComponent, 
        DatasetDetailsListComponent, 
        DatasetDetailsComponent, 
        StationDetailsListComponent, 
        StationDetailsComponent, 
        DatasetLayerDetailsListComponent, 
        DatasetLayerDetailsComponent,
        LayerDatasetInfoComponent
    ],
    exports: [
        RouterModule,
        HomeSidebarComponent, 
        EbvDetailsComponent, 
        SiteDetailsComponent, 
        DiagramSidebarComponent, 
        ShowLayaresDetailsComponent, 
        MetadataCatalogueComponent, 
        SiteDetailsListComponent, 
        DatasetDetailsListComponent,
        StationDetailsListComponent,
        DatasetLayerDetailsListComponent,
        DatasetDetailsComponent,
        LayerDatasetInfoComponent
    ],
    providers: [ DatePipe, SlicePipe ]
})
export class HomeModule { }
