import { Component, model } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { MatButtonModule } from '@angular/material/button';
import { CarouselComponent } from "../../components/carousel/carousel.component";
import { MatSelectModule } from '@angular/material/select';
import { NgClass, NgForOf } from '@angular/common';
import {ChangeDetectionStrategy} from '@angular/core';
import {MatCardModule} from '@angular/material/card';
import {provideNativeDateAdapter} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import { CampoCardComponent } from "../../components/campo-card/campo-card.component";
import { ReservaCalendarComponent } from "../../components/reserva-calendar/reserva-calendar.component";
import { ProcesoReservaComponent } from "../../components/proceso-reserva/proceso-reserva.component";
import { MatGridListModule } from '@angular/material/grid-list';

@Component({
  selector: 'app-home',
  standalone: true,
  providers: [provideNativeDateAdapter()],
  imports: [NavbarComponent, MatButtonModule,
    MatCardModule, MatDatepickerModule,
    CarouselComponent, MatSelectModule, 
    MatGridListModule,
    NgClass,
    NgForOf, CampoCardComponent, ReservaCalendarComponent, ProcesoReservaComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  images = [
    'campo3.jpg',
    'campo2.jpeg',
    'campo4.jpg'
  ];

  selectedSlots: { [key: string]: string[] } = {};

  isSelected(day: string, time: string): boolean {
    return this.selectedSlots[day]?.includes(time) || false;
  }

  selectedDistrict: string;
  districts: string[] = ['Parcona', 'Tinguiña', 'Pueblo Nuevo', 'Miraflores', 'Cerro Colorado'];

  selected = model<Date | null>(null);

  constructor() {
    this.selectedDistrict = this.districts[0];
  }

  campos = [
    {
      nombre: 'Campo El Estadio',
      imagen: 'campo-1.jpeg',
      descripcion: 'Ideal para torneos locales con grandes espacios.',
      precio: 60,
      estado: 'Disponible',
    },
    {
      nombre: 'Campo El Estadio',
      imagen: 'campo-2.jpeg',
      descripcion: 'Ideal para torneos locales con grandes espacios.',
      precio: 60,
      estado: 'Disponible',
    },
    {
      nombre: 'Campo El Estadio',
      imagen: 'campo-3.jpeg',
      descripcion: 'Ideal para torneos locales con grandes espacios.',
      precio: 60,
      estado: 'Disponible',
    },
    {
      nombre: 'Campo El Estadio',
      imagen: 'campo-4.jpeg',
      descripcion: 'Ideal para torneos locales con grandes espacios.',
      precio: 60,
      estado: 'Disponible',
    },
  ];
}
