import { Component, OnInit } from "@angular/core";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { MatToolbarModule } from "@angular/material/toolbar";
import { LoginComponent } from "../../auth/login/login.component";
import { AuthService } from "../../services/auth.service";
import { CommonModule } from "@angular/common";

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

  constructor(private dialog: MatDialog, private authService: AuthService) {}

  ngOnInit() {
    this.authService.currentUser$.subscribe((user) => {
      this.currentUser = user;
    });
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  openLoginDialog() {
    // Verifica el tamaño de la pantalla
    const isMobile = window.innerWidth <= 768;

    // Configura el tamaño del diálogo según el tipo de dispositivo
    const dialogConfig = {
      minWidth: isMobile ? "" : "900px", // En móvil, el ancho es el 100% de la pantalla
      minHeight: isMobile ? "auto" : "600px", // En móvil, la altura puede ser 'auto' o ajustada según el contenido
    };

    // Abre el diálogo con las configuraciones ajustadas
    this.dialog.open(LoginComponent, dialogConfig);
  }

  logout() {
    this.authService.logout();
  }
}
