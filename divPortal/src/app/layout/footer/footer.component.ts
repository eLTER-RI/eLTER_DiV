import { Component, Input, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';


@Component({
    selector: '[app-footer]',
    templateUrl: './footer.component.html',
    styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

    @Input() hoverFooter: any;

    @Input() footerText;
    @Input() footerOnHoverText;



    footerHtml;
    footerOnHoverHtml;

    constructor(public sanitizer: DomSanitizer) {

    }

    ngOnInit() {

    }

}
