import { MetadataCatalogueComponent } from './home/metadata-catalogue/metadata-catalogue.component';
import { DiagramComponent } from './home/diagram/diagram.component';
import { HomeComponent } from './home/home/home.component';
import { Routes } from '@angular/router';
import { LayoutComponent } from '../layout/layout.component';

export const routes: Routes = [

    {
        path: '', 
        component: LayoutComponent,
        children: [
            { path: '', redirectTo: 'home', pathMatch: 'full' },
			{ path: 'home', component: HomeComponent, pathMatch: 'full' },
            { path: 'diagram', component: DiagramComponent},
            { path: 'metadata-catalogue', component: MetadataCatalogueComponent}
            // { path: '', redirectTo: 'home', pathMatch: 'full' },
            // { path: 'home', loadChildren: () => import('./home/home.module').then(m => m.HomeModule) },
            // { path: 'diagram', component: DiagramComponent}
        ]
    },

    // Not found
    { path: '**', redirectTo: 'home' }


];

