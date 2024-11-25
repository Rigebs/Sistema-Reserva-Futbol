import { Component, Inject, Input } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { ReservaService } from "../../services/reserva.service";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Router } from "@angular/router";

@Component({
  selector: "app-pago-dialog",
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
  templateUrl: "./pago-dialog.component.html",
  styleUrl: "./pago-dialog.component.css",
})
export class PagoDialogComponent {
  qrUrl: string;

  id: number = 0;

  constructor(
    public dialogRef: MatDialogRef<PagoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private reservaService: ReservaService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {
    console.log("sd:", data);

    this.qrUrl = data.qrUrl;
    this.id = data.id;
  }

  verificarPago() {
    console.log("i: ", this.id);

    this.reservaService.isReservaActive(this.id).subscribe({
      next: (data) => {
        if (data.pagoStatus) {
          // Mostrar Snackbar
          this.snackBar.open("PAGO REALIZADO CORRECTAMENTE", "Cerrar", {
            duration: 2000, // Mostrar por 2 segundos
            horizontalPosition: "center", // Posición horizontal
            verticalPosition: "bottom", // Posición vertical
          });

          // Redirigir después de 2 segundos
          setTimeout(() => {
            this.router.navigate(["/home"]); // Navegar a /home
          }, 2000);
        } else {
          // En caso de que el pago no se haya realizado, puedes manejarlo aquí
          this.snackBar.open("Error: El pago no se realizó.", "Cerrar", {
            duration: 2000,
            horizontalPosition: "center",
            verticalPosition: "bottom",
          });
        }
      },
      error: (err) => {
        console.error("Error al verificar el pago:", err);
        this.snackBar.open("Error al verificar el pago.", "Cerrar", {
          duration: 3000,
          horizontalPosition: "center",
          verticalPosition: "bottom",
        });
      },
    });
  }
}
