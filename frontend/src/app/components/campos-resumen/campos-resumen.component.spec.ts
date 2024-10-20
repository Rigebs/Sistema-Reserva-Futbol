import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CamposResumenComponent } from './campos-resumen.component';

describe('CamposResumenComponent', () => {
  let component: CamposResumenComponent;
  let fixture: ComponentFixture<CamposResumenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CamposResumenComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CamposResumenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
