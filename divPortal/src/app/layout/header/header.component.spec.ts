/* tslint:disable:no-unused-variable */

import { Injector } from '@angular/core';
import { TranslateService, TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpClient, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { TestBed, async, inject } from '@angular/core/testing';
import { HeaderComponent } from './header.component';

import { SettingsService } from '../../core/settings/settings.service';
import { MenuService } from '../../core/menu/menu.service';
import { TranslatorService } from '../../core/translator/translator.service';
import { createTranslateLoader } from '../../app.module';

describe('Component: Header', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
    imports: [TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: createTranslateLoader,
                deps: [HttpClient]
            }
        })],
    providers: [MenuService, SettingsService, TranslatorService, Injector, provideHttpClient(withInterceptorsFromDi())]
}).compileComponents();
    });

    it('should create an instance', async(
        inject(
            [MenuService, SettingsService, TranslatorService, Injector],
            (menuService, settingsService, translator, injector) => {
                let component = new HeaderComponent(
                    menuService,
                    settingsService,
                    translator,
                    injector
                );
                expect(component).toBeTruthy();
            }
        )
    ));
});
