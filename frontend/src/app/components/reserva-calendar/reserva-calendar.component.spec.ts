import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservaCalendarComponent } from './reserva-calendar.component';

describe('ReservaCalendarComponent', () => {
  let component: ReservaCalendarComponent;
  let fixture: ComponentFixture<ReservaCalendarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservaCalendarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReservaCalendarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
