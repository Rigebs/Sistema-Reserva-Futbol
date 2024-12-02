import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatToolbarModule } from "@angular/material/toolbar";
import { LoginComponent } from "../../auth/login/login.component";
import { AuthService } from "../../services/auth.service";
import { CommonModule } from "@angular/common";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Router } from "@angular/router";
import { AuthNotificationService } from "../../services/auth-notification.service";
import { AuthTokenUtil } from "../../utils/auth-token-util";

@Component({
  selector: "app-navbar-home",
  standalone: true,
  imports: [MatToolbarModule, CommonModule],
  templateUrl: "./navbar-home.component.html",
  styleUrl: "./navbar-home.component.css",
})
export class NavbarHomeComponent implements OnInit {
  menuOpen = false;
  currentUser: string | null = null;
  isCompania = false;
  adminUsername: string | null = null;

  isMobile = window.innerWidth <= 768;

  showDropdown = false;

  isEspera = false;

  isCliente = false;

  isAdmin = false;

  isAvalaible: boolean = false;

  constructor(
    private dialog: MatDialog,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router,
    private authNotificationService: AuthNotificationService,
    private authTokenUtil: AuthTokenUtil
  ) {}

  ngOnInit(): void {
    this.verifyToken();
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;
      if (user) {
        this.checkAdminRole();
        this.checkEsperaRole();
      } else {
        this.isCompania = false;
        this.adminUsername = null;
      }
    });

    this.authNotificationService.message$.subscribe((message) => {
      if (message === "Primero tienes que iniciar sesión") {
        this.openLoginDialog();
        this.snackBar.open(message, "Cerrar", {
          duration: 3000,
          verticalPosition: "top",
          horizontalPosition: "center",
        });
      }
    });
  }

  toggleDropdown() {
    this.showDropdown = !this.showDropdown; // Alterna el dropdown
  }

  editProfile() {
    const currentUrl = this.router.url;
    this.router.navigate(["/usuario/editar-perfil"], {
      queryParams: { from: currentUrl },
    });
  }

  viewReservations() {
    console.log("Ver Reservas");
    // Aquí puedes redirigir o mostrar las reservas del usuario
  }

  checkAdminRole() {
    const hasToken = this.authTokenUtil.hasToken();
    if (hasToken) {
      const payload = this.authTokenUtil.decodeToken();
      this.isCompania = this.authTokenUtil.isCompania();
      this.adminUsername = payload.sub || null;
    }
  }

  verifyToken() {
    this.isAvalaible = this.authTokenUtil.isTokenValid();
    if (!this.isAvalaible) {
      this.logout();
    }
  }

  checkEsperaRole() {
    const hasToken = this.authTokenUtil.hasToken();
    if (hasToken) {
      const payload = this.authTokenUtil.decodeToken();
      this.isEspera = this.authTokenUtil.isEspera();
      this.isCliente = this.authTokenUtil.isCliente();
      this.isAdmin = this.authTokenUtil.isAdmin();
      this.adminUsername = payload.sub || null;
    }
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  openLoginDialog() {
    const isMobile = window.innerWidth <= 768;
    const dialogConfig = {
      minWidth: isMobile ? "" : "900px",
      minHeight: isMobile ? "" : "500px",
    };

    this.dialog.open(LoginComponent, dialogConfig);
  }

  logout() {
    this.authService.logout();
    this.isCompania = false;
    this.isEspera = false;
    this.adminUsername = null;
  }

  registrarSede() {
    this.router.navigate(["/registrar-sede"]);
  }

  openFieldOfferDialog() {
    if (!this.currentUser) {
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

  goToAdminPanel() {
    if (this.adminUsername) {
      this.router.navigate([`/${this.adminUsername}/panel-admin`]);
    } else {
      console.error("No se encontró el nombre de usuario del administrador.");
    }
  }

  goToSupremPanel() {
    if (this.adminUsername) {
      this.router.navigate([`/${this.adminUsername}/panel-suprem`]);
    } else {
      console.error("No se encontró el nombre de usuario del administrador.");
    }
  }
}
