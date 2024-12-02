import { CommonModule, NgForOf } from "@angular/common";
import { Component, EventEmitter, Input, Output } from "@angular/core";
import { MatCardModule } from "@angular/material/card";
import { MatIconModule } from "@angular/material/icon";
import { DatePipe } from "@angular/common";
import { MatButtonModule } from "@angular/material/button";

@Component({
  selector: "app-campos-resumen",
  standalone: true,
  imports: [MatCardModule, MatIconModule, CommonModule, MatButtonModule],
  templateUrl: "./campos-resumen.component.html",
  styleUrl: "./campos-resumen.component.css",
  providers: [DatePipe],
})
export class CamposResumenComponent {
  @Input() reservas: any[] = [];
  @Output() reservaEliminada = new EventEmitter<any>(); // Agregar salida

  constructor(private datePipe: DatePipe) {}

  formatDate(date: Date): string {
    return this.datePipe.transform(date, "dd/MM/yyyy") || "";
  }

  formatHour(hourString: string | null): string {
    if (!hourString) {
      return "N/A";
    }

    const [hour] = hourString.split(":").map(Number);
    const suffix = hour >= 12 ? "PM" : "AM";
    const formattedHour = hour % 12 || 12;
    return `${formattedHour}:00 ${suffix}`;
  }

  removeReserva(reserva: any) {
    this.reservaEliminada.emit(reserva);
    this.reservas = this.reservas.filter((r) => r !== reserva);
  }
}
