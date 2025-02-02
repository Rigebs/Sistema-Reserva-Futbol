import { Component, OnInit } from "@angular/core";
import { TableComponent } from "../../components/table/table.component";
import { TableColumn } from "../../models/table-column";
import { SidebarComponent } from "../../components/sidebar/sidebar.component";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatDialog } from "@angular/material/dialog";
import { DialogFormComponent } from "../../components/dialog-form/dialog-form.component";
import { CampoService } from "../../services/campo.service";
import { Campo } from "../../models/campo";
import { TipoDeporteService } from "../../services/tipo-deporte.service";
import { TipoDeporte } from "../../models/tipo-deporte";
import { MatSelectModule } from "@angular/material/select";
import { SidebarPruebaComponent } from "../../components/sidebar-prueba/sidebar-prueba.component";
import { MatSnackBar } from "@angular/material/snack-bar";
import { catchError, finalize, throwError } from "rxjs";

@Component({
  selector: "app-gestionar-campos",
  standalone: true,
  imports: [
    TableComponent,
    MatButtonModule,
    MatIconModule,
    MatSelectModule,
    SidebarPruebaComponent,
  ],
  templateUrl: "./gestionar-campos.component.html",
  styleUrl: "./gestionar-campos.component.css",
})
export class GestionarCamposComponent implements OnInit {
  // Propiedades
  campos: Campo[] = [];
  tipoDeportes: { value: number; label: string }[] = []; // Opciones para el combobox
  isLoading: boolean = true;
  errorMessage: string = "";

  // Configuración de las columnas de la tabla
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
        { icon: "block", action: "toggleState", color: "warn" },
      ],
    },
  ];

  dataSource: Campo[] = [];

  constructor(
    public dialog: MatDialog,
    private campoService: CampoService,
    private tipoDeporteService: TipoDeporteService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.getAllCampos();
    this.getAllTipoDeportes();
    console.log("CAMPOS:", this.dataSource);
  }

  // Método para obtener los datos de los campos
  getAllCampos(): void {
    this.isLoading = true;
    this.campoService.getCamposByCompania().subscribe({
      next: (data: Campo[]) => {
        this.dataSource = data.map((campo) => ({
          ...campo,
          estadoTag:
            campo.estado === "1"
              ? { label: "Habilitado", color: "green" }
              : { label: "Inhabilitado", color: "red" },
        }));
        console.log("CAMPOS:", this.dataSource);

        this.isLoading = false;
        console.log("ho");
      },
      error: (err) => {
        console.error("Error al obtener los campos:", err);
        this.errorMessage = "Ocurrió un error al cargar los datos.";
        this.isLoading = false;
      },
    });
  }

  // Método para obtener los tipos de deportes
  getAllTipoDeportes(): void {
    this.tipoDeporteService.getAll().subscribe({
      next: (data: TipoDeporte[]) => {
        console.log("DA: ", data);

        this.tipoDeportes = data.map((tipo) => ({
          value: tipo.id,
          label: tipo.nombre,
        }));
      },
      error: (err) => {
        console.error("Error al obtener tipos de deportes:", err);
      },
    });
  }

  openDialog(data: Campo | null = null): void {
    const dialogRef = this.dialog.open(DialogFormComponent, {
      width: "400px",
      data: data
        ? [
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
              type: "select",
              label: "Tipo de Deporte",
              name: "tipoDeporteId",
              value: data.tipoDeporte ? data.tipoDeporte.id : null, // Asignamos el id del tipo de deporte o null si no existe
              options: this.tipoDeportes, // Pasamos las opciones del tipo de deporte
            },
          ]
        : [
            { type: "text", label: "Nombre", name: "nombre", value: "" },
            {
              type: "textarea",
              label: "Descripción",
              name: "descripcion",
              value: "",
            },
            { type: "text", label: "Precio", name: "precio", value: "" },
            {
              type: "select",
              label: "Tipo de Deporte",
              name: "tipoDeporteId",
              value: null, // En el caso de crear un nuevo campo, el valor de tipoDeporteId es null
              options: this.tipoDeportes, // Pasamos las opciones disponibles
            },
          ],
    });

    // Suscripción al evento de envío del formulario
    dialogRef.componentInstance.submitEvent.subscribe((formData) => {
      const nuevoCampo: Campo = {
        ...formData,
        tipoDeporte: {
          id: formData.tipoDeporteId, // Solo enviamos el id del tipoDeporte
        },
      };

      console.log("CAMPO: ", nuevoCampo);

      if (data) {
        // Editar campo existente
        this.campoService.update(data.id, nuevoCampo).subscribe({
          next: () => {
            const index = this.dataSource.findIndex(
              (campo) => campo.id === data.id
            );
            if (index > -1) {
              this.dataSource[index] = { ...data, ...nuevoCampo };
              this.dataSource = [...this.dataSource];
            }
          },
          error: (err) => {
            console.error("Error al actualizar el campo:", err);
          },
        });
      } else {
        // Crear un nuevo campo
        this.campoService.save(nuevoCampo).subscribe({
          next: () => {
            this.getAllCampos();
          },
          error: (err) => {
            console.error("Error al guardar el campo:", err);
          },
        });
      }
    });
  }

  // Maneja las acciones de la tabla (editar o inhabilitar/activar)
  onAction(event: { action: string; row: any }) {
    if (event.action === "edit") {
      this.openDialog(event.row); // Abre el diálogo con datos del campo a editar
    } else if (event.action === "toggleState") {
      const estadoActual = event.row.estado;
      const nuevoEstado = estadoActual === "1" ? "0" : "1"; // Calcula el nuevo estado

      this.campoService
        .changeStatus(event.row.id, nuevoEstado)
        .pipe(
          catchError((error) => {
            this.snackBar.open("Error al cambiar estado", "Cerrar", {
              duration: 3000,
            });
            return throwError(() => error);
          }),
          finalize(() => {
            this.dataSource = [...this.dataSource];
          })
        )
        .subscribe(() => {
          this.snackBar.open("Estado cambiado", "Cerrar", { duration: 3000 });
          event.row.estado = nuevoEstado; // Actualiza el estado en el objeto `row`
        });
    }
  }
}
