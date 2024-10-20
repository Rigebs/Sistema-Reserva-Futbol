import { SelectionModel } from '@angular/cdk/collections';
import { NgClass, NgFor, NgIf } from '@angular/common';
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

@Component({
  selector: 'app-reserva-list',
  standalone: true,
  imports: [MatCheckboxModule, NgFor, NgIf, 
    MatTableModule,
    MatIcon,
    MatInputModule,
    MatChipsModule,
    FormsModule, MatButtonModule, NgClass],
  templateUrl: './reserva-list.component.html',
  styleUrl: './reserva-list.component.css'
})
export class ReservaListComponent {
  @Input() reservas: any[] = [];
  @Output() reservaFinalizada = new EventEmitter<any>();
  displayedColumns: string[] = ['nombreCampo', 'descripcion', 'seleccionar'];
  selection = new SelectionModel<any>(true, []);
  selectedReserva: any; // Para almacenar la reserva seleccionada

  constructor(public dialog: MatDialog) {}

  abrirDialogo(reserva: any): void {
    const dialogRef = this.dialog.open(ProcesoReservaComponent, {
      data: reserva,
    });

    dialogRef.componentInstance.reservaFinalizada.subscribe((reservaCompletada: any) => {
      this.reservaFinalizada.emit(reservaCompletada);
      this.cambiarColorBoton(reserva);
    });
  }

  cambiarColorBoton(reserva: any) {
    reserva.finalizada = true; // Marca la reserva como finalizada
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.reservas.length;
    return numSelected === numRows;
  }

  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
    } else {
      this.reservas.forEach((row) => this.selection.select(row));
    }
  }

  checkboxLabel(row?: any): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row`;
  }

  hayReservasSeleccionadas() {
    return this.selection.selected.length > 0;
  }

  toggleRotation(reserva: any) {
    reserva.rotating = !reserva.rotating;

    // Opción: detener la rotación después de un tiempo
    setTimeout(() => {
      reserva.rotating = false;
    }, 1000); // La rotación se detendrá después de 1 segundo
  }
}
