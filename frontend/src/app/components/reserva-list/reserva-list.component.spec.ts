import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReservaListComponent } from './reserva-list.component';

describe('ReservaListComponent', () => {
  let component: ReservaListComponent;
  let fixture: ComponentFixture<ReservaListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservaListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReservaListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
