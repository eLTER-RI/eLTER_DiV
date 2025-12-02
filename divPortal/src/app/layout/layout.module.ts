import { NgModule } from '@angular/core';
import { LayoutComponent } from './layout.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { HeaderComponent } from './header/header.component';
import { NavsearchComponent } from './header/navsearch/navsearch.component';
import { OffsidebarComponent } from './offsidebar/offsidebar.component';
import { FooterComponent } from './footer/footer.component';
import { AppOverlayComponent } from './app-overlay/app-overlay.component';
import { SharedModule } from '../shared/shared.module';
import { HomeModule } from '../routes/home/home.module';
import { DiagramSidebarComponent } from '../routes/home/diagram-sidebar/diagram-sidebar.component';


@NgModule({
  imports: [SharedModule, HomeModule],
  providers: [],
  declarations: [
    LayoutComponent,
    SidebarComponent,
    HeaderComponent,
    NavsearchComponent,
    OffsidebarComponent,
    FooterComponent,
    AppOverlayComponent,
  ],
  exports: [
    LayoutComponent,
    SidebarComponent,
    HeaderComponent,
    NavsearchComponent,
    OffsidebarComponent,
    FooterComponent,
    AppOverlayComponent,
  ],
})
export class LayoutModule {}
