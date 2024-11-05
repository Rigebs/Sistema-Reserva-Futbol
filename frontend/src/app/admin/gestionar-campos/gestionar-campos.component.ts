import { Component, OnInit } from "@angular/core";
import { TableComponent } from "../../components/table/table.component";
import { TableColumn } from "../../models/table-column";
import { SidebarComponent } from "../../components/sidebar/sidebar.component";
import { NgClass } from "@angular/common";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatDialog } from "@angular/material/dialog";
import { DialogFormComponent } from "../../components/dialog-form/dialog-form.component";

@Component({
  selector: "app-gestionar-campos",
  standalone: true,
  imports: [
    TableComponent,
    SidebarComponent,
    MatButtonModule,
    MatIconModule,
    NgClass,
  ],
  templateUrl: "./gestionar-campos.component.html",
  styleUrl: "./gestionar-campos.component.css",
})
export class GestionarCamposComponent {
  constructor(public dialog: MatDialog) {}

  columns: TableColumn[] = [
    { key: "id", label: "ID" },
    { key: "nombre", label: "Nombre" },
    { key: "descripcion", label: "Descripción" },
    { key: "precio", label: "Precio (s/)" },
    { key: "estado", label: "Estado", isTag: true },
    {
      key: "actions",
      label: "Editar/Inhabilitar",
      isAction: true,
      actions: [
        { icon: "edit", action: "edit", color: "primary" },
        // Acción que cambia dinámicamente entre `block` y `check_circle`
        { icon: "block", action: "toggleState", color: "warn" },
      ],
    },
  ];

  dataSource = [
    {
      id: 1,
      nombre: "Campo Central",
      descripcion: "Campo de césped natural, apto para partidos oficiales.",
      precio: 150,
      estado: true,
    },
    {
      id: 2,
      nombre: "Campo Norte",
      descripcion: "Campo de césped artificial, perfecto para entrenamiento.",
      precio: 100,
      estado: false,
    },
    {
      id: 3,
      nombre: "Campo Sur",
      descripcion: "Campo de arena, ideal para deportes de playa.",
      precio: 120,
      estado: true,
    },
  ];

  // Función para abrir el diálogo de edición con los datos del campo
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

  // Maneja las acciones de la tabla (editar o inhabilitar/activar)
  onAction(event: { action: string; row: any }) {
    if (event.action === "edit") {
      this.openDialog(event.row); // Abre el diálogo con datos del campo a editar
    } else if (event.action === "toggleState") {
      event.row.estado = !event.row.estado; // Cambia entre habilitado e inhabilitado
    }
    // Actualiza la referencia de `dataSource` para refrescar la tabla
    this.dataSource = [...this.dataSource];
  }
}
