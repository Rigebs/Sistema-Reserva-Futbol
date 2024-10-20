import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcesoReservaComponent } from './proceso-reserva.component';

describe('ProcesoReservaComponent', () => {
  let component: ProcesoReservaComponent;
  let fixture: ComponentFixture<ProcesoReservaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProcesoReservaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProcesoReservaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
