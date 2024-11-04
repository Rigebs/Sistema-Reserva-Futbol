import { Component, ViewChild } from '@angular/core';
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { ProcesoReservaComponent } from "../../components/proceso-reserva/proceso-reserva.component";
import { CommonModule } from '@angular/common';
import { MatNativeDateModule } from '@angular/material/core';
import { ReservaListComponent } from "../../components/reserva-list/reserva-list.component";
import { MatCardModule } from '@angular/material/card';
import { CamposResumenComponent } from "../../components/campos-resumen/campos-resumen.component";
import { Router } from '@angular/router';

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

  @ViewChild('reservaList') reservaListComponent!: ReservaListComponent;

  reservas = [
    { id: 1, nombreCampo: 'Campo 1', precio: 20.00, fecha: '2024-10-21', horaInicio: '10:00', horaFin: '12:00' },
    { id: 2, nombreCampo: 'Campo 2', precio: 30.00, fecha: '2024-10-22', horaInicio: '14:00', horaFin: '16:00' },
    { id: 3, nombreCampo: 'Campo 3', precio: 50.00, fecha: '2024-10-23', horaInicio: '16:00', horaFin: '18:00' },
  ];

  reservasFinalizadas: any[] = [];

  constructor(private router: Router) {}

  // Método para agregar una reserva finalizada
  agregarReservaFinalizada(reserva: any) {
    this.reservasFinalizadas.push(reserva);
  }

  // Método para navegar a la pasarela de pago
  irPasarelaDePago() {
    this.router.navigate(['/pasarela-pago'], { state: { reservas: this.reservasFinalizadas } });
  }

  onReservaEliminada(reserva: any) {
    this.reservasFinalizadas = this.reservasFinalizadas.filter((r) => r !== reserva);
    this.reservaListComponent.hola(reserva);
  }
}