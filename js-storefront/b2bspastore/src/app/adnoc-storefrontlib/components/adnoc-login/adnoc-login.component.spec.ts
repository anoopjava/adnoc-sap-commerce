import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdnocLoginComponent } from './adnoc-login.component';

describe('AdnocLoginComponent', () => {
  let component: AdnocLoginComponent;
  let fixture: ComponentFixture<AdnocLoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdnocLoginComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AdnocLoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
