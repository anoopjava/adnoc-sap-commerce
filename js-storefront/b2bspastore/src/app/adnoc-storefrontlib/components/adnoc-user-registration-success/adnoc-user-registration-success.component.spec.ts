import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdnocRegistrationSuccessComponent } from './adnoc-user-registration-success.component';

describe('AdnocRegistrationSuccessComponent', () => {
  let component: AdnocRegistrationSuccessComponent;
  let fixture: ComponentFixture<AdnocRegistrationSuccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdnocRegistrationSuccessComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AdnocRegistrationSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
