import { Component, Input } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { CommonModule } from "@angular/common";
import { CampoSede } from "../../models/campo-sede";
import { MatIconModule } from "@angular/material/icon";
import { Router } from "@angular/router";

@Component({
  selector: "app-campo-card",
  standalone: true,
  imports: [MatCardModule, MatButtonModule, CommonModule, MatIconModule],
  templateUrl: "./campo-card.component.html",
  styleUrl: "./campo-card.component.css",
})
export class CampoCardComponent {
  @Input() campo!: CampoSede;

  constructor(private router: Router) {}

  // Método para redirigir al usuario al formulario de reserva
  reservarCampo(userId: number) {
    this.router.navigate([`/${userId}/reservar-campo`]);
  }
}