import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowLayaresDetailsComponent } from './show-layares-details.component';

describe('ShowLayaresDetailsComponent', () => {
  let component: ShowLayaresDetailsComponent;
  let fixture: ComponentFixture<ShowLayaresDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShowLayaresDetailsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowLayaresDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
