import { Component, model, OnInit } from "@angular/core";
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { MatButtonModule } from "@angular/material/button";
import { CarouselComponent } from "../../components/carousel/carousel.component";
import { MatSelectModule } from "@angular/material/select";
import { NgClass, NgForOf } from "@angular/common";
import { ChangeDetectionStrategy } from "@angular/core";
import { MatCardModule } from "@angular/material/card";
import { provideNativeDateAdapter } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { CampoCardComponent } from "../../components/campo-card/campo-card.component";
import { ReservaCalendarComponent } from "../../components/reserva-calendar/reserva-calendar.component";
import { ProcesoReservaComponent } from "../../components/proceso-reserva/proceso-reserva.component";
import { MatGridListModule } from "@angular/material/grid-list";
import { CampoService } from "../../services/campo.service";
import { Campo } from "../../models/campo";

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
    NgClass,
    NgForOf,
    CampoCardComponent,
    ReservaCalendarComponent,
    ProcesoReservaComponent,
  ],
  templateUrl: "./home.component.html",
  styleUrl: "./home.component.css",
})
export class HomeComponent implements OnInit {
  images = ["campo3.jpg", "campo2.jpeg", "campo4.jpg"];
  campos: Campo[] = [];

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

  selected = model<Date | null>(null);

  constructor(private campoService: CampoService) {
    this.selectedDistrict = this.districts[0];
  }

  ngOnInit(): void {
    this.loadCampos();
  }

  loadCampos(): void {
    this.campoService.getAll().subscribe({
      next: (data: Campo[]) => {
        this.campos = data;
      },
      error: (err) => {
        console.error("Error al cargar los campos", err);
      },
    });
  }
}
