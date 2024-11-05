import { Component, Input } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatCardModule } from "@angular/material/card";
import { Campo } from "../../models/campo";
import { CommonModule } from "@angular/common";
import { CampoSede } from "../../models/campo-sede";

@Component({
  selector: "app-campo-card",
  standalone: true,
  imports: [MatCardModule, MatButtonModule, CommonModule],
  templateUrl: "./campo-card.component.html",
  styleUrl: "./campo-card.component.css",
})
export class CampoCardComponent {
  @Input() campo!: CampoSede;
}
