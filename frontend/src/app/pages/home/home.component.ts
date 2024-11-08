import { Component, OnInit } from "@angular/core";
import { NavbarComponent } from "../../components/navbar/navbar.component";
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

@Component({
  selector: "app-home",
  standalone: true,
  providers: [provideNativeDateAdapter()],
  imports: [
    NavbarComponent,
    MatButtonModule,
    MatCardModule,
    MatDatepickerModule,
    CarouselComponent,
    MatSelectModule,
    MatGridListModule,
    CampoCardComponent,
    CommonModule,
  ],
  templateUrl: "./home.component.html",
  styleUrl: "./home.component.css",
})
export class HomeComponent implements OnInit {
  images = ["campo3.jpg", "campo2.jpeg", "campo4.jpg"];
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

  // Cambiar a tipo Date | null directamente, sin usar ModelSignal
  selected: Date | null = new Date(); // Valor por defecto de hoy

  // Variables para las fechas de restricción
  minDate: Date = new Date(); // Fecha de hoy
  maxDate: Date = new Date(); // Fecha máxima (un mes después de hoy)

  constructor(private campoService: CampoService) {
    this.selectedDistrict = this.districts[0];

    // Establecer la fecha máxima a un mes después de hoy
    this.maxDate.setMonth(this.maxDate.getMonth() + 1);
  }

  ngOnInit(): void {
    this.loadCampos();
  }

  loadCampos(): void {
    // Si no hay fecha seleccionada, usamos la fecha actual
    const fechaSeleccionada = this.selected ? this.selected : new Date(); // Usar fecha actual si es null

    const fechaReserva = this.formatDate(fechaSeleccionada);

    // Llamar al servicio con la fecha seleccionada y el distrito actual
    this.campoService.getAllCampoSede("", "", "", fechaReserva).subscribe({
      next: (data: CampoSede[]) => {
        this.campos = data;
      },
      error: (err) => {
        console.error("Error al cargar los campos", err);
      },
    });
  }

  // Método para formatear la fecha en el formato 'yyyy-MM-dd'
  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
  }
}
