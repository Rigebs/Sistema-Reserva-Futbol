import { SelectionModel } from '@angular/cdk/collections';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';

@Component({
  selector: 'app-reserva-list',
  standalone: true,
  imports: [MatCheckboxModule, NgFor, NgIf, 
    MatTableModule,
    FormsModule, MatButtonModule, NgClass],
  templateUrl: './reserva-list.component.html',
  styleUrl: './reserva-list.component.css'
})
export class ReservaListComponent {
  @Input() reservas: any[] = [];

  displayedColumns: string[] = ['select', 'nombreCampo', 'acciones'];
  selection = new SelectionModel<any>(true, []);

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.reservas.length;
    return numSelected === numRows;
  }

  toggleAllRows() {
    if (this.isAllSelected()) {
      this.selection.clear();
    } else {
      this.reservas.forEach(row => this.selection.select(row));
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

  getEstadoClass(estado: string) {
    switch (estado) {
      case 'En proceso':
        return 'estado-en-proceso';
      case 'Reservado':
        return 'estado-reservado';
      case 'No disponible':
        return 'estado-no-disponible';
      default:
        return '';
    }
  }
}
