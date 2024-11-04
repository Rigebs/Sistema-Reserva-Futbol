import { Component } from "@angular/core";
import { MatToolbarModule } from "@angular/material/toolbar";

@Component({
  selector: "app-navbar",
  standalone: true,
  imports: [MatToolbarModule],
  templateUrl: "./navbar.component.html",
  styleUrl: "./navbar.component.css",
})
export class NavbarComponent {
  menuOpen = false;

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }
}
