import { Component, EventEmitter, OnInit, Output } from "@angular/core";
import { Departamento } from "../../models/departamento";
import { Provincia } from "../../models/provincia";
import { Distrito } from "../../models/distrito";
import { DepartamentoService } from "../../services/departamento.service";
import { ProvinciaService } from "../../services/provincia.service";
import { DistritoService } from "../../services/distrito.service";
import { MatCardModule } from "@angular/material/card";
import { MatSelectModule } from "@angular/material/select";
import { CommonModule } from "@angular/common";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";

@Component({
  selector: "app-seleccionar-ubicacion",
  standalone: true,
  imports: [
    FormsModule,
    MatSelectModule,
    MatFormFieldModule,
    MatCardModule,
    CommonModule,
  ],
  templateUrl: "./seleccionar-ubicacion.component.html",
  styleUrl: "./seleccionar-ubicacion.component.css",
})
export class SeleccionarUbicacionComponent implements OnInit {
  departamentos: Departamento[] = []; // Asignar datos de departamentos
  provincias: Provincia[] = []; // Asignar datos de provincias
  distritos: Distrito[] = []; // Asignar datos de distritos

  selectedDepartamentoId: number | null = null;
  selectedProvinciaId: number | null = null;

  @Output() distritoSeleccionado = new EventEmitter<any>();

  selectedDistrito: any;

  constructor(
    private departamentoService: DepartamentoService,
    private provinciaService: ProvinciaService,
    private distritoService: DistritoService
  ) {}

  ngOnInit(): void {
    this.departamentoService.getAll().subscribe((data) => {
      this.departamentos = data;
    });
  }

  onDepartamentoChange(departamentoId: number): void {
    this.provinciaService
      .getByDepartamentoId(departamentoId)
      .subscribe((data) => {
        this.provincias = data;
        this.selectedProvinciaId = null;
        this.distritos = [];
        this.selectedDistrito = null;
      });
  }

  onProvinciaChange(provinciaId: number): void {
    this.distritoService.getByProvinciaId(provinciaId).subscribe((data) => {
      this.distritos = data;
      this.selectedDistrito = null;
    });
  }

  onDistritoChange(distrito: any): void {
    this.selectedDistrito = distrito;
    this.distritoSeleccionado.emit(distrito);
  }
}
