import { Component, Input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-campo-card',
  standalone: true,
  imports: [MatCardModule, MatButtonModule],
  templateUrl: './campo-card.component.html',
  styleUrl: './campo-card.component.css'
})
export class CampoCardComponent {
  @Input() campo!: {
    nombre: string;
    imagen: string;
    descripcion: string;
    precio: number;
    estado: string;
  };
}
