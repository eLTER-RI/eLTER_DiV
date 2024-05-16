import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MetadataCatalogueComponent } from './metadata-catalogue.component';

describe('MetadataCatalogueComponent', () => {
  let component: MetadataCatalogueComponent;
  let fixture: ComponentFixture<MetadataCatalogueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MetadataCatalogueComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MetadataCatalogueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
