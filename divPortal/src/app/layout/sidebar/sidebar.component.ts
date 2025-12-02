import { SettingsService } from './../../core/settings/settings.service';
import { ActivationEnd, ActivationStart, NavigationEnd, NavigationStart, Router } from '@angular/router';
import { Component, OnInit, Injector, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';

@Component({
    selector: 'app-sidebar',
    templateUrl: './sidebar.component.html',
    styleUrls: ['./sidebar.component.scss'],
    standalone: false
})
export class SidebarComponent implements OnInit {
    
    showHome: boolean;
    showDiagram: boolean;


    constructor(private router: Router,
        private settings: SettingsService) {
        this.router.events.subscribe((val) => {
            if (val instanceof NavigationEnd) {
                this.showHome = val.url.indexOf('home') > -1;
                this.showDiagram = val.url.indexOf('diagram') > -1;

                if (val.url.indexOf('metadata-catalogue') > -1) {
                    if (!this.settings.getLayoutSetting('isCollapsed')) {
                        this.settings.toggleLayoutSetting('isCollapsed');
                    }                    
                }
            } 
        });
    }

    ngOnInit() {
        this.showHome = true;
    }

}
