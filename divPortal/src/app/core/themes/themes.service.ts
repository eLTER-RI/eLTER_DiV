import { Injectable, Renderer2, RendererFactory2 } from '@angular/core';

@Injectable()
export class ThemesService {
    defaultTheme: string = 'A';
    private currentTheme: string = '';
    private renderer: Renderer2;

    constructor(rendererFactory: RendererFactory2) {
        this.renderer = rendererFactory.createRenderer(null, null);
        this.setTheme(this.defaultTheme);
    }

    setTheme(name: string) {
        // Remove the old theme class
        if (this.currentTheme) {
            this.renderer.removeClass(
                document.body,
                `theme-${this.currentTheme.toLowerCase()}`
            );
        }

        // Add the new theme class
        this.renderer.addClass(document.body, `theme-${name.toLowerCase()}`);

        // Update current theme
        this.currentTheme = name;
    }

    getDefaultTheme() {
        return this.defaultTheme;
    }

    getCurrentTheme() {
        return this.currentTheme;
    }
}
