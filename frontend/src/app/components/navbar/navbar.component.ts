import { Component, OnInit } from "@angular/core";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { MatToolbarModule } from "@angular/material/toolbar";
import { LoginComponent } from "../../auth/login/login.component";
import { AuthService } from "../../services/auth.service";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-navbar",
  standalone: true,
  imports: [MatToolbarModule, MatDialogModule, CommonModule],
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
    this.dialog.open(LoginComponent, {
      width: "380px",
    });
  }

  logout() {
    this.authService.logout();
  }
}
