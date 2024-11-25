import { Component, OnInit } from "@angular/core";
import { SidebarComponent } from "../../components/sidebar/sidebar.component";
import { TableComponent } from "../../components/table/table.component";
import { TableColumn } from "../../models/table-column";
import { DialogFormComponent } from "../../components/dialog-form/dialog-form.component";
import { MatDialog } from "@angular/material/dialog";
import { SidebarPruebaComponent } from "../../components/sidebar-prueba/sidebar-prueba.component";
import { VentaService } from "../../services/venta.service";
import { ReservaDisplay } from "../../models/reserva-display";

@Component({
  selector: "app-reservaciones",
  standalone: true,
  imports: [TableComponent, SidebarPruebaComponent],
  templateUrl: "./reservaciones.component.html",
  styleUrl: "./reservaciones.component.css",
})
export class ReservacionesComponent implements OnInit {
  constructor(public dialog: MatDialog, private ventaService: VentaService) {}

  columns: TableColumn[] = [
    { key: "reservaId", label: "ID" },
    { key: "cliente", label: "Cliente" },
    { key: "fechaReserva", label: "Fecha" },
    { key: "subtotal", label: "subtotal" },
    { key: "total", label: "Total" },
  ];

  dataSource: ReservaDisplay[] = [];

  ngOnInit(): void {
    this.ventaService.getReservasForLoggedUser().subscribe({
      next: (data) => {
        this.dataSource = data;
        console.log("D: ", data);
      },
    });
  }

  openDialog(data: any = null): void {
    const dialogRef = this.dialog.open(DialogFormComponent, {
      width: "400px",
      data: data
        ? [
            // Caso de edición: carga datos existentes
            {
              type: "text",
              label: "Nombre",
              name: "nombre",
              value: data.nombre,
            },
            {
              type: "textarea",
              label: "Descripción",
              name: "descripcion",
              value: data.descripcion,
            },
            {
              type: "text",
              label: "Precio",
              name: "precio",
              value: data.precio,
            },
            {
              type: "checkbox",
              label: "Estado",
              name: "estado",
              value: data.estado,
            },
          ]
        : [
            // Caso de creación: campos vacíos
            { type: "text", label: "Nombre", name: "nombre", value: "" },
            {
              type: "textarea",
              label: "Descripción",
              name: "descripcion",
              value: "",
            },
            { type: "text", label: "Precio", name: "precio", value: "" },
          ],
    });

    dialogRef.componentInstance.submitEvent.subscribe((formData) => {
      if (data) {
        // Si se está editando un elemento existente, actualizar el elemento en `dataSource`
        Object.assign(data, formData);
      } else {
        // Si es un nuevo registro, agregar a `dataSource`
        formData.id = this.dataSource.length + 1;
        formData.estado = true;
        this.dataSource = [...this.dataSource, formData];
      }

      // Actualiza la referencia de `dataSource` para refrescar la tabla
      this.dataSource = [...this.dataSource];
    });
  }

  onAction(event: { action: string; row: any }) {
    this.openDialog(event.row);
  }
}
