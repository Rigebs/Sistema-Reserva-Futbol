import { Component, EventEmitter, Input, Output } from "@angular/core";
import { EmpresaCompania } from "../../models/empresa-compania";
import { MatIconModule } from "@angular/material/icon";
import { MatToolbarModule } from "@angular/material/toolbar";
import { MatTooltipModule } from "@angular/material/tooltip";
import { UsuarioService } from "../../services/usuario.service";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: "app-compania-card",
  standalone: true,
  imports: [MatIconModule, MatTooltipModule],
  templateUrl: "./compania-card.component.html",
  styleUrl: "./compania-card.component.css",
})
export class CompaniaCardComponent {
  @Input() compania!: EmpresaCompania;
  @Output() companiaEliminada = new EventEmitter<number>();

  // Imagen actual (inicia con la principal)
  currentImage: string = "";

  constructor(
    private usuarioService: UsuarioService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.currentImage = this.compania.companiaImagenUrl;
  }

  // Cambiar entre la imagen principal y el QR
  toggleImage(): void {
    this.currentImage =
      this.currentImage === this.compania.companiaImagenUrl
        ? this.compania.companiaQrImagenUrl
        : this.compania.companiaImagenUrl;
  }

  onCheck(): void {
    console.log("Aceptado:", this.compania);
    this.usuarioService.updateRoleCompania(this.compania.idCompania).subscribe({
      next: () => {
        this.companiaEliminada.emit(this.compania.idCompania);
        this.snackBar.open("Empresa aceptada correctamente", "Cerrar", {
          duration: 3000,
          horizontalPosition: "center",
          verticalPosition: "top",
        });
      },
    });
  }

  onCancel(): void {
    console.log("Cancelado:", this.compania);
    this.usuarioService.rejectCompania(this.compania.idCompania).subscribe({
      next: () => {
        this.companiaEliminada.emit(this.compania.idCompania);
        this.snackBar.open("Empresa rechazada correctamente", "Cerrar", {
          duration: 3000,
          horizontalPosition: "center",
          verticalPosition: "top",
        });
      },
    });
  }
}
