import { Component, OnInit } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { CarouselComponent } from "../../components/carousel/carousel.component";
import { MatSelectModule } from "@angular/material/select";
import { CommonModule } from "@angular/common";
import { MatCardModule } from "@angular/material/card";
import { provideNativeDateAdapter } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { CampoCardComponent } from "../../components/campo-card/campo-card.component";
import { MatGridListModule } from "@angular/material/grid-list";
import { CampoService } from "../../services/campo.service";
import { CampoSede } from "../../models/campo-sede";
import { NavbarHomeComponent } from "../../components/navbar-home/navbar-home.component";
import { MatDividerModule } from "@angular/material/divider";
import { DepartamentoService } from "../../services/departamento.service";
import { ProvinciaService } from "../../services/provincia.service";
import { DistritoService } from "../../services/distrito.service";
import { Distrito } from "../../models/distrito";
import { Departamento } from "../../models/departamento";
import { Provincia } from "../../models/provincia";
import { MatIconModule } from "@angular/material/icon";
import { MatTooltipModule } from "@angular/material/tooltip";

@Component({
  selector: "app-home",
  standalone: true,
  providers: [provideNativeDateAdapter()],
  imports: [
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    CarouselComponent,
    MatSelectModule,
    MatGridListModule,
    CampoCardComponent,
    CommonModule,
    NavbarHomeComponent,
    MatDividerModule,
    MatIconModule,
    MatTooltipModule,
  ],
  templateUrl: "./home.component.html",
  styleUrl: "./home.component.css",
})
export class HomeComponent implements OnInit {
  images = [
    "https://res.cloudinary.com/dpfcpo5me/image/upload/v1732066033/okgofljbrvcofompyxfk.jpg",
    "https://res.cloudinary.com/dpfcpo5me/image/upload/v1732066033/fntcuhbxcippz4b24rcr.jpg",
    "https://res.cloudinary.com/dpfcpo5me/image/upload/v1732066033/ffniiukbqmibqke3sb7f.jpg",
    "https://res.cloudinary.com/dpfcpo5me/image/upload/v1732066033/seu8mefj09f8scnkaom2.jpg",
  ];
  campos: CampoSede[] = [];

  selectedSlots: { [key: string]: string[] } = {};

  isSelected(day: string, time: string): boolean {
    return this.selectedSlots[day]?.includes(time) || false;
  }

  selectedDistrict: string;
  districts: string[] = [
    "Parcona",
    "Tinguiña",
    "Pueblo Nuevo",
    "Miraflores",
    "Cerro Colorado",
  ];

  selected: Date | null = new Date();

  minDate: Date = new Date();
  maxDate: Date = new Date();

  distritos: Distrito[] = [];
  provincias: Provincia[] = [];
  departamentos: Departamento[] = [];

  provinciaHabilitado: boolean = true;
  distritoHabilitado: boolean = true;

  departamentoSeleccionado: number | undefined;
  provinciaSeleccionado: number | undefined;
  distritoSeleccionado: number | undefined;

  departamentoNombre: string = "";
  provinciaNombre: string = "";
  distritoNombre: string = "";

  constructor(
    private campoService: CampoService,
    private departamentoService: DepartamentoService,
    private provinciaService: ProvinciaService,
    private distritoService: DistritoService
  ) {
    this.selectedDistrict = this.districts[-1];
    this.maxDate.setMonth(this.maxDate.getMonth() + 1);
  }

  ngOnInit(): void {
    this.loadCampos();
    this.loadDepartamentos();
  }

  filtrar(): void {
    const departamento = this.departamentos.find(
      (d) => d.id === this.departamentoSeleccionado
    );
    const provincia = this.provincias.find(
      (p) => p.id === this.provinciaSeleccionado
    );
    const distrito = this.distritos.find(
      (d) => d.id === this.distritoSeleccionado
    );

    console.log("Departamento:", departamento?.nombre || "No seleccionado");
    console.log("Provincia:", provincia?.nombre || "No seleccionado");
    console.log("Distrito:", distrito?.nombre || "No seleccionado");

    this.departamentoNombre = departamento?.nombre || "";
    this.provinciaNombre = provincia?.nombre || "";
    this.distritoNombre = distrito?.nombre || "";
    this.loadCampos();

    this.departamentoNombre = "";
    this.provinciaNombre = "";
    this.distritoNombre = "";
  }

  loadDepartamentos() {
    this.departamentoService.getAll().subscribe({
      next: (data) => {
        this.departamentos = data;
        console.log("data: ", data);
      },
      error: (error) => {
        console.log("ERROR: ", error);
      },
    });
  }

  onDepartamentoChange(event: any) {
    this.departamentoSeleccionado = event.value;
    console.log(event.value);
    this.provinciaService.getByDepartamentoId(event.value).subscribe({
      next: (data) => {
        if (data) {
          this.provinciaHabilitado = false;
          this.provincias = data;
        }
      },
    });
  }

  onProvinciaChange(event: any) {
    this.provinciaSeleccionado = event.value;
    this.distritoService.getByProvinciaId(event.value).subscribe({
      next: (data) => {
        if (data) {
          this.distritoHabilitado = false;
          this.distritos = data;
        }
      },
    });
  }

  onDistritoChange(event: any) {
    this.distritoSeleccionado = event.value;
  }

  loadCampos(): void {
    const fechaSeleccionada = this.selected ? this.selected : new Date();
    const fechaReserva = this.formatDate(fechaSeleccionada);

    this.campoService
      .getAllCampoSede(
        this.distritoNombre,
        this.provinciaNombre,
        this.departamentoNombre,
        fechaReserva
      )
      .subscribe({
        next: (data: CampoSede[]) => {
          this.campos = data;
          console.log("DATA: ", this.campos);
        },
        error: (err) => {
          console.error("Error al cargar los campos", err);
          this.campos = [];
        },
      });
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  }
}
