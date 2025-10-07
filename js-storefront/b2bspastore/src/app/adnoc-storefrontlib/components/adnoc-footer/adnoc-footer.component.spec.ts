import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdnocFooterComponent } from './adnoc-footer.component';

describe('AdnocFooterComponent', () => {
  let component: AdnocFooterComponent;
  let fixture: ComponentFixture<AdnocFooterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AdnocFooterComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AdnocFooterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
