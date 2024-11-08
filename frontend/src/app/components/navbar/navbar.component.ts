import { Component, OnInit } from "@angular/core";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { MatToolbarModule } from "@angular/material/toolbar";
import { LoginComponent } from "../../auth/login/login.component";
import { AuthService } from "../../services/auth.service";
import { CommonModule } from "@angular/common";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Router } from "@angular/router";
import { AuthNotificationService } from "../../services/auth-notification.service";

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
  isAdmin = false;
  adminUsername: string | null = null;

  constructor(
    private dialog: MatDialog,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router,
    private authNotificationService: AuthNotificationService
  ) {}

  ngOnInit(): void {
    // Subscribe to authentication state
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;
      if (user) {
        // Check if the user has admin privileges after login
        this.checkAdminRole();
      } else {
        // Reset state when logged out
        this.isAdmin = false;
        this.adminUsername = null;
      }
    });

    this.authNotificationService.message$.subscribe((message) => {
      if (message === 'Primero tienes que iniciar sesión') {
        this.openLoginDialog();
        this.snackBar.open(message, 'Cerrar', {
          duration: 3000,
          verticalPosition: 'top',
          horizontalPosition: 'center',
        });
      }
    });
  }

  checkAdminRole() {
    const token = localStorage.getItem('authToken');
    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      console.log("PAU: ", payload);
      
      this.isAdmin = payload.roles && payload.roles.includes('ROLE_ADMIN');
      this.adminUsername = payload.sub || null;
    }
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  openLoginDialog() {
    const isMobile = window.innerWidth <= 768;
    const dialogConfig = {
      minWidth: isMobile ? '' : '900px',
      minHeight: isMobile ? '' : '600px',
    };

    this.dialog.open(LoginComponent, dialogConfig);
  }

  logout() {
    this.authService.logout();
    this.isAdmin = false;
    this.adminUsername = null;
  }

  registrarSede() {
    this.router.navigate(['/registrar-sede']);
  }

  openFieldOfferDialog() {
    if (!this.currentUser) {
      this.openLoginDialog();
      this.snackBar.open('Debes iniciar sesión para ofrecer tus campos', 'Cerrar', {
        duration: 3000,
        verticalPosition: 'top',
        horizontalPosition: 'center',
      });
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
}