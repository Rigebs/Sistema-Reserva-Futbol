import { Component } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";

@Component({
  selector: "app-intro-dialog",
  standalone: true,
  imports: [MatButtonModule, MatDialogModule],
  templateUrl: "./intro-dialog.component.html",
  styleUrl: "./intro-dialog.component.css",
})
export class IntroDialogComponent {
  constructor(public dialogRef: MatDialogRef<IntroDialogComponent>) {}

  closeDialog(): void {
    this.dialogRef.close();
  }
}
