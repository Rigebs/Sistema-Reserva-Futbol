import { SelectionModel } from '@angular/cdk/collections';
import { CommonModule, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatInputModule } from '@angular/material/input';
import { MatChipsModule } from '@angular/material/chips';
import { ProcesoReservaComponent } from '../proceso-reserva/proceso-reserva.component';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-reserva-list',
  standalone: true,
  imports: [MatCheckboxModule,
    MatTableModule,
    MatIcon,
    MatInputModule,
    MatTooltipModule,
    CommonModule,
    MatChipsModule,
    FormsModule, MatButtonModule, NgClass],
  templateUrl: './reserva-list.component.html',
  styleUrl: './reserva-list.component.css'
})
export class ReservaListComponent {
  @Input() reservas: any[] = [];
  @Output() reservaFinalizada = new EventEmitter<any>();
  displayedColumns: string[] = ['nombreCampo', 'descripcion', 'precio', 'seleccionar'];
  selection = new SelectionModel<any>(true, []);
  selectedReserva: any;

  constructor(public dialog: MatDialog) {}

  abrirDialogo(reserva: any): void {
    if (reserva.finalizada) {
      return;
    }

    const dialogRef = this.dialog.open(ProcesoReservaComponent, {
      data: reserva,
    });

    dialogRef.componentInstance.reservaFinalizada.subscribe(
      (reservaCompletada: any) => {
        this.reservaFinalizada.emit(reservaCompletada);
        this.cambiarColorBoton(reserva);
      }
    );
  }

  cambiarColorBoton(reserva: any) {
    reserva.finalizada = true;
  }

  hola(reserva: any) {
    // Comparar 'reserva.nombre' con 'r.nombreCampo'
    const reservaEncontrada = this.reservas.find(r => r.nombreCampo === reserva.nombre);
    reservaEncontrada.finalizada = false;
  }
  
  

  toggleRotation(reserva: any) {
    reserva.rotating = !reserva.rotating;

    // Opción: detener la rotación después de un tiempo
    setTimeout(() => {
      reserva.rotating = false;
    }, 1000); // La rotación se detendrá después de 1 segundo
  }
}