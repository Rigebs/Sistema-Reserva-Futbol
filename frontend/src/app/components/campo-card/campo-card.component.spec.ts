import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CampoCardComponent } from './campo-card.component';

describe('CampoCardComponent', () => {
  let component: CampoCardComponent;
  let fixture: ComponentFixture<CampoCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CampoCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CampoCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
