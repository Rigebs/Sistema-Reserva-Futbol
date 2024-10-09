import { Component, model } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { MatButtonModule } from '@angular/material/button';
import { CarouselComponent } from "../../components/carousel/carousel.component";
import { MatSelectModule } from '@angular/material/select';
import { NgForOf } from '@angular/common';
import {ChangeDetectionStrategy} from '@angular/core';
import {MatCardModule} from '@angular/material/card';
import {provideNativeDateAdapter} from '@angular/material/core';
import {MatDatepickerModule} from '@angular/material/datepicker';
import { CampoCardComponent } from "../../components/campo-card/campo-card.component";


@Component({
  selector: 'app-home',
  standalone: true,
  providers: [provideNativeDateAdapter()],
  imports: [NavbarComponent, MatButtonModule,
    MatCardModule, MatDatepickerModule,
    CarouselComponent, MatSelectModule, NgForOf, CampoCardComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  images = [
    'campo1.jpeg',
    'campo2.jpeg',
    'campo3.jpg'
  ];

  selectedDistrict: string; // Almacena el distrito seleccionado
  districts: string[] = ['Parcona', 'Tinguiña', 'Pueblo Nuevo', 'Miraflores', 'Cerro Colorado']; // Lista de distritos

  selected = model<Date | null>(null);

  constructor() {
    this.selectedDistrict = this.districts[0]; // Opcional: selecciona un distrito por defecto
  }

  campos = [
    {
      nombre: 'Campo El Estadio',
      imagen: 'https://material.angular.io/assets/img/examples/shiba2.jpg',
      descripcion: 'Ideal para torneos locales con grandes espacios.',
      precio: 60,
      estado: 'Disponible',
    },
    {
      nombre: 'Campo El Estadio',
      imagen: 'https://material.angular.io/assets/img/examples/shiba2.jpg',
      descripcion: 'Ideal para torneos locales con grandes espacios.',
      precio: 60,
      estado: 'Disponible',
    },
    {
      nombre: 'Campo El Estadio',
      imagen: 'https://material.angular.io/assets/img/examples/shiba2.jpg',
      descripcion: 'Ideal para torneos locales con grandes espacios.',
      precio: 60,
      estado: 'Disponible',
    },
    {
      nombre: 'Campo El Estadio',
      imagen: 'https://material.angular.io/assets/img/examples/shiba2.jpg',
      descripcion: 'Ideal para torneos locales con grandes espacios.',
      precio: 60,
      estado: 'Disponible',
    },
  ];
}
