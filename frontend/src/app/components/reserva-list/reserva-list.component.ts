import { SelectionModel } from "@angular/cdk/collections";
import { CommonModule, NgClass, NgFor, NgIf } from "@angular/common";
import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatDialog } from "@angular/material/dialog";
import { MatIcon } from "@angular/material/icon";
import { MatTableModule } from "@angular/material/table";
import { MatInputModule } from "@angular/material/input";
import { MatChipsModule } from "@angular/material/chips";
import { ProcesoReservaComponent } from "../proceso-reserva/proceso-reserva.component";
import { MatTooltipModule } from "@angular/material/tooltip";
import { SedeWithCampo } from "../../models/sede-with-campo";

@Component({
  selector: "app-reserva-list",
  standalone: true,
  imports: [
    MatCheckboxModule,
    MatTableModule,
    MatIcon,
    MatInputModule,
    MatTooltipModule,
    CommonModule,
    MatChipsModule,
    FormsModule,
    MatButtonModule,
    NgClass,
  ],
  templateUrl: "./reserva-list.component.html",
  styleUrl: "./reserva-list.component.css",
})
export class ReservaListComponent implements OnInit {
  @Input() sede: SedeWithCampo | undefined;
  @Output() reservaFinalizada = new EventEmitter<any>();

  displayedColumns: string[] = [
    "nombreCampo",
    "descripcion",
    "precio",
    "seleccionar",
  ];
  selection = new SelectionModel<SedeWithCampo>(true, []);
  selectedReserva: SedeWithCampo | null = null;

  constructor(public dialog: MatDialog) {}
  ngOnInit(): void {}

  abrirDialogo(reserva: SedeWithCampo): void {
    const dialogRef = this.dialog.open(ProcesoReservaComponent, {
      data: reserva,
    });

    dialogRef.componentInstance.reservaFinalizada.subscribe(
      (reservaCompletada: SedeWithCampo) => {
        this.reservaFinalizada.emit(reservaCompletada);
      }
    );
  }

  // Este getter nos da acceso directo a los campos de la sede para mostrarlos en la tabla
  get reservas() {
    return this.sede?.camposWithSede || [];
  }
}
