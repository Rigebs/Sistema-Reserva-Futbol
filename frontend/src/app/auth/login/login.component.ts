import { CommonModule } from "@angular/common";
import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { AuthService } from "../../services/auth.service";
import { catchError, of } from "rxjs";
import { Usuario } from "../../models/usuario";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: "app-login",
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatButtonModule,
    FormsModule,
    MatInputModule,
    MatDialogModule,
  ],
  templateUrl: "./login.component.html",
  styleUrl: "./login.component.css",
})
export class LoginComponent {
  user: Usuario = { identifier: "", password: "" };
  loginError: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<LoginComponent>,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  onLogin() {
    this.loginError = false;
    if (this.user.identifier && this.user.password) {
      this.authService
        .login(this.user)
        .pipe(
          catchError(() => {
            this.loginError = true;
            this.snackBar.open("Error en el inicio de sesión", "Cerrar", {
              duration: 3000,
              horizontalPosition: "right",
              verticalPosition: "top",
            });
            return of(null);
          })
        )
        .subscribe((response) => {
          if (response && response.token) {
            this.snackBar.open("Inicio de sesión exitoso", "Cerrar", {
              duration: 3000,
              horizontalPosition: "right",
              verticalPosition: "top",
            });
            this.dialogRef.close();
          } else {
            this.loginError = true;
          }
        });
    } else {
      this.loginError = true;
    }
  }

  onGoogleLogin() {
    this.dialogRef.close();
  }
}
