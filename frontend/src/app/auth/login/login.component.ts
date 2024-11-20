import { CommonModule } from "@angular/common";
import { Component, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogRef } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { AuthService } from "../../services/auth.service";
import { catchError, of } from "rxjs";
import { Usuario } from "../../models/usuario";
import { MatSnackBar } from "@angular/material/snack-bar";
import { UsuarioRegistro } from "../../models/usuario-registro";
import { Verify } from "../../models/verify-code";
import { PasswordResetRequest } from "../../models/password-reset-request";
import { PasswordReset } from "../../models/password-reset";
import { MatIconModule } from "@angular/material/icon";

@Component({
  selector: "app-login",
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatButtonModule,
    FormsModule,
    MatInputModule,
    CommonModule,
    MatIconModule,
  ],
  templateUrl: "./login.component.html",
  styleUrl: "./login.component.css",
})
export class LoginComponent {
  isLogin: boolean = true; // Alternar entre login y registro
  isVerification: boolean = false; // Estado para mostrar la verificación de código
  isPasswordReset: boolean = false; // Estado para la recuperación de contraseña
  isPasswordChange: boolean = false; // Estado para el cambio de contraseña
  loginUser: Usuario = { identifier: "", password: "" };
  registerUser: UsuarioRegistro = { email: "", password: "", username: "" };
  verificationCode: string = ""; // Código de verificación de 6 dígitos
  loginError: boolean = false;
  resetPasswordEmail: string = ""; // Correo para solicitar el restablecimiento de contraseña
  resetPassword: PasswordReset = {
    email: "",
    verificationCode: "",
    newPassword: "",
  };

  usernamePattern: RegExp = /^[a-zA-Z0-9]+$/;

  isUsernameValid: boolean = true;

  constructor(
    public dialogRef: MatDialogRef<LoginComponent>,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  onLogin() {
    this.loginError = false;
    if (this.loginUser.identifier && this.loginUser.password) {
      this.authService
        .login(this.loginUser)
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

  onRegister() {
    this.isUsernameValid = this.usernamePattern.test(
      this.registerUser.username
    );

    if (!this.isUsernameValid) {
      this.snackBar.open(
        "El nombre de usuario solo debe contener letras y números, sin espacios.",
        "Cerrar",
        {
          duration: 3000,
          horizontalPosition: "right",
          verticalPosition: "top",
        }
      );
      return;
    }

    this.authService.register(this.registerUser).subscribe({
      next: () => {
        this.snackBar.open(
          "Registro exitoso, por favor verifica tu email",
          "Cerrar",
          {
            duration: 3000,
            horizontalPosition: "right",
            verticalPosition: "top",
          }
        );
        this.isVerification = true;
      },
      error: (error) => {
        this.snackBar.open(`Error: ${error.message}`, "Cerrar", {
          duration: 3000,
          horizontalPosition: "right",
          verticalPosition: "top",
        });
        console.error("Error capturado en onRegister:", error.message);
      },
    });
  }
  onVerifyCode() {
    const verify: Verify = {
      email: this.registerUser.email,
      verificationCode: this.verificationCode,
    };

    this.authService.verifyCode(verify).subscribe({
      next: (response) => {
        this.snackBar.open("Código verificado exitosamente", "Cerrar", {
          duration: 3000,
          horizontalPosition: "right",
          verticalPosition: "top",
        });
        this.isVerification = false; // Oculta la verificación del código
        this.isLogin = true; // Regresa al formulario de inicio de sesión
      },
      error: (error) => {
        this.snackBar.open(`Error: ${error.message}`, "Cerrar", {
          duration: 3000,
          horizontalPosition: "right",
          verticalPosition: "top",
        });
        console.error("Error al verificar el código:", error.message);
      },
    });
  }

  onResendVerificationCode() {
    this.authService.resendVerificationCode(this.registerUser.email).subscribe({
      next: (response) => {
        this.snackBar.open(
          "El código de verificación ha sido reenviado",
          "Cerrar",
          {
            duration: 3000,
            horizontalPosition: "right",
            verticalPosition: "top",
          }
        );
      },
      error: (error) => {
        this.snackBar.open("Ocurrio un error", "Cerrar", {
          duration: 3000,
          horizontalPosition: "right",
          verticalPosition: "top",
        });
        console.error("Error al reenviar código:", error.message);
      },
    });
  }

  onResetPassword() {
    this.isPasswordReset = true;
    this.isLogin = false;
  }

  onRequestPasswordReset() {
    if (this.resetPasswordEmail) {
      const resetRequest: PasswordResetRequest = {
        email: this.resetPasswordEmail,
      };

      this.authService.requestPasswordReset(resetRequest).subscribe({
        next: (response) => {
          this.snackBar.open(
            "Si el correo existe, se ha enviado un código de verificación",
            "Cerrar",
            {
              duration: 3000,
              horizontalPosition: "right",
              verticalPosition: "top",
            }
          );
          // Aquí copiamos el correo de resetPasswordEmail a resetPassword.email
          this.resetPassword.email = this.resetPasswordEmail;
          this.isPasswordChange = true; // Pasamos a la pantalla de cambio de contraseña
          this.isPasswordReset = false;
        },
        error: (error) => {
          this.snackBar.open(`Error: ${error.message}`, "Cerrar", {
            duration: 3000,
            horizontalPosition: "right",
            verticalPosition: "top",
          });
          console.error(
            "Error al solicitar restablecimiento de contraseña:",
            error.message
          );
        },
      });
    } else {
      this.snackBar.open("Por favor ingresa tu correo", "Cerrar", {
        duration: 3000,
        horizontalPosition: "right",
        verticalPosition: "top",
      });
    }
  }

  onChangePassword() {
    if (
      this.resetPassword.email &&
      this.resetPassword.verificationCode &&
      this.resetPassword.newPassword
    ) {
      this.authService.resetPassword(this.resetPassword).subscribe({
        next: (response) => {
          this.snackBar.open("Contraseña cambiada exitosamente", "Cerrar", {
            duration: 3000,
            horizontalPosition: "right",
            verticalPosition: "top",
          });

          // Cambiar a la vista de inicio de sesión
          this.isLogin = true; // Asegura que se muestre el formulario de inicio de sesión
          this.isPasswordChange = false; // Reinicia el estado de cambio de contraseña

          // Reiniciar los campos de restablecimiento de contraseña
          this.resetPassword = {
            email: "",
            verificationCode: "",
            newPassword: "",
          };
        },
        error: (error) => {
          this.snackBar.open(`Error: ${error.message}`, "Cerrar", {
            duration: 3000,
            horizontalPosition: "right",
            verticalPosition: "top",
          });
          console.error("Error al cambiar la contraseña:", error.message);
        },
      });
    } else {
      this.snackBar.open("Por favor completa todos los campos", "Cerrar", {
        duration: 3000,
        horizontalPosition: "right",
        verticalPosition: "top",
      });
    }
  }

  onIdentifierInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    // Permitir solo letras, números, puntos y arroba
    input.value = input.value.replace(/[^a-zA-Z0-9@.]/g, "");
    this.loginUser.identifier = input.value; // Actualizar el valor de loginUser.identifier
  }

  onUsuarioInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^a-zA-Z0-9]/g, "");
    this.loginUser.identifier = input.value; // Actualizar el valor de loginUser.identifier
  }

  toggleForm() {
    this.isLogin = !this.isLogin;
    this.isVerification = false;
    this.isPasswordReset = false;
    this.isPasswordChange = false; // Reinicia el estado de cambio de contraseña
  }

  hide = signal(true);
  clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }
}
