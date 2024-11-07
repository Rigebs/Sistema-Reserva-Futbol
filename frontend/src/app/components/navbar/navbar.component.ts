import { Component, OnInit } from "@angular/core";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { MatToolbarModule } from "@angular/material/toolbar";
import { LoginComponent } from "../../auth/login/login.component";
import { AuthService } from "../../services/auth.service";
import { CommonModule } from "@angular/common";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Router } from "@angular/router";

@Component({
  selector: "app-navbar",
  standalone: true,
  imports: [MatToolbarModule, CommonModule],
  templateUrl: "./navbar.component.html",
  styleUrl: "./navbar.component.css",
})
export class NavbarComponent implements OnInit {
  menuOpen = false;
  currentUser: string | null = null;

  constructor(
    private dialog: MatDialog,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;
    });
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  openLoginDialog() {
    const isMobile = window.innerWidth <= 768;

    const dialogConfig = {
      minWidth: isMobile ? "" : "900px",
      minHeight: isMobile ? "" : "600px",
    };

    this.dialog.open(LoginComponent, dialogConfig);
  }

  logout() {
    this.authService.logout();
  }

  registrarSede() {
    this.router.navigate(["/registrar-sede"]);
  }

  openFieldOfferDialog() {
    if (!this.currentUser) {
      // Si el usuario no está autenticado, muestra el snackbar

      // Abre el diálogo de inicio de sesión
      this.openLoginDialog();
      this.snackBar.open(
        "Debes iniciar sesión para ofrecer tus campos",
        "Cerrar",
        {
          duration: 3000,
          verticalPosition: "top",
          horizontalPosition: "center",
        }
      );
    } else {
      this.registrarSede();
    }
  }
}
