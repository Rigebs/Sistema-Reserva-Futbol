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
import { NavbarHomeComponent } from "../../components/navbar-home/navbar-home.component";

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
    NavbarHomeComponent,
  ],
  templateUrl: "./home.component.html",
  styleUrl: "./home.component.css",
})
export class HomeComponent implements OnInit {
  images = ["voley.jpg", "hola.jpg", "sera.jpg", "tenis.jpg"];
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

  constructor(private campoService: CampoService) {
    this.selectedDistrict = this.districts[0];
    this.maxDate.setMonth(this.maxDate.getMonth() + 1);
  }

  ngOnInit(): void {
    this.loadCampos();
  }

  loadCampos(): void {
    const fechaSeleccionada = this.selected ? this.selected : new Date();

    const fechaReserva = this.formatDate(fechaSeleccionada);

    this.campoService.getAllCampoSede("", "", "", fechaReserva).subscribe({
      next: (data: CampoSede[]) => {
        this.campos = data;
        console.log("DATA: ", this.campos);
      },
      error: (err) => {
        console.error("Error al cargar los campos", err);
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
