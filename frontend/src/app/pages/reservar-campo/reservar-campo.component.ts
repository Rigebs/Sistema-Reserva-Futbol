import { Component } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { ProcesoReservaComponent } from "../../components/proceso-reserva/proceso-reserva.component";
import { CommonModule } from '@angular/common';
import { MatNativeDateModule } from '@angular/material/core';
import { ReservaListComponent } from "../../components/reserva-list/reserva-list.component";
import { MatCardModule } from '@angular/material/card';
import { CamposResumenComponent } from "../../components/campos-resumen/campos-resumen.component";

@Component({
  selector: 'app-reservar-campo',
  standalone: true,
  imports: [NavbarComponent, ProcesoReservaComponent,
    MatCardModule,
    CommonModule, MatNativeDateModule, ReservaListComponent, CamposResumenComponent],
  templateUrl: './reservar-campo.component.html',
  styleUrl: './reservar-campo.component.css'
})
export class ReservarCampoComponent {
  reservas = [
    { id: 1, nombreCampo: 'Campo 1', estado: 'En proceso' },
    { id: 2, nombreCampo: 'Campo 2', estado: 'Reservado' },
    { id: 3, nombreCampo: 'Campo 2', estado: 'No disponible' },
  ];

  // Lista de reservas finalizadas
  reservasFinalizadas: any[] = [];

  // Método para eliminar una reserva
  onEliminarReserva(id: number) {
    this.reservas = this.reservas.filter(reserva => reserva.id !== id);
  }

  // Método para agregar una reserva finalizada
  agregarReservaFinalizada(reserva: any) {
    this.reservasFinalizadas.push(reserva);
  }
}
