import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TranslatorService } from '../core/translator/translator.service';
import { MenuService } from '../core/menu/menu.service';
import { FormsModule } from '@angular/forms';
import { CommonModule} from '@angular/common';
import { menu } from './menu';
import { routes } from './routes';
import { SharedModule } from '../shared/shared.module';

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forRoot(routes, { useHash: true }),
        FormsModule,
    ],
    declarations: [
  ],
    exports: [
        RouterModule
    ]
})

export class RoutesModule {
    constructor(public menuService: MenuService, tr: TranslatorService) {
        menuService.addMenu(menu);
    }
}
