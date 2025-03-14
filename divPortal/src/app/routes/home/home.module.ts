import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { SharedModule } from './../../shared/shared.module';
import { NgModule } from '@angular/core';
import { HomeComponent } from './home/home.component';
import { RouterModule } from '@angular/router';
import { HomeSidebarComponent } from './home-sidebar/home-sidebar.component';
import { BaseMapSidebarComponent } from './basemap-sidebar/basemap-sidebar.component';
import { LayersSidebarComponent } from './layers-sidebar/layers-sidebar.component';
import { EbvSidebarComponent } from './ebv-sidebar/ebv-sidebar.component';
import { NgSelectModule } from '@ng-select/ng-select';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe, SlicePipe } from '@angular/common';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { EbvDetailsComponent } from './ebv-details/ebv-details.component';
import { SiteDetailsComponent } from './site-details/site-details.component';
import { DiagramComponent } from './diagram/diagram.component';
import { DndModule } from 'ng2-dnd';
import { NgxDaterangepickerMd } from 'ngx-daterangepicker-material';
import { NgxPaginationModule } from 'ngx-pagination';
import { ShowLayaresDetailsComponent } from './show-layares-details/show-layares-details.component';
import { MalihuScrollbarModule } from 'ngx-malihu-scrollbar';
import { MetadataCatalogueComponent } from './metadata-catalogue/metadata-catalogue.component';
import { SearchSidebarComponent } from './search-sidebar/search-sidebar.component';
import { SiteDetailsListComponent } from './site-details-list/site-details-list.component';
import { DatasetDetailsListComponent } from './dataset-details-list/dataset-details-list.component';
import { DatasetDetailsComponent } from './dataset-details/dataset-details.component';
import { DiagramSidebarComponent } from './diagram-sidebar/diagram-sidebar.component';




@NgModule({
    imports: [
        SharedModule,
        CommonModule,
        NgSelectModule,
        FormsModule,
        BsDatepickerModule.forRoot(),
        DndModule.forRoot(),
        NgxDaterangepickerMd.forRoot(),
        NgxPaginationModule,
        TooltipModule,
        MalihuScrollbarModule.forRoot() ,
        RouterModule,
    ],
    declarations: [HomeComponent, HomeSidebarComponent, BaseMapSidebarComponent, LayersSidebarComponent, EbvSidebarComponent, 
        EbvDetailsComponent, SiteDetailsComponent, DiagramComponent, DiagramSidebarComponent, ShowLayaresDetailsComponent, MetadataCatalogueComponent, SearchSidebarComponent, SiteDetailsListComponent, DatasetDetailsListComponent, DatasetDetailsComponent,
        ],
    exports: [
        RouterModule,HomeSidebarComponent, EbvDetailsComponent, SiteDetailsComponent, DiagramSidebarComponent, ShowLayaresDetailsComponent, MetadataCatalogueComponent, SiteDetailsListComponent, DatasetDetailsListComponent
    ],
    providers: [ DatePipe, SlicePipe ]
})
export class HomeModule { }
