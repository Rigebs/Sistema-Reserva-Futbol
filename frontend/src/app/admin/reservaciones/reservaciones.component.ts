import { Component } from "@angular/core";
import { SidebarComponent } from "../../components/sidebar/sidebar.component";
import { TableComponent } from "../../components/table/table.component";
import { TableColumn } from "../../models/table-column";
import { DialogFormComponent } from "../../components/dialog-form/dialog-form.component";
import { MatDialog } from "@angular/material/dialog";
import { SidebarPruebaComponent } from "../../components/sidebar-prueba/sidebar-prueba.component";

@Component({
  selector: "app-reservaciones",
  standalone: true,
  imports: [TableComponent, SidebarPruebaComponent],
  templateUrl: "./reservaciones.component.html",
  styleUrl: "./reservaciones.component.css",
})
export class ReservacionesComponent {
  constructor(public dialog: MatDialog) {}

  columns: TableColumn[] = [
    { key: "id", label: "ID" },
    { key: "cliente", label: "Cliente" },
    { key: "descripcion", label: "Descripción" },
    { key: "total", label: "Total" },
    {
      key: "actions",
      label: "Revisar",
      isAction: true,
      actions: [{ icon: "visibility", action: "view", color: "primary" }],
    },
  ];

  dataSource = [
    {
      id: 101,
      cliente: "Carlos Pérez",
      descripcion: "Reserva del Campo Central para torneo local.",
      total: 300,
    },
    {
      id: 102,
      cliente: "Lucía Gómez",
      descripcion: "Reserva del Campo Norte para práctica semanal.",
      total: 150,
    },
    {
      id: 103,
      cliente: "Andrés Torres",
      descripcion: "Reserva del Campo Sur para evento de playa.",
      total: 250,
    },
  ];

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
