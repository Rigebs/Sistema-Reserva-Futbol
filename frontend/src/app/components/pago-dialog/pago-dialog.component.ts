import { Component, Inject, Input } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";

@Component({
  selector: "app-pago-dialog",
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
  templateUrl: "./pago-dialog.component.html",
  styleUrl: "./pago-dialog.component.css",
})
export class PagoDialogComponent {
  qrUrl: string;

  constructor(
    public dialogRef: MatDialogRef<PagoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.qrUrl = data.qrUrl; // Asignamos la URL recibida a la propiedad qrUrl
  }
}
